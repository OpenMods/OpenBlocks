package openblocks.trophy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import openblocks.common.tileentity.TileEntityTrophy;

public class MooshroomBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++) {
				int pX = x + tile.xCoord;
				int pY = tile.yCoord;
				int pZ = z + tile.zCoord;
				if (tile.worldObj.isAirBlock(pX, pY, pZ)
						&& Block.mushroomBrown.canPlaceBlockAt(tile.worldObj, pX, pY, pZ)) {
					tile.worldObj.setBlock(pX, pY, pZ, Block.mushroomBrown.blockID);
				}
			}

		return 100;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
