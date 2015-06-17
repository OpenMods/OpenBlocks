package openblocks.common.tileentity;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.Stencil;
import openblocks.common.block.BlockCanvas;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemSqueegee;
import openblocks.common.item.ItemStencil;
import openblocks.common.sync.SyncableBlockLayers;
import openblocks.common.sync.SyncableBlockLayers.Layer;
import openmods.api.IActivateAwareTile;
import openmods.api.ICustomBreakDrops;
import openmods.api.ICustomHarvestDrops;
import openmods.sync.SyncableBlock;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableIntArray;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.BlockUtils;

import net.minecraft.world.IBlockAccess;

public class TileEntityCanvas extends SyncedTileEntity implements IActivateAwareTile, ICustomBreakDrops, ICustomHarvestDrops {

	public static final int[] ALL_SIDES = { 0, 1, 2, 3, 4, 5 };

	/* Used for painting other blocks */
	private SyncableBlock paintedBlock;
	private SyncableInt paintedBlockMeta;

	private SyncableIntArray baseColors;

	private SyncableBlockLayers stencilsUp;
	private SyncableBlockLayers stencilsDown;
	private SyncableBlockLayers stencilsEast;
	private SyncableBlockLayers stencilsWest;
	private SyncableBlockLayers stencilsNorth;
	private SyncableBlockLayers stencilsSouth;

	private SyncableBlockLayers[] allSides;

	public TileEntityCanvas() {
		syncMap.addUpdateListener(createRenderUpdateListener());
	}

	public SyncableIntArray getBaseColors() {
		return baseColors;
	}

	@Override
	protected void createSyncedFields() {
		stencilsUp = new SyncableBlockLayers();
		stencilsDown = new SyncableBlockLayers();
		stencilsEast = new SyncableBlockLayers();
		stencilsWest = new SyncableBlockLayers();
		stencilsNorth = new SyncableBlockLayers();
		stencilsSouth = new SyncableBlockLayers();
		allSides = new SyncableBlockLayers[] {
				stencilsDown, stencilsUp, stencilsNorth, stencilsSouth, stencilsWest, stencilsEast
		};
		baseColors = new SyncableIntArray(new int[] { 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF });
		paintedBlock = new SyncableBlock();
		paintedBlockMeta = new SyncableInt(0);
	}

	public SyncableBlockLayers getLayersForSide(int side) {
		return allSides[side];
	}

	public Layer getLayerForSide(int renderSide, int layerId) {
		final SyncableBlockLayers layers = getLayersForSide(renderSide);
		return layers != null? layers.getLayer(layerId) : null;
	}
	
	public void setDefaultColors(int defaultColor){
	}

	public int getColorForRender(int renderSide, int layerId) {
		if (layerId == BlockCanvas.BASE_LAYER) return baseColors.getValue(renderSide);
		final Layer layer = getLayerForSide(renderSide, layerId);
		return layer != null? layer.getColorForRender() : 0xFEFEFE;
	}

	public IIcon getTextureForRender(int renderSide, int layerId) {
		if (layerId > BlockCanvas.BASE_LAYER) {
			Layer layer = getLayerForSide(renderSide, layerId);
			if (layer != null) {
				Stencil stencil = layer.getStencil();
				if (stencil != null) { return layer.hasStencilCover()? stencil.getCoverBlockIcon() : stencil.getBlockIcon(); }
			}
		}
		return getBaseTexture(renderSide);
	}

	private IIcon getBaseTexture(int side) {
		Block block = paintedBlock.getValue();
		if (block == Blocks.air) return OpenBlocks.Blocks.canvas.baseIcon;
		return block.getIcon(side, paintedBlockMeta.get());
	}
	
	public Block getPaintedBlock() {
		return paintedBlock.getValue();
	}
	
	public int getPaintedBlockMeta() {
		return paintedBlockMeta.get();
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	private boolean isBlockUnpainted() {
		for (int i = 0; i < allSides.length; i++) {
			if (!allSides[i].isEmpty() || baseColors.getValue(i) != 0xFFFFFF) return false;
		}
		return true;
	}

	public boolean applyPaint(int color, ForgeDirection... sides) {
		boolean hasChanged = false;

		for (ForgeDirection side : sides) {
			final int sideId = side.ordinal();
			SyncableBlockLayers layers = getLayersForSide(sideId);
			if (layers.isLastLayerStencil()) {
				layers.setLastLayerColor(color);
				layers.moveStencilToNextLayer();
			} else {
				// collapse all layers, since they will be fully covered by paint
				layers.clear();
				baseColors.setValue(sideId, color);
			}

			hasChanged |= layers.isDirty();
		}

		hasChanged |= baseColors.isDirty();

		if (!worldObj.isRemote) sync();
		return hasChanged;
	}

	private void dropStackFromSide(ItemStack stack, int side) {
		if (worldObj.isRemote) return;
		ForgeDirection dropSide = ForgeDirection.getOrientation(side);

		double dropX = xCoord + dropSide.offsetX;
		double dropY = yCoord + dropSide.offsetY;
		double dropZ = zCoord + dropSide.offsetZ;

		BlockUtils.dropItemStackInWorld(worldObj, dropX, dropY, dropZ, stack);
	}

	public void removePaint(int... sides) {
		for (int side : sides) {
			SyncableBlockLayers layer = getLayersForSide(side);

			// If there is a stencil on top, pop it off.
			if (layer.isLastLayerStencil()) {
				Stencil stencil = layer.getTopStencil();
				ItemStack dropStack = new ItemStack(OpenBlocks.Items.stencil, 1, stencil.ordinal());
				dropStackFromSide(dropStack, side);
			}

			layer.clear();

			baseColors.setValue(side, 0xFFFFFF);
		}

		if (isBlockUnpainted() && paintedBlock.containsValidBlock()) {
			Block block = paintedBlock.getValue();
			worldObj.setBlock(xCoord, yCoord, zCoord, block, paintedBlockMeta.get(), BlockNotifyFlags.SEND_TO_CLIENTS);
		}

		if (!worldObj.isRemote) sync();
	}

	public boolean useStencil(int side, Stencil stencil) {
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
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		ItemStack held = player.getHeldItem();
		if (held != null) {
			Item heldItem = held.getItem();
			if (heldItem instanceof ItemSqueegee || heldItem instanceof ItemPaintBrush || heldItem instanceof ItemStencil) return false;
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
		for (SyncableBlockLayers sideLayers : allSides) {
			if (sideLayers.isLastLayerStencil()) {
				Stencil stencil = sideLayers.getTopStencil();
				if (stencil != null) drops.add(new ItemStack(OpenBlocks.Items.stencil, 1, stencil.ordinal()));
			}
		}
	}

	@Override
	public boolean suppressNormalHarvestDrops() {
		return paintedBlock.containsValidBlock();
	}

	@Override
	public void addHarvestDrops(EntityPlayer player, List<ItemStack> drops) {
		if (paintedBlock.containsValidBlock()) {
			final Block paintedBlock = this.paintedBlock.getValue();
			final int paintedBlockMeta = this.paintedBlockMeta.get();

			final Random rand = worldObj.rand;

			int fortune = player != null? EnchantmentHelper.getFortuneModifier(player) : 0;
			int count = paintedBlock.quantityDropped(paintedBlockMeta, fortune, rand);
			int damageDropped = paintedBlock.damageDropped(paintedBlockMeta);

			for (int i = 0; i < count; i++) {
				Item item = paintedBlock.getItemDropped(paintedBlockMeta, rand, fortune);
				if (item != null) drops.add(new ItemStack(item, 1, damageDropped));

			}
		}
	}

	public void setPaintedBlockBlock(Block block, int meta, int color) {
		paintedBlock.setValue(block);
		paintedBlockMeta.set(meta);
		for(int i = 0; i < 6; i++){
			baseColors.setValue(i, color);
		}
	}
}