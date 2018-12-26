package openblocks.enchantments.flimflams;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;
import openmods.utils.WorldUtils;

public class InvisibleMobsFlimFlam implements IFlimFlamAction {

	private static final int MIN_10 = 10 * 60 * 20;
	private static final Random random = new Random();

	@Override
	public boolean execute(EntityPlayerMP target) {
		final World world = target.world;

		AxisAlignedBB around = target.getEntityBoundingBox().grow(20);
		List<EntityLiving> mobs = world.getEntitiesWithinAABB(EntityLiving.class, around, WorldUtils.NON_PLAYER);

		if (mobs.isEmpty()) return false;

		for (EntityLiving e : mobs) {
			if (random.nextFloat() < 0.3) e.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, MIN_10, 1));
		}

		return true;
	}

}
