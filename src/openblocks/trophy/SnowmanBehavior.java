package openblocks.trophy;

import openblocks.common.tileentity.TileEntityTrophy;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;

public class SnowmanBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		if (tile.worldObj.isRemote) { return; }
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				int pX = x + tile.xCoord;
				int pY = tile.yCoord;
				int pZ = z + tile.zCoord;
				if (tile.worldObj.isAirBlock(pX, pY, pZ)
						&& Block.snow.canPlaceBlockAt(tile.worldObj, pX, pY, pZ)) {
					tile.worldObj.setBlock(pX, pY, pZ, Block.snow.blockID);
				}
			}
		}
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {
		// TODO Auto-generated method stub

	}

}
