package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;
import openmods.utils.BlockNotifyFlags;

public class SnowmanBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				int pX = x + tile.xCoord;
				int pY = tile.yCoord;
				int pZ = z + tile.zCoord;
				final World worldObj = tile.getWorldObj();
				if (worldObj.isAirBlock(pX, pY, pZ) && Blocks.snow_layer.canPlaceBlockAt(worldObj, pX, pY, pZ)) {
					worldObj.setBlock(pX, pY, pZ, Blocks.snow_layer, worldObj.rand.nextInt(4), BlockNotifyFlags.ALL);
				}
			}
		}

		return 10;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
