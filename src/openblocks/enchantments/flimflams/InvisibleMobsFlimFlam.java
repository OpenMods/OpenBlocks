package openblocks.enchantments.flimflams;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamEffect;
import openmods.utils.WorldUtils;

public class InvisibleMobsFlimFlam implements IFlimFlamEffect {

	private static final int MIN_10 = 10 * 60 * 20;
	private static final Random random = new Random();

	@Override
	public boolean execute(EntityPlayer target) {
		final World world = target.worldObj;

		AxisAlignedBB around = target.boundingBox.expand(20, 20, 20);
		List<EntityLiving> mobs = WorldUtils.getEntitiesWithinAABB(world, EntityLiving.class, around, WorldUtils.NON_PLAYER);

		if (mobs.isEmpty()) return false;

		for (EntityLiving e : mobs) {
			if (random.nextFloat() < 0.3) e.addPotionEffect(new PotionEffect(Potion.invisibility.id, MIN_10, 1));
		}

		return true;
	}

	@Override
	public String name() {
		return "invisible-mobs";
	}

	@Override
	public int weight() {
		return 10;
	}

	@Override
	public int cost() {
		return 10;
	}

	@Override
	public boolean isSilent() {
		return false;
	}

}
