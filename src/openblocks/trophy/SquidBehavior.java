package openblocks.trophy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class SquidBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		final World worldObj = tile.worldObj;
		if (worldObj == null || worldObj.isRemote || worldObj.provider.isHellWorld) { return; }
		int x = MathHelper.floor_double(player.posX);
		int y = MathHelper.floor_double(player.posY + 1);
		int z = MathHelper.floor_double(player.posZ);
		if (worldObj.isAirBlock(x, y, z)) {
			worldObj.setBlock(x, y, z, Block.waterMoving.blockID);
		}
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
