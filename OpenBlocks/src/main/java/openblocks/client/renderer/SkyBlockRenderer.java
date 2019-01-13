package openblocks.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.FMLClientHandler;
import openblocks.Config;
import openmods.Log;
import openmods.reflection.MethodAccess;
import openmods.reflection.MethodAccess.Function0;
import openmods.renderer.PreWorldRenderHookVisitor;
import openmods.utils.render.FramebufferBlitter;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class SkyBlockRenderer implements IResourceManagerReloadListener {

	public static final SkyBlockRenderer INSTANCE = new SkyBlockRenderer();

	private Framebuffer skyFb;

	private boolean isActive;

	private int stencilBit = -1;

	private int stencilMask;

	private int lastRenderUsers;

	private boolean skyCaptured;

	public boolean hasSkyTexture() {
		return isActive && skyCaptured;
	}

	public int getStencilMask() {
		return stencilMask;
	}

	private static final Runnable NULL_HOOK = () -> {};

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

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		final boolean canActivate = checkActivationConditions();

		if (canActivate) {
			if (!isActive) {
				isActive = activate();
			}
		} else {
			if (isActive) {
				deactivate();
				isActive = false;
			}
		}
	}

	private static boolean checkActivationConditions() {
		if (!Config.renderSkyBlocks) {
			Log.info("Disabled by config");
			return false;
		}

		if (FMLClientHandler.instance().hasOptifine() && optifineShadersEnabled()) {
			if (Config.skyBlocksOptifineOverride) {
				Log.warn("Optifine detected: skyblocks + shaders may hang your game");
			} else {
				Log.info("Disabled due to Optifine shaders (use `optifineOverride` config to override)");
				return false;
			}
		}

		if (!OpenGlHelper.isFramebufferEnabled()) {
			Log.info("Framebuffer not enabled");
			return false;
		}

		if (!FramebufferBlitter.INSTANCE.isValid()) {
			Log.info("Framebuffer blit not enabled");
			return false;
		}

		if (!PreWorldRenderHookVisitor.isActive()) {
			Log.info("Pre-world render hook not active");
			return false;
		}

		final Framebuffer mcFb = Minecraft.getMinecraft().getFramebuffer();

		if (!mcFb.isStencilEnabled() && !mcFb.enableStencil()) {
			Log.info("Stencil not enabled");
			return false;
		}

		return true;
	}

	private static boolean optifineShadersEnabled() {
		try {
			final Class<?> config = Class.forName("Config");
			final Function0<Boolean> isShaders = MethodAccess.create(boolean.class, config, "isShaders");
			return isShaders.call(null);
		} catch (Exception e) {
			Log.info(e, "Failed to read Optifine config");
		}

		// can't tell, assume the worst
		return true;
	}

	private boolean activate() {
		stencilBit = MinecraftForgeClient.reserveStencilBit();
		if (stencilBit < 0) {
			Log.info("All stencil bits reserved");
			return false;
		}

		PreWorldRenderHookVisitor.setHook(new SkyCapture());
		Log.debug("Sky block rendering initialized correctly, stencilBit = %d", stencilBit);
		stencilMask = 1 << stencilBit;

		return true;
	}

	private void deactivate() {
		PreWorldRenderHookVisitor.setHook(NULL_HOOK);

		if (stencilBit >= 0) {
			MinecraftForgeClient.releaseStencilBit(stencilBit);
			stencilBit = -1;
		}

		Log.debug("Sky block rendering deactivated");
	}

	public void bindSkyTexture() {
		skyFb.bindFramebufferTexture();
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
