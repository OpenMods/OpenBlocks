package openblocks.client;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.common.entity.EntityHangGlider;
import openmods.renderer.PlayerBodyRenderEvent;
import org.lwjgl.opengl.GL11;

public class GliderPlayerRenderHandler {

	@SubscribeEvent
	public void onPlayerBodyRender(PlayerBodyRenderEvent evt) {
		final AbstractClientPlayerEntity player = evt.player;
		if (EntityHangGlider.isGliderDeployed(player)) {
			player.limbSwing = 0f;
			player.prevLimbSwingAmount = 0f;
			player.limbSwingAmount = 0f;
			GL11.glRotatef(75, -1, 0, 0);
		}
	}
}
