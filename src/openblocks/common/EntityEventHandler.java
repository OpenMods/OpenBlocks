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
	}
}
