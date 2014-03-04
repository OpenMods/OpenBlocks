package openblocks.enchantments.flimflams;

import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import openblocks.api.IAttackFlimFlam;

public class TeleportFlimFlam implements IAttackFlimFlam {

	@Override
	public void execute(EntityPlayer attacker, EntityPlayer target) {
		final World world = target.worldObj;

		EntityEnderPearl e = new EntityEnderPearl(world, target);
		e.setPosition(target.posX, target.posY + 1, target.posZ);
		e.motionX = world.rand.nextGaussian();
		e.motionY = 0.5;
		e.motionZ = world.rand.nextGaussian();
		world.spawnEntityInWorld(e);
	}

	@Override
	public String name() {
		return "teleport";
	}

	@Override
	public float weight() {
		return 1;
	}

}
