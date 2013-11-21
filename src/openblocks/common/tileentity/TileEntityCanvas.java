package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.Stencil;
import openblocks.common.item.ItemPaintBrush;
import openblocks.sync.*;
import openblocks.sync.SyncableBlockLayers.Layer;
import openblocks.utils.BlockUtils;
import openmods.common.api.IAwareTile;

public class TileEntityCanvas extends SyncedTileEntity implements IAwareTile {

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
		if (layerId == -1) { return baseColors.getValue(renderSide); }
		Layer layer = getLayerForSide(renderSide, layerId);
		if (layer != null) { return layer.getColorForRender(); }
		return 0xCCCCCC;
	}

	public Icon getTextureForRender(int renderSide, int layerId) {
		if (layerId > -1) {
			Layer layer = getLayerForSide(renderSide, layerId);
			if (layer != null) {
				Stencil stencil = layer.getStencil();
				if (stencil != null) { return layer.hasStencilCover()? stencil.getCoverBlockIcon() : stencil.getBlockIcon(); }
			}
		}
		return getBaseTexture(renderSide);
	}

	private Icon getBaseTexture(int side) {
		if (paintedBlockId.getValue() == 0) return OpenBlocks.Blocks.canvas.baseIcon;
		Block block = Block.blocksList[paintedBlockId.getValue()];
		if (block == null) return OpenBlocks.Blocks.canvas.baseIcon;
		return block.getIcon(side, paintedBlockMeta.getValue());
	}

	private boolean isBlockUnpainted() {
		for (int i = 0; i < allSides.length; i++) {
			if (!allSides[i].isEmpty() || baseColors.getValue(i) != 0xFFFFFF) return false;
		}
		return true;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {

		ForgeDirection dropSide = ForgeDirection.getOrientation(side);
		double dropX = xCoord + dropSide.offsetX;
		double dropY = yCoord + dropSide.offsetY;
		double dropZ = zCoord + dropSide.offsetZ;

		// get the layers on the side activated
		SyncableBlockLayers layers = getLayersForSide(side);
		ItemStack heldItem = player.getHeldItem();

		// if they're holding an item
		if (heldItem != null) {
			Item item = heldItem.getItem();
			// and it's a paintbrush
			if (item.equals(OpenBlocks.Items.paintBrush) && heldItem.getItemDamage() < ItemPaintBrush.MAX_USES) {
				int color = ItemPaintBrush.getColorFromStack(heldItem);

				if (player.isSneaking()) {
					for (int i = 0; i < 6; i++) {
						if (heldItem.getItemDamage() == ItemPaintBrush.MAX_USES) break;
						SyncableBlockLayers layer = getLayersForSide(i);
						if (!layer.setColor(color)) {
							// clear the layers
							layer.clear();
							baseColors.setValue(i, color);
						}
						heldItem.damageItem(1, player);
					}
				} else if (heldItem.getItemDamage() < ItemPaintBrush.MAX_USES) {
					if (!layers.setColor(color)) {
						// clear the layers
						layers.clear();
						baseColors.setValue(side, color);
					}
					heldItem.damageItem(1, player);
				}

				worldObj.playSoundAtEntity(player, "mob.slime.small", 0.1F, 0.8F);

				if (!worldObj.isRemote) {
					sync();
				}
				return false;
			} else if (item.equals(OpenBlocks.Items.squeegee)) {
				// Remove layers and then base color.
				// Upon the removal of ALL layers, reset the block to not a
				// canvas to spare
				// The TE ticks and custom rendering

				// Select the layers we are going to clean.
				if (!worldObj.isRemote && !isBlockUnpainted()) {
					SyncableBlockLayers[] cleanLayers = player.isSneaking()? allSides
							: new SyncableBlockLayers[] { layers };
					for (SyncableBlockLayers cleaningLayer : cleanLayers) {
						// If there is a stencil on top, pop it off.
						if (cleaningLayer.hasStencilCover()) {
							ItemStack dropStack = new ItemStack(OpenBlocks.Items.stencil, 1, cleaningLayer.getTopStencil().ordinal());
							BlockUtils.dropItemStackInWorld(worldObj, dropX, dropY, dropZ, dropStack);
						}
						cleaningLayer.clear();
					}
					if (player.isSneaking()) {
						for (int i = 0; i < baseColors.size(); i++) {
							baseColors.setValue(i, 0xFFFFFF);
						}
					} else {
						baseColors.setValue(side, 0xFFFFFF);
					}
					if (isBlockUnpainted() && paintedBlockId.getValue() != 0) {
						/*
						 * Dispatch chunk update, but not block update. Since
						 * the change should be almost invisible
						 */
						worldObj.setBlock(xCoord, yCoord, zCoord,
								paintedBlockId.getValue(),
								paintedBlockMeta.getValue(), 2);
					}
					worldObj.playSoundAtEntity(player, "openblocks:wipe", 1F, 1F);
					sync();
				}
				return true;
			} else if (item.equals(OpenBlocks.Items.stencil)) {
				boolean currentStencilMatchesItem = false;
				// get the stencil
				Stencil stencil = Stencil.values()[heldItem.getItemDamage()];
				// if there already is a stencil
				if (layers.hasStencilCover()) {
					Stencil topStencil = layers.getTopStencil();
					// and it matches what we're holding, hold off for now..
					if (topStencil == stencil) {
						currentStencilMatchesItem = true;
					}
				}
				// if it's a different stencil that we're trying to place,
				// place it
				if (!currentStencilMatchesItem) {
					if (layers.hasStencilCover() && !worldObj.isRemote) {
						ItemStack dropStack = new ItemStack(OpenBlocks.Items.stencil, 1, layers.getTopStencil().ordinal());
						BlockUtils.dropItemStackInWorld(worldObj, dropX, dropY, dropZ, dropStack);
					}
					layers.setStencilCover(stencil);
					player.inventory.decrStackSize(player.inventory.currentItem, 1);
					if (!worldObj.isRemote) {
						sync();
					}
					return false;
				}
			}
		}
		boolean madeCoverChange = false;
		if (player.isSneaking()) {
			getLayersForSide(side).rotateCover();
			madeCoverChange = true;
		} else if (layers.getTopStencil() != null && layers.hasStencilCover()) {
			if (!worldObj.isRemote) {
				ItemStack dropStack = new ItemStack(OpenBlocks.Items.stencil, 1, layers.getTopStencil().ordinal());
				BlockUtils.dropItemStackInWorld(worldObj, dropX, dropY, dropZ, dropStack);
			}
			getLayersForSide(side).removeCover();
			madeCoverChange = true;
		}
		if (madeCoverChange) {
			if (!worldObj.isRemote) {
				sync();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {}

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockBroken() {
		if (worldObj.isRemote) return;
		for (SyncableBlockLayers sideLayers : allSides) {
			if (sideLayers.hasStencilCover()) {
				Stencil stencil = sideLayers.getTopStencil();
				if (stencil != null) {
					BlockUtils.dropItemStackInWorld(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, new ItemStack(OpenBlocks.Items.stencil, 1, stencil.ordinal()));
				}
			}
		}
	}

	@Override
	public void onBlockAdded() {}
}
