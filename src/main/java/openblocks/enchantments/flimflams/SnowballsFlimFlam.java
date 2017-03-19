package openblocks.enchantments.flimflams;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;

public class SnowballsFlimFlam implements IFlimFlamAction {

	private static final Random RANDOM = new Random();

	@Override
	public boolean execute(EntityPlayerMP target) {
		final World world = target.worldObj;
		for (int i = 0; i < 200; i++) {
			EntitySnowball snowball = new EntitySnowball(world, target.posX, target.posY + 4, target.posZ);
			snowball.motionX = RANDOM.nextGaussian() * 0.05;
			snowball.motionY = 1;
			snowball.motionZ = RANDOM.nextGaussian() * 0.05;

			world.spawnEntityInWorld(snowball);
		}

		return true;
	}
}
