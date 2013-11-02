package openblocks.client;

import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemSleepingBag;

public class PlayerRenderEventHandler {

	@ForgeSubscribe
	public void onPrePlayerRender(RenderPlayerEvent.Pre event) {
		if (OpenBlocks.Items.sleepingBag != null && event.entityPlayer != null) {
			if (event.entityPlayer.isPlayerSleeping() && ItemSleepingBag.isWearingSleepingBag(event.entityPlayer)) {
				event.entityPlayer.yOffset = .7f;
			}
		}
	}
}
