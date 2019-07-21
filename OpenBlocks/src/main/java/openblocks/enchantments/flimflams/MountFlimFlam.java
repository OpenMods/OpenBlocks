package openblocks.enchantments.flimflams;

import java.util.List;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;
import openmods.utils.CollectionUtils;

public class MountFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(ServerPlayerEntity target) {
		final World world = target.world;

		AxisAlignedBB around = target.getEntityBoundingBox().grow(40);
		List<MobEntity> mobs = world.getEntitiesWithinAABB(MobEntity.class, around,
				entity -> !(entity instanceof CreeperEntity) && !(entity instanceof SquidEntity));
		if (mobs.isEmpty()) return false;
		MobEntity selected = CollectionUtils.getRandom(mobs);
		return target.startRiding(selected);
	}

}
