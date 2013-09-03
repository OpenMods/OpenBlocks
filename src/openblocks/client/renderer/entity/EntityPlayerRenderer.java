package openblocks.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import openblocks.OpenBlocks;

import org.lwjgl.opengl.GL11;

public class EntityPlayerRenderer extends RenderPlayer {

	@Override
	protected void rotatePlayer(AbstractClientPlayer player, float par2, float par3, float par4) {
		super.rotatePlayer(player, par2, par3, par4);
		if (OpenBlocks.proxy.gliderClientMap.containsKey(player)
				&& !player.onGround) {
			player.limbSwing = 0f;
			player.prevLimbYaw = 0f;
			player.limbYaw = 0f;
			GL11.glRotatef(75, -1, 0, 0);
		}
	}
}
