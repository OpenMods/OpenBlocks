package openblocks.trophy;

import openblocks.common.tileentity.TileEntityTrophy;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class SquidBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (tile.worldObj.isRemote) {
			return;
		}
		int x = (int)Math.round(player.posX);
		int y = (int)Math.round(player.posY+1);
		int z = (int)Math.round(player.posZ);
		if (tile.worldObj.isAirBlock(x, y, z)) {
			tile.worldObj.setBlock(x, y, z, Block.waterMoving.blockID);
		}
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {
		// TODO Auto-generated method stub

	}

}
