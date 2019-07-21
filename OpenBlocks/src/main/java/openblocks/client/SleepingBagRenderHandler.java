package openblocks.client;

import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.common.item.ItemSleepingBag;

public class SleepingBagRenderHandler {

	@SubscribeEvent
	public void onPrePlayerRender(RenderPlayerEvent.Pre event) {
		final PlayerEntity entityPlayer = event.getEntityPlayer();
		if (entityPlayer instanceof RemoteClientPlayerEntity) {
			if (entityPlayer.isPlayerSleeping() && ItemSleepingBag.isWearingSleepingBag(entityPlayer)) {
				entityPlayer.renderOffsetY = 1.7f;
			}
		}

	}
}
