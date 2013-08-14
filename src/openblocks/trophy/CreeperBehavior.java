package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class CreeperBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntity tile, EntityPlayer player) {
		if (!tile.worldObj.isRemote) {
			tile.worldObj.createExplosion(player, tile.xCoord, tile.yCoord, tile.zCoord, 2, false);
	
		}
	}
	
	@Override
	public void executeTickBehavior(TileEntity tile) {
		// TODO Auto-generated method stub

	}

}
