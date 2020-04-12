package openblocks.client;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.Icons.IDrawableIcon;
import openblocks.common.item.ItemSonicGlasses;
import openmods.config.properties.ConfigurationChange;
import openmods.renderer.ManualDisplayList;
import openmods.utils.TextureUtils;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class SoundEventsManager {

	private SoundEventsManager() {}

	public void init() {
		icons.registerDefaults();
		MinecraftForge.EVENT_BUS.register(icons);
	}

	public static final SoundEventsManager instance = new SoundEventsManager();
	public final SoundIconRegistry icons = new SoundIconRegistry();

	private static class SoundEvent {
		public final IDrawableIcon icon;
		public final double size;

		public final ISound sound;

		private boolean isPlaying = true;

		private int ticks;
		private final int TTL;

		private SoundEvent(ISound sound, IDrawableIcon icon, double size, double TTL) {
			this.sound = sound;
			this.icon = icon;
			this.size = size;

			this.TTL = MathHelper.floor(20 * TTL);
		}

		public void update(SoundHandler handler) {
			if (isPlaying) {
				isPlaying = handler.isSoundPlaying(sound);
			} else {
				ticks++;
			}
		}

		public boolean isAlive() {
			return ticks <= TTL;
		}

		public double getAlpha(double partialTick) {
			return 1 - (ticks + partialTick) / TTL;
		}
	}

	private final List<SoundEvent> events = Lists.newLinkedList();

	public static boolean isEntityWearingGlasses(Entity e) {
		if (e instanceof PlayerEntity) {
			ItemStack helmet = ((PlayerEntity)e).getItemStackFromSlot(EquipmentSlotType.HEAD);
			return !helmet.isEmpty() && helmet.getItem() instanceof ItemSonicGlasses;
		}

		return false;
	}

	public static boolean isPlayerWearingGlasses() {
		final Entity e = Minecraft.getMinecraft().getRenderViewEntity();
		return isEntityWearingGlasses(e);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onSoundEvent(PlaySoundEvent evt) {
		if (SoundEventsManager.isPlayerWearingGlasses()) {
			final ISound sound = evt.getResultSound();
			if (sound != null) {
				// NOTE do not remove, otherwise sound.getVolume will throw NPE
				if (sound.createAccessor(evt.getManager().sndHandler) != null) {
					final IDrawableIcon icon = icons.getIcon(sound.getSoundLocation());

					synchronized (events) {
						events.add(new SoundEvent(sound, icon, Math.log(sound.getVolume() + 1), sound.getPitch()));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onReconfig(ConfigurationChange.Post evt) {
		if (evt.category.equals("glasses")) notPumpkinOverlay.invalidate();
	}

	public void tickUpdate() {
		synchronized (events) {
			final SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
			Iterator<SoundEvent> it = events.iterator();

			while (it.hasNext()) {
				SoundEvent evt = it.next();
				evt.update(handler);
				if (!evt.isAlive()) it.remove();
			}
		}
	}

	private final ManualDisplayList notPumpkinOverlay = new ManualDisplayList();
	private static final ResourceLocation notPumpkin = OpenBlocks.location("textures/misc/glasses_obsidian.png");

	private void dimWorld(final TextureManager tex, final Minecraft mc) {
		final double level = Config.sonicGlassesOpacity;
		if (level <= 0) return;

		if (level >= 1 && !Config.sonicGlassesUseTexture) {
			GlStateManager.color(0, 0, 0);
			GL11.glClearColor(0, 0, 0, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			return;
		}

		if (!notPumpkinOverlay.isCompiled()) {
			notPumpkinOverlay.compile(() -> {
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPushMatrix();
				GL11.glLoadIdentity();

				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glPushMatrix();
				GL11.glLoadIdentity();
				GL11.glOrtho(-1, 1, -1, 1, -1, 1);

				final float maxU = (float)mc.displayWidth / 1024;
				final float maxV = (float)mc.displayHeight / 1024;

				if (Config.sonicGlassesUseTexture) {
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
					GL11.glBegin(GL11.GL_QUADS);
					GL11.glVertex3f(-1, -1, 0);
					GL11.glVertex3f(+1, -1, 0);
					GL11.glVertex3f(+1, +1, 0);
					GL11.glVertex3f(-1, +1, 0);
					GL11.glEnd();
				}

				GL11.glPopMatrix();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glPopMatrix();
			});
		}

		GlStateManager.disableAlpha();

		if (Config.sonicGlassesUseTexture) {
			GlStateManager.enableTexture2D();
			tex.bindTexture(notPumpkin);
			GlStateManager.color(1, 1, 1, (float)level);
		} else {
			GlStateManager.disableTexture2D();
			GlStateManager.color(0.085f, 0.074f, 0.129f, (float)level);
		}

		notPumpkinOverlay.render();

		GlStateManager.enableAlpha();
	}

	@SubscribeEvent
	public void renderEvents(RenderWorldLastEvent evt) {
		final Minecraft mc = Minecraft.getMinecraft();

		if (mc.gameSettings.thirdPersonView != 0) return;
		final TextureManager tex = mc.renderEngine;
		final Entity rve = mc.getRenderViewEntity();
		if (!isEntityWearingGlasses(rve)) return;

		GlStateManager.disableFog();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GlStateManager.disableLighting();
		GlStateManager.disableDepth();

		dimWorld(tex, mc);

		final float partialTicks = evt.getPartialTicks();
		final double interpX = rve.prevPosX + (rve.posX - rve.prevPosX) * partialTicks;
		final double interpY = rve.prevPosY + (rve.posY - rve.prevPosY) * partialTicks;
		final double interpZ = rve.prevPosZ + (rve.posZ - rve.prevPosZ) * partialTicks;

		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
		TextureUtils.bindTextureToClient(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		synchronized (events) {
			for (SoundEvent snd : events) {
				final double px = snd.sound.getXPosF() - interpX;
				final double py = snd.sound.getYPosF() - interpY;
				final double pz = snd.sound.getZPosF() - interpZ;

				GL11.glPushMatrix();
				GL11.glTranslated(px, py, pz);
				RenderUtils.setupBillboard(rve);
				snd.icon.draw(snd.getAlpha(partialTicks), snd.size);
				GL11.glPopMatrix();
			}
		}
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.enableDepth();
	}
}
