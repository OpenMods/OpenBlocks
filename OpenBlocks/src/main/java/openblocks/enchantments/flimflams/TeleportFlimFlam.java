package openblocks.enchantments.flimflams;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;

public class TeleportFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		final World world = target.world;

		EntityEnderPearl e = new EntityEnderPearl(world, target);
		e.setPosition(target.posX, target.posY + 1, target.posZ);
		e.motionX = world.rand.nextGaussian();
		e.motionY = 0.5;
		e.motionZ = world.rand.nextGaussian();
		world.spawnEntity(e);
		return true;
	}

}
