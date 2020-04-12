package openblocks.enchantments.flimflams;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;
import openmods.utils.WorldUtils;

public class InvisibleMobsFlimFlam implements IFlimFlamAction {

	private static final int MIN_10 = 10 * 60 * 20;
	private static final Random random = new Random();

	@Override
	public boolean execute(ServerPlayerEntity target) {
		final World world = target.world;

		AxisAlignedBB around = target.getEntityBoundingBox().grow(20);
		List<MobEntity> mobs = world.getEntitiesWithinAABB(MobEntity.class, around, WorldUtils.NON_PLAYER);

		if (mobs.isEmpty()) return false;

		for (MobEntity e : mobs) {
			if (random.nextFloat() < 0.3) e.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, MIN_10, 1));
		}

		return true;
	}

}
