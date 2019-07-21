package openblocks.enchantments.flimflams;

import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;

public class TeleportFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(ServerPlayerEntity target) {
		final World world = target.world;

		EnderPearlEntity e = new EnderPearlEntity(world, target);
		e.setPosition(target.posX, target.posY + 1, target.posZ);
		e.motionX = world.rand.nextGaussian();
		e.motionY = 0.5;
		e.motionZ = world.rand.nextGaussian();
		world.spawnEntity(e);
		return true;
	}

}
