package openblocks.enchantments.flimflams;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayerMP;
import openblocks.api.IFlimFlamAction;

import com.google.common.collect.ImmutableList;

public class SquidFilmFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	private static final List<String> names = ImmutableList.of(
			"Fancy Hat",
			"Hello there!",
			"Look at my hat!",
			"My hat is amazing!",
			"Ceci n'est pas une pipe",
			"???");

	@Override
	public boolean execute(EntityPlayerMP target) {
		if (target.riddenByEntity != null && !target.riddenByEntity.isDead) return false;

		EntitySquid squid = new EntitySquid(target.worldObj);
		squid.moveEntity(target.posX, target.getEntityBoundingBox().minY, target.posZ);

		int selected = random.nextInt(names.size());
		squid.setCustomNameTag(names.get(selected));

		target.worldObj.spawnEntityInWorld(squid);
		squid.mountEntity(target);
		return true;
	}

}
