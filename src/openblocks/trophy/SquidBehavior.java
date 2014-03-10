package openblocks.trophy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class SquidBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		final World worldObj = tile.worldObj;
		int x = MathHelper.floor_double(player.posX);
		int y = MathHelper.floor_double(player.posY + 1);
		int z = MathHelper.floor_double(player.posZ);

		if (worldObj.provider.isHellWorld) worldObj.playSoundEffect(x, y, z, "random.fizz", 0.5F, 2.6F);
		else if (worldObj.isAirBlock(x, y, z)) worldObj.setBlock(x, y, z, Block.waterMoving.blockID);

		return 10;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
