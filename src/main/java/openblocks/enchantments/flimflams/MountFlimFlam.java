package openblocks.enchantments.flimflams;

import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;
import openmods.utils.CollectionUtils;

public class MountFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		final World world = target.world;

		AxisAlignedBB around = target.getEntityBoundingBox().grow(40);
		List<EntityLiving> mobs = world.getEntitiesWithinAABB(EntityLiving.class, around,
				entity -> !(entity instanceof EntityCreeper) && !(entity instanceof EntitySquid));
		if (mobs.isEmpty()) return false;
		EntityLiving selected = CollectionUtils.getRandom(mobs);
		return target.startRiding(selected);
	}

}
