package openblocks.common.tileentity;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import openblocks.OpenBlocks;
import openblocks.client.renderer.block.canvas.CanvasState;
import openblocks.common.StencilPattern;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemSqueegee;
import openblocks.common.item.ItemStencil;
import openblocks.common.sync.SyncableBlockLayers;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomBreakDrops;
import openmods.api.ICustomHarvestDrops;
import openmods.sync.SyncMap;
import openmods.sync.SyncableBlockState;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityCanvas extends SyncedTileEntity implements IActivateAwareTile, ICustomBreakDrops, ICustomHarvestDrops {

	public static class UnpackingBlockAccess implements IBlockAccess {

		private final World original;

		public UnpackingBlockAccess(World original) {
			this.original = original;
		}

		@Override
		@Nullable
		public TileEntity getTileEntity(BlockPos pos) {
			final TileEntity te = original.getTileEntity(pos);
			return te instanceof TileEntityCanvas? null : te;
		}

		@Override
		public int getCombinedLight(BlockPos pos, int lightValue) {
			return original.getCombinedLight(pos, lightValue);
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			final TileEntity te = original.getTileEntity(pos);
			if (te instanceof TileEntityCanvas) return ((TileEntityCanvas)te).getPaintedBlockState();

			return original.getBlockState(pos);
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			final BlockState state = getBlockState(pos);
			return state.getBlock().isAir(state, this, pos);
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return original.getBiome(pos);
		}

		@Override
		public int getStrongPower(BlockPos pos, Direction direction) {
			return original.getStrongPower(pos, direction);
		}

		@Override
		public WorldType getWorldType() {
			return original.getWorldType();
		}

		@Override
		public boolean isSideSolid(BlockPos pos, Direction side, boolean _default) {
			final Chunk chunk = original.getChunkFromBlockCoords(pos);
			if (chunk == null || chunk.isEmpty()) return _default;

			final BlockState state = getBlockState(pos);
			return state.isSideSolid(this, pos, side);
		}

	}

	private SyncableBlockState paintedBlockState;

	private BlockState rawPaintedBlockState;

	private int prevLightValue;

	private int prevLightOpacity;

	private BlockState actualPaintedBlockState;

	@SuppressWarnings("unused")
	private SyncableBlockLayers stencilsUp;

	@SuppressWarnings("unused")
	private SyncableBlockLayers stencilsDown;

	@SuppressWarnings("unused")
	private SyncableBlockLayers stencilsEast;

	@SuppressWarnings("unused")
	private SyncableBlockLayers stencilsWest;

	@SuppressWarnings("unused")
	private SyncableBlockLayers stencilsNorth;

	@SuppressWarnings("unused")
	private SyncableBlockLayers stencilsSouth;

	private Map<Direction, SyncableBlockLayers> allSides;

	private CanvasState canvasState = CanvasState.EMPTY;

	public TileEntityCanvas() {}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(changes -> {
			boolean stateChanged = false;

			if (changes.contains(paintedBlockState)) {
				onPaintedBlockUpdate();
				stateChanged = true;
			}

			for (Map.Entry<Direction, SyncableBlockLayers> e : allSides.entrySet()) {
				final SyncableBlockLayers side = e.getValue();
				if (changes.contains(side)) {
					canvasState = canvasState.update(e.getKey(), side.convertToState());
					stateChanged = true;
				}
			}

			if (stateChanged)
				markBlockForRenderUpdate(getPos());
		});
	}

	private SyncableBlockLayers createLayer(Direction facing) {
		SyncableBlockLayers result = new SyncableBlockLayers();
		allSides.put(facing, result);
		return result;
	}

	@Override
	protected void createSyncedFields() {
		allSides = Maps.newEnumMap(Direction.class);

		stencilsUp = createLayer(Direction.UP);
		stencilsDown = createLayer(Direction.DOWN);
		stencilsEast = createLayer(Direction.EAST);
		stencilsWest = createLayer(Direction.WEST);
		stencilsNorth = createLayer(Direction.NORTH);
		stencilsSouth = createLayer(Direction.SOUTH);

		paintedBlockState = new SyncableBlockState();
	}

	public SyncableBlockLayers getLayersForSide(Direction side) {
		return allSides.get(side);
	}

	public BlockState getPaintedBlockState() {
		if (rawPaintedBlockState == null) {
			rawPaintedBlockState = paintedBlockState.getValue();
		}

		return rawPaintedBlockState;
	}

	public BlockState getActualPaintedBlockState() {
		if (actualPaintedBlockState == null) {
			final BlockState rawBlockState = getPaintedBlockState();
			try {
				actualPaintedBlockState = rawBlockState.getActualState(new UnpackingBlockAccess(getWorld()), getPos());
			} catch (Exception e) {
				// best effort, see?
				actualPaintedBlockState = rawBlockState;
			}
		}

		return actualPaintedBlockState;
	}

	private boolean isBlockUnpainted() {
		for (Direction side : Direction.VALUES)
			if (!allSides.get(side).isEmpty()) return false;

		return true;
	}

	public boolean applyPaint(int color, Direction... sides) {
		boolean hasChanged = false;

		for (Direction side : sides) {
			SyncableBlockLayers layers = getLayersForSide(side);
			layers.applyPaint(color);

			hasChanged |= layers.isDirty();
		}

		trySync();
		return hasChanged;
	}

	private void dropStackFromSide(@Nonnull ItemStack stack, Direction side) {
		if (world.isRemote) return;
		BlockUtils.dropItemStackInWorld(world, pos.offset(side), stack);
	}

	public void removePaint(Direction... sides) {
		for (Direction side : sides) {
			SyncableBlockLayers layer = getLayersForSide(side);

			final Optional<StencilPattern> stencil = layer.clearAll();
			if (stencil.isPresent() && OpenBlocks.Items.stencil != null) {
				ItemStack dropStack = ItemStencil.createItemStack(OpenBlocks.Items.stencil, stencil.get());
				dropStackFromSide(dropStack, side);
			}
		}

		if (isBlockUnpainted() && !paintedBlockState.isAir()) {
			final BlockState state = paintedBlockState.getValue();
			world.setBlockState(pos, state);
		}

		trySync();
	}

	public boolean useStencil(Direction side, StencilPattern stencil) {
		SyncableBlockLayers layer = getLayersForSide(side);
		layer.putStencil(stencil);

		trySync();
		return true;
	}

	@Override
	public boolean onBlockActivated(PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (hand != Hand.MAIN_HAND) return false;

		final ItemStack held = player.getHeldItemMainhand();
		final Item heldItem = held.getItem();
		// logic is placed on item side
		if (heldItem instanceof ItemSqueegee || heldItem instanceof ItemPaintBrush || heldItem instanceof ItemStencil) return false;

		SyncableBlockLayers layer = getLayersForSide(side);

		boolean result = false;
		if (player.isSneaking()) {
			if (!world.isRemote) {
				final Optional<StencilPattern> stencil = layer.popStencil();
				if (stencil.isPresent() && OpenBlocks.Items.stencil != null) {
					ItemStack dropStack = ItemStencil.createItemStack(OpenBlocks.Items.stencil, stencil.get());
					dropStackFromSide(dropStack, side);
					result = true;
				}
			}
		} else {
			result = layer.rotateCover();
		}

		trySync();
		return result;
	}

	@Override
	public List<ItemStack> getDrops(List<ItemStack> drops) {
		for (SyncableBlockLayers sideLayers : allSides.values()) {
			final Optional<StencilPattern> stencil = sideLayers.peekStencil();
			if (stencil.isPresent() && OpenBlocks.Items.stencil != null) {
				drops.add(ItemStencil.createItemStack(OpenBlocks.Items.stencil, stencil.get()));
			}
		}

		return drops;
	}

	@Override
	public boolean suppressBlockHarvestDrops() {
		return !paintedBlockState.isAir();
	}

	@Override
	public void addHarvestDrops(PlayerEntity player, List<ItemStack> drops, BlockState blockState, int fortune, boolean isSilkHarvest) {
		if (!paintedBlockState.isAir()) {
			final BlockState state = this.paintedBlockState.getValue();
			final Block paintedBlock = state.getBlock();

			final Random rand = world.rand;

			int count = paintedBlock.quantityDropped(state, fortune, rand);
			int damageDropped = paintedBlock.damageDropped(state);

			for (int i = 0; i < count; i++) {
				Item item = paintedBlock.getItemDropped(state, rand, fortune);
				if (item != Items.AIR) drops.add(new ItemStack(item, 1, damageDropped));

			}
		}
	}

	public void setPaintedBlock(BlockState state) {
		paintedBlockState.setValue(state);
		onPaintedBlockUpdate();
	}

	private void onPaintedBlockUpdate() {
		rawPaintedBlockState = null;
		actualPaintedBlockState = null;
		updateLight();
	}

	@SuppressWarnings("deprecation")
	private void updateLight() {
		final BlockState paintedBlockStatek = getPaintedBlockState();
		final Block paintedBlock = paintedBlockStatek.getBlock();
		final int newLightValue = paintedBlock.getLightValue(paintedBlockStatek);
		final int newLightOpacity = paintedBlock.getLightOpacity(paintedBlockStatek);
		if (newLightValue != prevLightValue || newLightOpacity != prevLightOpacity) {
			world.checkLight(pos);
			prevLightOpacity = newLightOpacity;
			prevLightValue = newLightValue;
		}
	}

	public CanvasState getCanvasState() {
		return canvasState;
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		canvasState.release();
		canvasState = CanvasState.EMPTY;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		canvasState.release();
		canvasState = CanvasState.EMPTY;
	}
}