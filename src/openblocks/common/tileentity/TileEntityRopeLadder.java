package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityRopeLadder extends OpenTileEntity implements IAwareTile {

	private boolean shouldAnimate = true;

	@SideOnly(Side.CLIENT)
	@Override
	public void prepareForInventoryRender(Block block, int metadata) {
		super.prepareForInventoryRender(block, metadata);
		shouldAnimate = false;
	}

	public boolean shouldAnimate() {
		return shouldAnimate;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			int y = yCoord;
			while (y-- > 0) {
				if (worldObj.isAirBlock(xCoord, y, zCoord) && OpenBlocks.Blocks.ropeLadder.canPlaceBlockOnSide(worldObj, xCoord, y, zCoord, getRotation())) {
					worldObj.setBlock(xCoord, y, zCoord, getBlockType().blockID, getMetadata(), 3);
				} else {
					return;
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public void onBlockBroken() {
		if (worldObj.isRemote) return;
		int y = yCoord;
		while (y-- > 0) {
			TileEntity te = worldObj.getBlockTileEntity(xCoord, y, zCoord);
			if (te instanceof TileEntityRopeLadder) {
				worldObj.setBlockToAir(xCoord, y, zCoord);
			} else {
				return;
			}
		}
	}

	@Override
	public void onBlockAdded() {}

	@Override
	public void onNeighbourChanged(int blockId) {}

}
