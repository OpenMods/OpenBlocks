package openblocks.trophy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class SquidBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntity tile, EntityPlayer player) {
		if (tile.worldObj.isRemote) {
			return;
		}
		if (tile.worldObj.isAirBlock(tile.xCoord, tile.yCoord + 1, tile.zCoord)) {
			tile.worldObj.setBlock(tile.xCoord, tile.yCoord + 1, tile.zCoord, Block.waterMoving.blockID);
		}
	}

	@Override
	public void executeTickBehavior(TileEntity tile) {
		// TODO Auto-generated method stub

	}

}
