package openblocks.enchantments.flimflams;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import openblocks.api.IFlimFlamAction;

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
	public boolean execute(ServerPlayerEntity target) {
		if (target.isBeingRidden()) return false;

		SquidEntity squid = new SquidEntity(target.world);
		squid.move(MoverType.SELF, target.posX, target.getEntityBoundingBox().minY, target.posZ);

		int selected = random.nextInt(names.size());
		squid.setCustomNameTag(names.get(selected));

		target.world.spawnEntity(squid);
		return true;
	}

}
