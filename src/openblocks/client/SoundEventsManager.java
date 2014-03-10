package openblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.PlayStreamingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.Config;
import openblocks.client.billboards.BillboardEventsManager;
import openblocks.common.item.ItemSonicGlasses;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class SoundEventsManager {

	private SoundEventsManager() {}

	public static final SoundEventsManager instance = new SoundEventsManager();

	public final SoundIconRegistry icons = new SoundIconRegistry();
	
	public void init() {
		icons.registerDefaults();
		MinecraftForge.EVENT_BUS.register(icons);
	}
	
	public static boolean isEntityWearingGlasses(Entity e) {
		if (e instanceof EntityPlayer) {
			ItemStack helmet = ((EntityPlayer)e).inventory.armorItemInSlot(3);
			return helmet != null
					&& helmet.getItem() instanceof ItemSonicGlasses;
		}

		return false;
	}

	public static boolean isPlayerWearingGlasses() {
		final Entity e = FMLClientHandler.instance().getClient().renderViewEntity;
		return isEntityWearingGlasses(e);
	}

	@ForgeSubscribe
	public void onSoundEvent(PlaySoundEvent evt) {
		if (SoundEventsManager.isPlayerWearingGlasses()) {
			BillboardEventsManager.instance.addEvent(evt.x, evt.y, evt.z, icons.getIcon(evt.name), Math.log(evt.volume + 1), 5 * evt.pitch);
		}
	}

	@ForgeSubscribe
	public void onSoundEvent(PlayStreamingEvent evt) {
		if (SoundEventsManager.isPlayerWearingGlasses()) {
			String soundName = SoundIconRegistry.CATEGORY_STREAMING + "."
					+ evt.name;
			BillboardEventsManager.instance.addEvent(evt.x, evt.y, evt.z, icons.getIcon(soundName), 1, 10);
		}
	}

	private Integer renderNotPumpkin;
	private static final ResourceLocation notPumpkin = new ResourceLocation("openblocks:textures/misc/glasses_obsidian.png");

	private void dimWorld(TextureManager tex, Minecraft mc) {
		final double level = Config.sonicGlassesOpacity;
		if (level <= 0) return;

		if (level >= 1 && !Config.sonicGlassesUseTexture) {
			GL11.glColor3f(0, 0, 0);
			GL11.glClearColor(0, 0, 0, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			return;
		}

		if (renderNotPumpkin == null) {
			renderNotPumpkin = GL11.glGenLists(1);

			GL11.glNewList(renderNotPumpkin, GL11.GL_COMPILE);
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();

			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glOrtho(-1, 1, -1, 1, -1, 1);

			GL11.glColor3f(1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			Tessellator tes = new Tessellator();
			tes.startDrawingQuads();

			final double maxU = (double)mc.displayWidth / 1024;
			final double maxV = (double)mc.displayHeight / 1024;

			if (Config.sonicGlassesUseTexture) {
				tes.setColorRGBA_F(1, 1, 1, (float)level);

				tex.bindTexture(notPumpkin);
				tes.addVertexWithUV(-1, -1, 0, 0, 0);
				tes.addVertexWithUV(+1, -1, 0, maxU, 0);
				tes.addVertexWithUV(+1, +1, 0, maxU, maxV);
				tes.addVertexWithUV(-1, +1, 0, 0, maxV);

				tes.draw();
			} else {
				tes.setColorRGBA_F(0.085f, 0.074f, 0.129f, (float)level);
				tes.addVertex(-1, -1, 0);
				tes.addVertex(+1, -1, 0);
				tes.addVertex(+1, +1, 0);
				tes.addVertex(-1, +1, 0);

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				tes.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_LIGHTING);

			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glPopMatrix();
			GL11.glEndList();
		}
		
		GL11.glCallList(renderNotPumpkin);
	}

	@Override
	protected void finalize() throws Throwable {
		if (renderNotPumpkin != null) GL11.glDeleteLists(renderNotPumpkin, 1);
	}

	@ForgeSubscribe
	public void renderEvents(RenderWorldLastEvent evt) {
		final Minecraft mc = evt.context.mc;

		if (mc.gameSettings.thirdPersonView != 0) return;
		final TextureManager tex = evt.context.renderEngine;
		final Entity rve = mc.renderViewEntity;
		if (!isEntityWearingGlasses(rve)) return;

		GL11.glDisable(GL11.GL_FOG);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

		dimWorld(tex, mc);
	}
}
