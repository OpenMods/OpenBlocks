package openblocks.client;

import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import openmods.Log;
import openmods.renderer.StencilRendererHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

public class StencilSkyRenderer extends StencilRendererHandler {

	private final Method setupFogMethod;

	private boolean setupFogFailed;

	private final int stencilMask;

	private void setupFog(float partialTickTime) {
		if (setupFogFailed) return;
		EntityRenderer re = Minecraft.getMinecraft().entityRenderer;
		try {
			setupFogMethod.invoke(re, -1, partialTickTime);
		} catch (Throwable t) {
			Log.warn(t, "Can't setup fog. Oh, well...");
		}
	}

	public StencilSkyRenderer(int stencilMask) {
		this.stencilMask = stencilMask;

		Method m = null;
		try {
			m = ReflectionHelper.findMethod(EntityRenderer.class, null, ArrayUtils.toArray("setupFog", "func_78468_a"), int.class, float.class);
		} catch (Throwable t) {
			Log.warn(t, "Failed to get method 'setupFog'");
			setupFogFailed = true;
		}
		setupFogMethod = m;
	}

	@Override
	public void render(RenderGlobal context, float partialTickTime) {
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glStencilMask(stencilMask);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);

		GlStateManager.disableDepth();
		GlStateManager.disableLighting();

		setupFog(partialTickTime);
		context.renderSky(partialTickTime, 0); // TODO 1.8.9 verify

		GL11.glClearStencil(0);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glPopAttrib();
	}

}
