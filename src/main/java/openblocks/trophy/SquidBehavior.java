package openblocks.trophy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class SquidBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		final BlockPos base = tile.getPos().up();
		final World world = tile.getWorld();

		if (world.provider.doesWaterVaporize()) world.playSoundEffect(base.getX(), base.getY(), base.getZ(), "random.fizz", 0.5F, 2.6F);
		else if (world.isAirBlock(base)) world.setBlockState(base, Blocks.flowing_water.getDefaultState());

		return 10;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}

}
