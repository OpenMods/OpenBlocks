package openblocks.enchantments.flimflams;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;
import openmods.utils.CollectionUtils;

public class SheepDyeFlimFlam implements IFlimFlamAction {

	private static final Random random = new Random();

	@Override
	public boolean execute(ServerPlayerEntity target) {
		World world = target.world;
		AxisAlignedBB around = target.getEntityBoundingBox().grow(20);
		List<SheepEntity> sheeps = world.getEntitiesWithinAABB(SheepEntity.class, around);
		if (sheeps.isEmpty()) return false;

		SheepEntity chosenOne = sheeps.get(random.nextInt(sheeps.size()));
		chosenOne.setFleeceColor(CollectionUtils.getRandom(Arrays.asList(DyeColor.values())));
		return true;
	}

}
