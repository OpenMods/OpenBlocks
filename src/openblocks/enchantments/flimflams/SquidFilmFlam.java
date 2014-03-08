package openblocks.enchantments.flimflams;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import openblocks.api.IFlimFlamEffect;

import com.google.common.collect.ImmutableList;

public class SquidFilmFlam implements IFlimFlamEffect {

	private static final Random random = new Random();

	private static final List<String> names = ImmutableList.of(
			"Fancy Hat",
			"Hello there!",
			"Look at my hat!",
			"My hat is amazing!",
			"Ceci n'est pas une pipe",
			"???");

	@Override
	public boolean execute(EntityPlayer target) {
		if (target.riddenByEntity != null && !target.riddenByEntity.isDead) return false;

		EntitySquid squid = new EntitySquid(target.worldObj);
		squid.moveEntity(target.posX, target.boundingBox.minY, target.posZ);

		int selected = random.nextInt(names.size());
		squid.setCustomNameTag(names.get(selected));

		target.worldObj.spawnEntityInWorld(squid);
		squid.mountEntity(target);
		return true;
	}

	@Override
	public String name() {
		return "squid";
	}

	@Override
	public float weight() {
		return 1;
	}

	@Override
	public float cost() {
		return 20;
	}

}
