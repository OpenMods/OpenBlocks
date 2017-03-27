package openblocks.common.tileentity;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.common.Stencil;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemSqueegee;
import openblocks.common.sync.SyncableBlockLayers;
import openblocks.common.sync.SyncableBlockLayers.Layer;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomBreakDrops;
import openmods.api.ICustomHarvestDrops;
import openmods.sync.SyncableBlock;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableIntArray;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityCanvas extends SyncedTileEntity implements IActivateAwareTile, ICustomBreakDrops, ICustomHarvestDrops {

	/* Used for painting other blocks */
	private SyncableBlock paintedBlock;
	private SyncableInt paintedBlockMeta;

	private IBlockState paintedBlockState;

	private SyncableIntArray baseColors;

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

	private Map<EnumFacing, SyncableBlockLayers> allSides = Maps.newEnumMap(EnumFacing.class);

	public TileEntityCanvas() {
		syncMap.addUpdateListener(createRenderUpdateListener());
	}

	public SyncableIntArray getBaseColors() {
		return baseColors;
	}

	private SyncableBlockLayers createLayer(EnumFacing facing) {
		SyncableBlockLayers result = new SyncableBlockLayers();
		allSides.put(facing, result);
		return result;
	}

	@Override
	protected void createSyncedFields() {
		stencilsUp = createLayer(EnumFacing.UP);
		stencilsDown = createLayer(EnumFacing.DOWN);
		stencilsEast = createLayer(EnumFacing.EAST);
		stencilsWest = createLayer(EnumFacing.WEST);
		stencilsNorth = createLayer(EnumFacing.NORTH);
		stencilsSouth = createLayer(EnumFacing.SOUTH);

		baseColors = new SyncableIntArray(new int[] { 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF });
		paintedBlock = new SyncableBlock();
		paintedBlockMeta = new SyncableInt(0);
	}

	public SyncableBlockLayers getLayersForSide(EnumFacing side) {
		return allSides.get(side);
	}

	public Layer getLayerForSide(EnumFacing renderSide, int layerId) {
		final SyncableBlockLayers layers = getLayersForSide(renderSide);
		return layers != null? layers.getLayer(layerId) : null;
	}

	private int getBaseColor(EnumFacing renderSide) {
		return baseColors.getValue(renderSide.ordinal());
	}

	private void setBaseColor(EnumFacing renderSide, int color) {
		baseColors.setValue(renderSide.ordinal(), color);
	}

	public int getColorForRender(EnumFacing renderSide, int layerId) {
		if (layerId == 0) return getBaseColor(renderSide);
		final Layer layer = getLayerForSide(renderSide, layerId);
		return layer != null? layer.getColorForRender() : 0xCCCCCC;
	}

	// TODO 1.8.9 temporary, will be aligned once I figure rendering
	public ResourceLocation getTextureForRender(EnumFacing renderSide, int layerId) {
		Layer layer = getLayerForSide(renderSide, layerId);
		if (layer != null) {
			Stencil stencil = layer.getStencil();
			if (stencil != null) { return layer.hasStencilCover()? stencil.coverBlockIcon : stencil.blockIcon; }
		}

		return null;
	}

	// TODO 1.8.9 possibly use for render?
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
		for (EnumFacing side : EnumFacing.VALUES) {
			if (!allSides.get(side).isEmpty() || getBaseColor(side) != 0xFFFFFF) return false;
		}
		return true;
	}

	public boolean applyPaint(int color, EnumFacing... sides) {
		boolean hasChanged = false;

		for (EnumFacing side : sides) {
			SyncableBlockLayers layers = getLayersForSide(side);
			if (layers.isLastLayerStencil()) {
				layers.setLastLayerColor(color);
				layers.moveStencilToNextLayer();
			} else {
				// collapse all layers, since they will be fully covered by paint
				layers.clear();
				setBaseColor(side, color);
			}

			hasChanged |= layers.isDirty();
		}

		hasChanged |= baseColors.isDirty();

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

			// If there is a stencil on top, pop it off.
			if (layer.isLastLayerStencil()) {
				Stencil stencil = layer.getTopStencil();
				ItemStack dropStack = new ItemStack(OpenBlocks.Items.stencil, 1, stencil.ordinal());
				dropStackFromSide(dropStack, side);
			}

			layer.clear();

			setBaseColor(side, 0xFFFFFF);
		}

		if (isBlockUnpainted() && paintedBlock.containsValidBlock()) {
			final Block block = paintedBlock.getValue();
			final IBlockState state = block.getStateFromMeta(paintedBlockMeta.get());
			worldObj.setBlockState(pos, state);
		}

		if (!worldObj.isRemote) sync();
	}

	public boolean useStencil(EnumFacing side, Stencil stencil) {
		SyncableBlockLayers layer = getLayersForSide(side);
		if (layer.isLastLayerStencil()) {
			Stencil topStencil = layer.getTopStencil();
			if (topStencil == stencil) return false;

			ItemStack dropStack = new ItemStack(OpenBlocks.Items.stencil, 1, topStencil.ordinal());
			dropStackFromSide(dropStack, side);
			layer.setLastLayerStencil(stencil);
		} else layer.pushNewStencil(stencil);

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

		if (layer.isLastLayerStencil()) {
			if (player.isSneaking()) {
				if (!worldObj.isRemote) {
					ItemStack dropStack = new ItemStack(OpenBlocks.Items.stencil, 1, layer.getTopStencil().ordinal());
					dropStackFromSide(dropStack, side);
				}
				layer.removeCover();
			} else getLayersForSide(side).rotateCover();

			if (!worldObj.isRemote) sync();
			return true;
		}

		return false;
	}

	@Override
	public void addDrops(List<ItemStack> drops) {
		for (SyncableBlockLayers sideLayers : allSides.values()) {
			if (sideLayers.isLastLayerStencil()) {
				Stencil stencil = sideLayers.getTopStencil();
				if (stencil != null) drops.add(new ItemStack(OpenBlocks.Items.stencil, 1, stencil.ordinal()));
			}
		}
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

	public void setPaintedBlockBlock(Block block, int meta) {
		paintedBlock.setValue(block);
		paintedBlockMeta.set(meta);
	}
}