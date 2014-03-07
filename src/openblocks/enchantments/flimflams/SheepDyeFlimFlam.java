package openblocks.enchantments.flimflams;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IAttackFlimFlam;

public class SheepDyeFlimFlam implements IAttackFlimFlam {

	private static final Random random = new Random();

	@Override
	public void execute(EntityPlayer source, EntityPlayer target) {
		World world = target.worldObj;
		AxisAlignedBB around = target.boundingBox.expand(20, 20, 20);
		@SuppressWarnings("unchecked")
		List<EntitySheep> sheeps = world.getEntitiesWithinAABB(EntitySheep.class, around);

		if (sheeps.isEmpty()) return;

		EntitySheep chosenOne = sheeps.get(random.nextInt(sheeps.size()));
		int color = chosenOne.getFleeceColor();
		chosenOne.setFleeceColor(color + random.nextInt(15));
	}

	@Override
	public String name() {
		return "sheep-dye";
	}

	@Override
	public float weight() {
		return 1;
	}

}
