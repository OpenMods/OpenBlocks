package openblocks.trophy;

import openblocks.common.tileentity.TileEntityTrophy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class BlazeBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (!tile.worldObj.isRemote) {
			player.setFire(4);
		}
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {
		// TODO Auto-generated method stub

	}

}
