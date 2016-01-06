package openblocks.trophy;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class EndermanBehavior implements ITrophyBehavior {

	@Override
	public int executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		final World world = tile.getWorld();
		EntityEnderPearl e = new EntityEnderPearl(world, player);
		final BlockPos pos = tile.getPos();
		e.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
		e.motionX = world.rand.nextGaussian();
		e.motionY = 1;
		e.motionZ = world.rand.nextGaussian();
		world.spawnEntityInWorld(e);
		return 10;
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}
}
