package openblocks.common.tileentity;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
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
		public IBlockState getBlockState(BlockPos pos) {
			final TileEntity te = original.getTileEntity(pos);
			if (te instanceof TileEntityCanvas) return ((TileEntityCanvas)te).getPaintedBlockState();

			return original.getBlockState(pos);
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			final IBlockState state = getBlockState(pos);
			return state.getBlock().isAir(state, this, pos);
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return original.getBiome(pos);
		}

		@Override
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return original.getStrongPower(pos, direction);
		}

		@Override
		public WorldType getWorldType() {
			return original.getWorldType();
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
			final Chunk chunk = original.getChunkFromBlockCoords(pos);
			if (chunk == null || chunk.isEmpty()) return _default;

			final IBlockState state = getBlockState(pos);
			return state.getBlock().isSideSolid(state, this, pos, side);
		}

	}

	private SyncableBlockState paintedBlockState;

	private IBlockState rawPaintedBlockState;

	private int prevLightValue;

	private int prevLightOpacity;

	private IBlockState actualPaintedBlockState;

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

	private Map<EnumFacing, SyncableBlockLayers> allSides;

	private CanvasState canvasState = CanvasState.EMPTY;

	public TileEntityCanvas() {}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				boolean stateChanged = false;

				if (changes.contains(paintedBlockState)) {
					onPaintedBlockUpdate();
					stateChanged = true;
				}

				for (Map.Entry<EnumFacing, SyncableBlockLayers> e : allSides.entrySet()) {
					final SyncableBlockLayers side = e.getValue();
					if (changes.contains(side)) {
						canvasState = canvasState.update(e.getKey(), side.convertToState());
						stateChanged = true;
					}
				}

				if (stateChanged)
					markBlockForRenderUpdate(getPos());
			}
		});
	}

	private SyncableBlockLayers createLayer(EnumFacing facing) {
		SyncableBlockLayers result = new SyncableBlockLayers();
		allSides.put(facing, result);
		return result;
	}

	@Override
	protected void createSyncedFields() {
		allSides = Maps.newEnumMap(EnumFacing.class);

		stencilsUp = createLayer(EnumFacing.UP);
		stencilsDown = createLayer(EnumFacing.DOWN);
		stencilsEast = createLayer(EnumFacing.EAST);
		stencilsWest = createLayer(EnumFacing.WEST);
		stencilsNorth = createLayer(EnumFacing.NORTH);
		stencilsSouth = createLayer(EnumFacing.SOUTH);

		paintedBlockState = new SyncableBlockState();
	}

	public SyncableBlockLayers getLayersForSide(EnumFacing side) {
		return allSides.get(side);
	}

	public IBlockState getPaintedBlockState() {
		if (rawPaintedBlockState == null) {
			rawPaintedBlockState = paintedBlockState.getValue();
		}

		return rawPaintedBlockState;
	}

	public IBlockState getActualPaintedBlockState() {
		if (actualPaintedBlockState == null) {
			final IBlockState rawBlockState = getPaintedBlockState();
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
		for (EnumFacing side : EnumFacing.VALUES)
			if (!allSides.get(side).isEmpty()) return false;

		return true;
	}

	public boolean applyPaint(int color, EnumFacing... sides) {
		boolean hasChanged = false;

		for (EnumFacing side : sides) {
			SyncableBlockLayers layers = getLayersForSide(side);
			layers.applyPaint(color);

			hasChanged |= layers.isDirty();
		}

		trySync();
		return hasChanged;
	}

	private void dropStackFromSide(ItemStack stack, EnumFacing side) {
		if (worldObj.isRemote) return;
		BlockUtils.dropItemStackInWorld(worldObj, pos.offset(side), stack);
	}

	public void removePaint(EnumFacing... sides) {
		for (EnumFacing side : sides) {
			SyncableBlockLayers layer = getLayersForSide(side);

			final Optional<StencilPattern> stencil = layer.clearAll();
			if (stencil.isPresent()) {
				ItemStack dropStack = new ItemStack(OpenBlocks.Items.stencil, 1, stencil.get().ordinal());
				dropStackFromSide(dropStack, side);
			}
		}

		if (isBlockUnpainted() && !paintedBlockState.isAir()) {
			final IBlockState state = paintedBlockState.getValue();
			worldObj.setBlockState(pos, state);
		}

		trySync();
	}

	public boolean useStencil(EnumFacing side, StencilPattern stencil) {
		SyncableBlockLayers layer = getLayersForSide(side);
		layer.putStencil(stencil);

		trySync();
		return true;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumHand hand, ItemStack held, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (hand != EnumHand.MAIN_HAND) return false;

		if (held != null) {
			final Item heldItem = held.getItem();
			// logic is placed on item side
			if (heldItem instanceof ItemSqueegee || heldItem instanceof ItemPaintBrush || heldItem instanceof ItemStencil) return false;
		}

		SyncableBlockLayers layer = getLayersForSide(side);

		boolean result = false;
		if (player.isSneaking()) {
			if (!worldObj.isRemote) {
				final Optional<StencilPattern> stencil = layer.popStencil();
				if (stencil.isPresent()) {
					ItemStack dropStack = OpenBlocks.Items.stencil.createItemStack(stencil.get());
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
			if (stencil.isPresent()) {
				drops.add(OpenBlocks.Items.stencil.createItemStack(stencil.get()));
			}
		}

		return drops;
	}

	@Override
	public boolean suppressBlockHarvestDrops() {
		return !paintedBlockState.isAir();
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops, IBlockState blockState, int fortune, boolean isSilkHarvest) {
		if (!paintedBlockState.isAir()) {
			final IBlockState state = this.paintedBlockState.getValue();
			final Block paintedBlock = state.getBlock();

			final Random rand = worldObj.rand;

			int count = paintedBlock.quantityDropped(state, fortune, rand);
			int damageDropped = paintedBlock.damageDropped(state);

			for (int i = 0; i < count; i++) {
				Item item = paintedBlock.getItemDropped(state, rand, fortune);
				if (item != null) drops.add(new ItemStack(item, 1, damageDropped));

			}
		}
	}

	public void setPaintedBlock(IBlockState state) {
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
		final IBlockState paintedBlockStatek = getPaintedBlockState();
		final Block paintedBlock = paintedBlockStatek.getBlock();
		final int newLightValue = paintedBlock.getLightValue(paintedBlockStatek);
		final int newLightOpacity = paintedBlock.getLightOpacity(paintedBlockStatek);
		if (newLightValue != prevLightValue || newLightOpacity != prevLightOpacity) {
			worldObj.checkLight(pos);
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