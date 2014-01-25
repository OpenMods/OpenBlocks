package openblocks.common;

import net.minecraft.entity.EntityList;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import openblocks.Config;

import org.apache.commons.lang3.ArrayUtils;

public class EntityEventHandler {

	@ForgeSubscribe
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {

		if (event.entity != null
				&& EntityList.classToStringMapping.containsKey(event.entity.getClass())) {
			String livingName = (String)EntityList.classToStringMapping.get(event.entity.getClass());

			if (ArrayUtils.contains(Config.disableMobNames, livingName)) {
				event.entity.setDead();
				return;
			}
		}
	}
}
