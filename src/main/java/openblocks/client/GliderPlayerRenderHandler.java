package openblocks.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import openblocks.common.entity.EntityHangGlider;
import openmods.renderer.PlayerBodyRenderEvent;
import org.lwjgl.opengl.GL11;

public class GliderPlayerRenderHandler {

	@SubscribeEvent
	public void onPlayerBodyRender(PlayerBodyRenderEvent evt) {
		final AbstractClientPlayer player = evt.player;
		if (EntityHangGlider.isGliderDeployed(player)) {
			player.limbSwing = 0f;
			player.prevLimbSwingAmount = 0f;
			player.limbSwingAmount = 0f;
			GL11.glRotatef(75, -1, 0, 0);
		}
	}
}
