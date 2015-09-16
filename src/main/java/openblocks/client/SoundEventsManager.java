package openblocks.client;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;
import openblocks.Config;
import openblocks.client.Icons.IDrawableIcon;
import openblocks.common.item.ItemSonicGlasses;
import openmods.config.properties.ConfigurationChange;
import openmods.renderer.ManualDisplayList;
import openmods.renderer.ManualDisplayList.Renderer;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SoundEventsManager {

	private SoundEventsManager() {}

	public void init() {
		icons.registerDefaults();
		MinecraftForge.EVENT_BUS.register(icons);
	}

	public static final SoundEventsManager instance = new SoundEventsManager();
	public final SoundIconRegistry icons = new SoundIconRegistry();

	private static class SoundEvent {
		public final float x, y, z;
		public final IDrawableIcon icon;
		public final double size;

		private double time;
		private final double timeDeltaPerTick;

		private SoundEvent(float x, float y, float z, IDrawableIcon icon, double size, double TTL) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.icon = icon;
			this.size = size;

			time = 1;
			timeDeltaPerTick = 1 / (TTL * 20);
		}

		public void update() {
			time -= timeDeltaPerTick;
		}

		public boolean isAlive() {
			return time >= 0;
		}

		public double getTime(double partialTick) {
			return time - timeDeltaPerTick * partialTick;
		}
	}

	private final List<SoundEvent> events = Lists.newLinkedList();

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

	private void addEvent(float x, float y, float z, ResourceLocation sound, double size, double time) {
		IDrawableIcon icon = icons.getIcon(sound);

		synchronized (events) {
			events.add(new SoundEvent(x, y, z, icon, size, time));
		}
	}

	@SubscribeEvent
	public void onSoundEvent(PlaySoundEvent17 evt) {
		if (SoundEventsManager.isPlayerWearingGlasses()) {
			ISound sound = evt.sound;
			addEvent(sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), sound.getPositionedSoundLocation(), Math.log(sound.getVolume() + 1), 5 * sound.getPitch());
		}
	}

	@SubscribeEvent
	public void onReconfig(ConfigurationChange.Post evt) {
		if (evt.category.equals("glasses")) notPumpkinOverlay.invalidate();
	}

	public void tickUpdate() {
		synchronized (events) {
			Iterator<SoundEvent> it = events.iterator();
			while (it.hasNext()) {
				SoundEvent evt = it.next();
				evt.update();
				if (!evt.isAlive()) it.remove();
			}
		}
	}

	private ManualDisplayList notPumpkinOverlay = new ManualDisplayList();
	private static final ResourceLocation notPumpkin = new ResourceLocation("openblocks:textures/misc/glasses_obsidian.png");

	private void dimWorld(final TextureManager tex, final Minecraft mc) {
		final double level = Config.sonicGlassesOpacity;
		if (level <= 0) return;

		if (level >= 1 && !Config.sonicGlassesUseTexture) {
			GL11.glColor3f(0, 0, 0);
			GL11.glClearColor(0, 0, 0, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			return;
		}

		if (!notPumpkinOverlay.isCompiled()) {
			notPumpkinOverlay.compile(new Renderer() {
				@Override
				public void render() {
					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();

					GL11.glMatrixMode(GL11.GL_PROJECTION);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();
					GL11.glOrtho(-1, 1, -1, 1, -1, 1);

					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					final float maxU = (float)mc.displayWidth / 1024;
					final float maxV = (float)mc.displayHeight / 1024;

					if (Config.sonicGlassesUseTexture) {
						GL11.glColor4f(1, 1, 1, (float)level);
						tex.bindTexture(notPumpkin);
						GL11.glBegin(GL11.GL_QUADS);

						GL11.glTexCoord2f(0, 0);
						GL11.glVertex3f(-1, -1, 0);

						GL11.glTexCoord2f(maxU, 0);
						GL11.glVertex3f(+1, -1, 0);

						GL11.glTexCoord2f(maxU, maxV);
						GL11.glVertex3f(+1, +1, 0);

						GL11.glTexCoord2f(0, maxV);
						GL11.glVertex3f(-1, +1, 0);
						GL11.glEnd();
					} else {
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glColor4f(0.085f, 0.074f, 0.129f, (float)level);
						GL11.glBegin(GL11.GL_QUADS);
						GL11.glVertex3f(-1, -1, 0);
						GL11.glVertex3f(+1, -1, 0);
						GL11.glVertex3f(+1, +1, 0);
						GL11.glVertex3f(-1, +1, 0);
						GL11.glEnd();
						GL11.glEnable(GL11.GL_TEXTURE_2D);
					}

					GL11.glDisable(GL11.GL_BLEND);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_LIGHTING);

					GL11.glPopMatrix();
					GL11.glMatrixMode(GL11.GL_MODELVIEW);
					GL11.glPopMatrix();
				}
			});
		}

		notPumpkinOverlay.render();
	}

	@SubscribeEvent
	public void renderEvents(RenderWorldLastEvent evt) {
		final Minecraft mc = Minecraft.getMinecraft();

		if (mc.gameSettings.thirdPersonView != 0) return;
		final TextureManager tex = mc.renderEngine;
		final Entity rve = mc.renderViewEntity;
		if (!isEntityWearingGlasses(rve)) return;

		GL11.glDisable(GL11.GL_FOG);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		dimWorld(tex, mc);

		final double interpX = rve.prevPosX + (rve.posX - rve.prevPosX) * evt.partialTicks;
		final double interpY = rve.prevPosY + (rve.posY - rve.prevPosY) * evt.partialTicks;
		final double interpZ = rve.prevPosZ + (rve.posZ - rve.prevPosZ) * evt.partialTicks;

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		synchronized (events) {
			for (SoundEvent snd : events) {
				final double px = snd.x - interpX;
				final double py = snd.y - interpY;
				final double pz = snd.z - interpZ;

				GL11.glPushMatrix();
				GL11.glTranslated(px, py, pz);
				RenderUtils.setupBillboard(rve);
				snd.icon.draw(tex, snd.getTime(evt.partialTicks), snd.size);
				GL11.glPopMatrix();
			}
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
