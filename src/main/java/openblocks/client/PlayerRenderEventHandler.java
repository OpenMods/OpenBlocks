package openblocks.client;

import net.minecraftforge.client.event.RenderPlayerEvent;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemSleepingBag;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class PlayerRenderEventHandler {

	@SubscribeEvent
	public void onPrePlayerRender(RenderPlayerEvent.Pre event) {
		if (event.entityPlayer != null) {
			if (OpenBlocks.Items.sleepingBag != null) {
				if (event.entityPlayer.isPlayerSleeping() && ItemSleepingBag.isWearingSleepingBag(event.entityPlayer)) {
					event.entityPlayer.yOffset = .7f;
				}
			}
		}

	}
}
