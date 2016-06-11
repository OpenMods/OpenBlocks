package openblocks.enchantments.flimflams;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;
import openmods.utils.WorldUtils;

public class SheepDyeFlimFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	@Override
	public boolean execute(EntityPlayerMP target) {
		World world = target.worldObj;
		AxisAlignedBB around = target.boundingBox.expand(20, 20, 20);
		List<EntitySheep> sheeps = WorldUtils.getEntitiesWithinAABB(world, EntitySheep.class, around);
		if (sheeps.isEmpty()) return false;

		EntitySheep chosenOne = sheeps.get(random.nextInt(sheeps.size()));
		int color = chosenOne.getFleeceColor();
		chosenOne.setFleeceColor(color + random.nextInt(15));
		return true;
	}

}
