package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.Stencil;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemSqueegee;
import openblocks.common.item.ItemStencil;
import openblocks.common.sync.SyncableBlockLayers;
import openblocks.common.sync.SyncableBlockLayers.Layer;
import openmods.api.IActivateAwareTile;
import openmods.api.ISpecialDrops;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableInt;
import openmods.sync.SyncableIntArray;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockNotifyFlags;
import openmods.utils.BlockUtils;
import openmods.utils.render.PaintUtils;

public class TileEntityCanvas extends SyncedTileEntity implements IActivateAwareTile, ISpecialDrops {

	private static final int BASE_LAYER = -1;

	public static final int[] ALL_SIDES = { 0, 1, 2, 3, 4, 5 };

	/* Used for painting other blocks */
	public SyncableInt paintedBlockId, paintedBlockMeta;

	private SyncableIntArray baseColors;

	public SyncableBlockLayers stencilsUp;
	public SyncableBlockLayers stencilsDown;
	public SyncableBlockLayers stencilsEast;
	public SyncableBlockLayers stencilsWest;
	public SyncableBlockLayers stencilsNorth;
	public SyncableBlockLayers stencilsSouth;

	public SyncableBlockLayers[] allSides;

	@Override
    public boolean canUpdate() {
        return false;
    }
	
	@Override
	public void initialize() {}

	public void setupForItemRenderer() {
		createSyncedFields();
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
		paintedBlockId = new SyncableInt(0);
		paintedBlockMeta = new SyncableInt(0);
	}

	public SyncableBlockLayers getLayersForSide(int side) {
		return allSides[side];
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	public Layer getLayerForSide(int renderSide, int layerId) {
		SyncableBlockLayers layers = getLayersForSide(renderSide);
		if (layers != null) { return layers.getLayer(layerId); }
		return null;
	}

	public int getColorForRender(int renderSide, int layerId) {
		if (layerId == BASE_LAYER) { return baseColors.getValue(renderSide); }
		Layer layer = getLayerForSide(renderSide, layerId);
		if (layer != null) { return layer.getColorForRender(); }
		return 0xCCCCCC;
	}

	public Icon getTextureForRender(int renderSide, int layerId) {
		if (layerId > BASE_LAYER) {
			Layer layer = getLayerForSide(renderSide, layerId);
			if (layer != null) {
				Stencil stencil = layer.getStencil();
				if (stencil != null) { return layer.hasStencilCover()? stencil.getCoverBlockIcon() : stencil.getBlockIcon(); }
			}
		}
		return getBaseTexture(renderSide);
	}

	private Icon getBaseTexture(int side) {
		SyncableBlockLayers layers = getLayersForSide(side);
		int blockId = layers.getBaseTextureBlockId();
		int blockMeta = layers.getBaseTextureMetadata();
		if (blockId == 0) {
			blockId = paintedBlockId.getValue();
			blockMeta = paintedBlockMeta.getValue();
		}
		if (blockId > 0) {
			Block block = Block.blocksList[blockId];
			if (block != null) {
				return block.getIcon(side, blockMeta);
			}
		}
		return OpenBlocks.Blocks.canvas.baseIcon;
	}

	private boolean isBlockUnpainted() {
		for (int i = 0; i < allSides.length; i++) {
			if (!allSides[i].isEmpty() || baseColors.getValue(i) != 0xFFFFFF) return false;
		}
		return true;
	}

	public void applyPaint(int color, int... sides) {
		for (int side : sides) {
			SyncableBlockLayers layer = getLayersForSide(side);
			if (layer.isLastLayerStencil()) {
				layer.setLastLayerColor(color);
				layer.moveStencilToNextLayer();
			} else {
				// collapse all layers, since they will be fully covered by
				// paint
				layer.clear(false);
				baseColors.setValue(side, color);
			}
		}

		if (!worldObj.isRemote) sync();
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

			layer.clear(true);

			baseColors.setValue(side, 0xFFFFFF);
		}

		if (isBlockUnpainted() && paintedBlockId.getValue() != 0) {
			worldObj.setBlock(xCoord, yCoord, zCoord, paintedBlockId.getValue(), paintedBlockMeta.getValue(), BlockNotifyFlags.SEND_TO_CLIENTS);
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

		SyncableBlockLayers layer = getLayersForSide(side);
		
		ItemStack held = player.getHeldItem();
		if (held != null) {
			Item heldItem = held.getItem();
			if (heldItem instanceof ItemSqueegee || heldItem instanceof ItemPaintBrush || heldItem instanceof ItemStencil) return false;
			if (heldItem instanceof ItemBlock && !player.isSneaking()) {
				int blockId = ((ItemBlock)heldItem).getBlockID();
				Block block = Block.blocksList[blockId];
				if (PaintUtils.instance.isAllowedToReplace(block)) {
					layer.setBaseTextureBlockId(blockId);
					layer.setBaseTextureMetadata(held.getItemDamage());
					if (!worldObj.isRemote) sync();
					return true;
				}
			}
		}

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
		if (paintedBlockId.getValue() == 0) {
			drops.add(new ItemStack(getBlockType()));
		}
		for (SyncableBlockLayers sideLayers : allSides) {
			if (sideLayers.isLastLayerStencil()) {
				Stencil stencil = sideLayers.getTopStencil();
				if (stencil != null) drops.add(new ItemStack(OpenBlocks.Items.stencil, 1, stencil.ordinal()));
			}
		}
	}
}
