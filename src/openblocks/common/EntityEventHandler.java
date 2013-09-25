package openblocks.common;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.monster.IMob;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingEvent;
import openblocks.common.entity.ai.EntityAIMoveTowardsDecoy;

public class EntityEventHandler {

	@ForgeSubscribe
	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {

		if (event.entityLiving.ticksExisted < 5
				&& event.entityLiving instanceof EntityCreature
				&& event.entityLiving instanceof IMob) {

			EntityCreature creature = (EntityCreature) event.entityLiving;

			List<EntityAITaskEntry> tasks = creature.targetTasks.taskEntries;

			boolean found = false;

			for (EntityAITaskEntry task : tasks) {
				if (task.action instanceof EntityAIMoveTowardsDecoy) {
					found = true;
					break;
				}
			}
			if (!found) {
				creature.targetTasks.addTask(
						creature.targetTasks.taskEntries.size(),
						new EntityAIMoveTowardsDecoy(creature, 1.0D));
			}
		}
	}
}
