package openblocks.enchantments.flimflams;

import java.util.Random;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;

public class SnowballsFlimFlam implements IFlimFlamAction {

	private static final Random RANDOM = new Random();

	@Override
	public boolean execute(ServerPlayerEntity target) {
		final World world = target.world;
		for (int i = 0; i < 200; i++) {
			SnowballEntity snowball = new SnowballEntity(world, target.posX, target.posY + 4, target.posZ);
			snowball.motionX = RANDOM.nextGaussian() * 0.05;
			snowball.motionY = 1;
			snowball.motionZ = RANDOM.nextGaussian() * 0.05;

			world.spawnEntity(snowball);
		}

		return true;
	}
}
