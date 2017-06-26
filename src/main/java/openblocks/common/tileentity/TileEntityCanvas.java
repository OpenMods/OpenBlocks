package openblocks.common.tileentity;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import openblocks.OpenBlocks;
import openblocks.client.renderer.block.canvas.CanvasState;
import openblocks.common.StencilPattern;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemSqueegee;
import openblocks.common.sync.SyncableBlockLayers;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomBreakDrops;
import openmods.api.ICustomHarvestDrops;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncMap;
import openmods.sync.SyncableBlock;
import openmods.sync.SyncableInt;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityCanvas extends SyncedTileEntity implements IActivateAwareTile, ICustomBreakDrops, ICustomHarvestDrops {

	/* Used for painting other blocks */
	private SyncableBlock paintedBlock;
	private SyncableInt paintedBlockMeta;

	private IBlockState paintedBlockState;

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

				if (changes.contains(paintedBlock) || changes.contains(paintedBlockMeta)) {
					paintedBlockState = null;
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

		paintedBlock = new SyncableBlock();
		paintedBlockMeta = new SyncableInt(0);
	}

	public SyncableBlockLayers getLayersForSide(EnumFacing side) {
		return allSides.get(side);
	}

	public IBlockState getPaintedBlockState() {
		if (paintedBlockState == null) {
			final Block block = paintedBlock.getValue();
			if (block != Blocks.AIR) {
				// TODO 1.10 switch storage to state
				paintedBlockState = block.getStateFromMeta(paintedBlockMeta.get());
			} else {
				paintedBlockState = OpenBlocks.Blocks.canvas.getDefaultState();
			}
		}

		return paintedBlockState;
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

		if (!worldObj.isRemote) sync();
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

		if (isBlockUnpainted() && paintedBlock.containsValidBlock()) {
			final Block block = paintedBlock.getValue();
			final IBlockState state = block.getStateFromMeta(paintedBlockMeta.get());
			worldObj.setBlockState(pos, state);
		}

		if (!worldObj.isRemote) sync();
	}

	public boolean useStencil(EnumFacing side, StencilPattern stencil) {
		SyncableBlockLayers layer = getLayersForSide(side);
		layer.putStencil(stencil);

		if (!worldObj.isRemote) sync();
		return true;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack held = player.getHeldItemMainhand();
		if (held != null) {
			Item heldItem = held.getItem();
			if (heldItem instanceof ItemSqueegee || heldItem instanceof ItemPaintBrush || heldItem == OpenBlocks.Items.stencil) return false;
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

		if (!worldObj.isRemote) sync();
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
		return paintedBlock.containsValidBlock();
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops, IBlockState blockState, int fortune, boolean isSilkHarvest) {
		if (paintedBlock.containsValidBlock()) {
			final Block paintedBlock = this.paintedBlock.getValue();
			final IBlockState state = paintedBlock.getStateFromMeta(paintedBlockMeta.get());

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
		final Block block = state.getBlock();
		final int meta = block.getMetaFromState(state);

		paintedBlock.setValue(block);
		paintedBlockMeta.set(meta);
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