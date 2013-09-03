package openblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.utils.CompatibilityUtils;

import org.lwjgl.opengl.GL11;


public class ClientEventHandler {

	public ClientEventHandler() {

	}

	@ForgeSubscribe
	public void onRenderGameOverlay(RenderGameOverlayEvent evt) {
		if (true) return;

		if (evt.type == ElementType.CROSSHAIRS
				&& evt instanceof RenderGameOverlayEvent.Pre) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			Minecraft mc = Minecraft.getMinecraft();
			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glPushMatrix();
			GL11.glTranslatef(200, 100, 100.0F);
			GL11.glScalef(350, 350, 350);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			RenderHelper.enableStandardItemLighting();
			RenderManager.instance.playerViewY = 180.0F;
			RenderManager.instance.renderEntityWithPosYaw(mc.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
			GL11.glPopMatrix();
			RenderHelper.disableStandardItemLighting();

			ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
			int width = scaledresolution.getScaledWidth();
			int height = scaledresolution.getScaledHeight();
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(0.5F, 0, 0, 1.0F);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			CompatibilityUtils.bindTextureToClient("textures/gui/blur.png");
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(0.0D, (double)height, -90.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV((double)width, (double)height, -90.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV((double)width, 0.0D, -90.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
			tessellator.draw();
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_BLEND);

		}
	}
	
	@ForgeSubscribe
	public void onRenderWorldLast(RenderWorldLastEvent evt) {
		SoundEventsManager.instance.renderEvents(evt);
	}
}
