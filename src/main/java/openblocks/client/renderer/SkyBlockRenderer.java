package openblocks.client.renderer;

import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.FMLClientHandler;
import openblocks.Config;
import openmods.Log;
import openmods.renderer.PreWorldRenderHookVisitor;
import openmods.utils.render.FramebufferBlitter;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class SkyBlockRenderer {

	public static final SkyBlockRenderer INSTANCE = new SkyBlockRenderer();

	private Framebuffer skyFb;

	private boolean isInitialized;

	private boolean isActive;

	private int stencilMask = -1;

	private int lastRenderUsers;

	private boolean skyCaptured;

	public boolean hasSkyTexture() {
		return isActive && skyCaptured;
	}

	public int getStencilMask() {
		return stencilMask;
	}

	private class SkyCapture implements Runnable {

		@Override
		public void run() {
			// There may be single frame, when we have users, but no sky. That's acceptable IMO
			skyCaptured = lastRenderUsers > 0;
			if (!skyCaptured) return;
			lastRenderUsers = 0;

			final Framebuffer mcFb = Minecraft.getMinecraft().getFramebuffer();

			if (skyFb == null) {
				skyFb = new Framebuffer(mcFb.framebufferWidth, mcFb.framebufferHeight, false);
			} else if (skyFb.framebufferWidth != mcFb.framebufferWidth ||
					skyFb.framebufferHeight != mcFb.framebufferHeight) {
				skyFb.createBindFramebuffer(mcFb.framebufferWidth, mcFb.framebufferHeight);
			}

			FramebufferBlitter.INSTANCE.blitFramebuffer(mcFb, skyFb);

			mcFb.bindFramebuffer(false);
		}
	}

	public void incrementUsers() {
		lastRenderUsers++;
	}

	public void setup() {
		Preconditions.checkState(!isInitialized, "Double initialization");
		isInitialized = true;

		if (!Config.renderSkyBlocks) {
			Log.info("Disabled by config");
			return;
		}

		if (FMLClientHandler.instance().hasOptifine()) {
			if (Config.skyBlocksOptifineOverride) {
				Log.warn("Optifine detected: skyblocks + shaders may hang your game");
			} else {
				Log.info("Disabled due to Optifine (use `optifineOverride` config to override)");
				return;
			}
		}

		if (!OpenGlHelper.isFramebufferEnabled()) {
			Log.info("Framebuffer not enabled");
			return;
		}

		if (!FramebufferBlitter.INSTANCE.isValid()) {
			Log.info("Framebuffer blit not enabled");
			return;
		}

		if (!PreWorldRenderHookVisitor.isActive()) {
			Log.info("Pre-world render hook not active");
			return;
		}

		final Framebuffer mcFb = Minecraft.getMinecraft().getFramebuffer();

		if (!mcFb.isStencilEnabled() && !mcFb.enableStencil()) {
			Log.info("Stencil not enabled");
			return;
		}

		final int stencilBit = MinecraftForgeClient.reserveStencilBit();
		if (stencilBit < 0) {
			Log.info("All stencil bits reserved");
			return;
		}

		PreWorldRenderHookVisitor.setHook(new SkyCapture());
		Log.debug("Sky block rendering initialized correctly, stencilBit = %d", stencilBit);
		stencilMask = 1 << stencilBit;
		isActive = true;
	}

	public void renderSkyTexture() {
		if (!isActive) return;

		final Minecraft mc = Minecraft.getMinecraft();
		final Framebuffer mcFb = mc.getFramebuffer();

		GlStateManager.disableFog();
		RenderUtils.disableLightmap();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();

		GlStateManager.depthMask(false);
		skyFb.framebufferRender(mc.displayWidth, mc.displayHeight);
		GlStateManager.enableDepth();
		GlStateManager.viewport(0, 0, mcFb.framebufferWidth, mcFb.framebufferHeight);

		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		RenderUtils.enableLightmap();
		GlStateManager.enableFog();
	}

}
