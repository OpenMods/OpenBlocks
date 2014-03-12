package openblocks.enchantments.flimflams;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamEffect;

public class TeleportFlimFlam implements IFlimFlamEffect {

	@Override
	public boolean execute(EntityPlayerMP target) {
		final World world = target.worldObj;

		EntityEnderPearl e = new EntityEnderPearl(world, target);
		e.setPosition(target.posX, target.posY + 1, target.posZ);
		e.motionX = world.rand.nextGaussian();
		e.motionY = 0.5;
		e.motionZ = world.rand.nextGaussian();
		world.spawnEntityInWorld(e);
		return true;
	}

	@Override
	public String name() {
		return "teleport";
	}

	@Override
	public int weight() {
		return 3;
	}

	@Override
	public int cost() {
		return 15;
	}

	@Override
	public boolean isSilent() {
		return false;
	}

}
