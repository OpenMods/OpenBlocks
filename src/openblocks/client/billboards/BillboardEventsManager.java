package openblocks.client.billboards;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.client.Icons.IDrawableIcon;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class BillboardEventsManager {

	public static final BillboardEventsManager instance = new BillboardEventsManager();

	private final List<BillboardEvent> events = Lists.newLinkedList();
	
	public void addEvent(float x, float y, float z, IDrawableIcon icon, double size, double time) {
		events.add(new BillboardEvent(x, y, z, icon, size, time));
	}
	
	public void tickUpdate() {
		Iterator<BillboardEvent> it = events.iterator();
		while (it.hasNext()) {
			BillboardEvent evt = it.next();
			evt.update();
			if (!evt.isAlive()) it.remove();
		}
	}
	
	@ForgeSubscribe(priority = EventPriority.LOW)
	public void renderEvents(RenderWorldLastEvent evt) {
		
		final Minecraft mc = evt.context.mc;

		if (mc.gameSettings.thirdPersonView != 0) return;
		final TextureManager tex = evt.context.renderEngine;
		final Entity rve = mc.renderViewEntity;

		final double interpX = rve.prevPosX + (rve.posX - rve.prevPosX)
				* evt.partialTicks;
		final double interpY = rve.prevPosY + (rve.posY - rve.prevPosY)
				* evt.partialTicks;
		final double interpZ = rve.prevPosZ + (rve.posZ - rve.prevPosZ)
				* evt.partialTicks;

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (BillboardEvent event : events) {
			final double px = event.x - interpX;
			final double py = event.y - interpY;
			final double pz = event.z - interpZ;

			GL11.glPushMatrix();
			GL11.glTranslated(px, py, pz);
			RenderUtils.setupBillboard(rve);
			event.icon.draw(tex, event.getTime(evt.partialTicks), event.size);
			GL11.glPopMatrix();
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
