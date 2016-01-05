package openblocks.enchantments.flimflams;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import openblocks.api.IFlimFlamAction;
import openmods.utils.CollectionUtils;
import openmods.utils.WorldUtils;

public class MountFlimFlam implements IFlimFlamAction {

	private static final IEntitySelector SAFE_SELECTOR = new IEntitySelector() {
		@Override
		public boolean isEntityApplicable(Entity entity) {
			return !(entity instanceof EntityCreeper) && !(entity instanceof EntitySquid);
		}
	};

	@Override
	public boolean execute(EntityPlayerMP target) {
		final World world = target.worldObj;

		AxisAlignedBB around = target.boundingBox.expand(40, 40, 40);
		List<EntityCreature> mobs = WorldUtils.getEntitiesWithinAABB(world, EntityCreature.class, around, SAFE_SELECTOR);
		if (mobs.isEmpty()) return false;
		EntityLiving selected = CollectionUtils.getRandom(mobs);
		target.mountEntity(selected);
		return true;
	}

}
