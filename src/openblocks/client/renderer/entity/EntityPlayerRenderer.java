package openblocks.client.renderer.entity;

import openblocks.client.ClientTickHandler;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

public class EntityPlayerRenderer extends RenderPlayer {

	@Override
	protected void rotatePlayer(EntityPlayer player, float par2, float par3, float par4) {
		super.rotatePlayer(player, par2, par3, par4);
		if (player == Minecraft.getMinecraft().thePlayer && ClientTickHandler.isBeingDragged) {
			GL11.glRotatef(90, 1, 0, 0);
		}
    }
}
