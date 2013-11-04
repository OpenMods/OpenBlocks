package openblocks.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class RenderUtils {

	public static void setupBillboard(Entity rve) {
		GL11.glRotatef(-rve.rotationYaw, 0, 1, 0);
		GL11.glRotatef(rve.rotationPitch, 1, 0, 0);
	}
	
	/**
	 * Please! For the love of sanity. Do not store this instance ANYWHERE!
	 * If you set it to a TE or Entity, Please remove it after you're done!
	 * THANK YOU!
	 * @return Returns a world for rendering with
	 */
	public static World getRenderWorld() {
		if(Minecraft.getMinecraft() != null) return Minecraft.getMinecraft().theWorld;
		return null;
	}

	public static double interpolatePos(double current, double prev, float partialTickTime) {
		return prev + partialTickTime * (current - prev);
	}

	public static void translateToPlayer(Entity e, float partialTickTime) {
		GL11.glTranslated(
				interpolatePos(e.posX, e.prevPosX, partialTickTime) - RenderManager.renderPosX,
				interpolatePos(e.posY, e.prevPosY, partialTickTime) - RenderManager.renderPosY,
				interpolatePos(e.posZ, e.prevPosZ, partialTickTime) - RenderManager.renderPosZ);
	}

	public static void translateToWorld(Entity e, float partialTickTime) {
		GL11.glTranslated(
				interpolatePos(e.posX, e.prevPosX, partialTickTime),
				interpolatePos(e.posY, e.prevPosY, partialTickTime),
				interpolatePos(e.posZ, e.prevPosZ, partialTickTime));
	}

}
