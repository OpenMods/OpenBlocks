package openblocks.common;

import net.minecraft.entity.EntityList;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent;
import openblocks.Config;

public class EntityEventHandler {

	@ForgeSubscribe
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {

		if (event.entityLiving != null && EntityList.classToStringMapping.containsKey(event.entityLiving.getClass())) {
			String livingName = (String)EntityList.classToStringMapping.get(event.entityLiving.getClass());

			if (Config.disableMobNames.contains(livingName)) {
				event.entityLiving.setDead();
				return;
			}
		}

		/*
		 * if (event.entityLiving.ticksExisted < 5 && event.entityLiving
		 * instanceof EntityCreature && event.entityLiving instanceof IMob) {
		 * 
		 * EntityCreature creature = (EntityCreature)event.entityLiving;
		 * 
		 * @SuppressWarnings("unchecked")
		 * List<EntityAITaskEntry> tasks = creature.targetTasks.taskEntries;
		 * 
		 * boolean found = false;
		 * 
		 * for (EntityAITaskEntry task : tasks) {
		 * if (task.action instanceof EntityAIMoveTowardsDecoy) {
		 * found = true;
		 * break;
		 * }
		 * }
		 * if (!found) {
		 * creature.targetTasks.addTask(creature.targetTasks.taskEntries.size(),
		 * new EntityAIMoveTowardsDecoy(creature, 1.0D));
		 * }
		 * }
		 */
	}
}
