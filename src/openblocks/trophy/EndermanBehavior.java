package openblocks.trophy;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTrophy;

public class EndermanBehavior implements ITrophyBehavior {

	@Override
	public void executeActivateBehavior(TileEntityTrophy tile, EntityPlayer player) {
		final World world = tile.worldObj;
		if (world.isRemote) return;
		EntityEnderPearl e = new EntityEnderPearl(world, player);
		e.setPosition(tile.xCoord + 0.5, tile.yCoord + 0.5, tile.zCoord + 0.5);
		e.setVelocity(world.rand.nextGaussian(), 1, world.rand.nextGaussian());
		world.spawnEntityInWorld(e);
	}

	@Override
	public void executeTickBehavior(TileEntityTrophy tile) {}
}
