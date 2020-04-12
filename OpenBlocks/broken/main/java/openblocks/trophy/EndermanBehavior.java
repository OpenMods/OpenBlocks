package openblocks.trophy;

import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class EndermanBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, PlayerEntity player) {
		final World world = tile.getWorld();
		EnderPearlEntity e = new EnderPearlEntity(world, player);
		final BlockPos pos = tile.getPos();
		e.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		e.motionX = world.rand.nextGaussian();
		e.motionY = 1;
		e.motionZ = world.rand.nextGaussian();
		world.spawnEntity(e);
		return 10;
	}
}
