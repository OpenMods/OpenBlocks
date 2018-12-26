package openblocks.client;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.common.item.ItemSleepingBag;

public class SleepingBagRenderHandler {

	@SubscribeEvent
	public void onPrePlayerRender(RenderPlayerEvent.Pre event) {
		final EntityPlayer entityPlayer = event.getEntityPlayer();
		if (entityPlayer instanceof EntityOtherPlayerMP) {
			if (entityPlayer.isPlayerSleeping() && ItemSleepingBag.isWearingSleepingBag(entityPlayer)) {
				entityPlayer.renderOffsetY = 1.7f;
			}
		}

	}
}
