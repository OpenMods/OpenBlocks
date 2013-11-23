package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.client.model.ModelProjector;
import openblocks.client.renderer.HeightMapRenderer;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;
import openblocks.common.tileentity.TileEntityProjector;
import openmods.OpenMods;

import org.lwjgl.opengl.GL11;

public class TileEntityProjectorRenderer extends TileEntitySpecialRenderer {

	private final static ResourceLocation texture = new ResourceLocation("openblocks:textures/models/projector.png");

	private static ModelProjector model = new ModelProjector();

	public static void reload() {
		model = new ModelProjector();
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTickTime) {
		int pass = MinecraftForgeClient.getRenderPass();
		final TileEntityProjector projector = (TileEntityProjector)te;

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glRotated(90 * projector.rotation(), 0, 1, 0);
		GL11.glTranslated(-0.5, 0, -0.5);
		GL11.glColor3f(1, 1, 1);
		int mapId = projector.mapId();
		if (pass <= 0) renderProjector(projector, partialTickTime, mapId >= 0);
		else renderMap(projector, mapId);

		GL11.glPopMatrix();
	}

	private static void renderMap(final TileEntityProjector projector, int mapId) {
		if (projector.worldObj != null) {
			HeightMapData data = MapDataManager.getMapData(projector.worldObj, mapId);
			if (data.isValid()) {
				GL11.glTranslatef(0, 1, 0);
				HeightMapRenderer.instance.render(mapId, data);
			}
		}
	}

	private void renderProjector(TileEntityProjector projector, float partialTickTime, boolean active) {
		GL11.glTranslated(0.25, 0.5, 0.25);
		bindTexture(texture);
		if (active) {
			long ticks = OpenMods.proxy.getTicks(projector.worldObj);
			model.render(ticks * 0.01f, ticks * 0.3f, 0.25f * MathHelper.sin(ticks * 0.005f) + 0.25f);
		} else {
			model.render(0, 0, 0);
		}
	}

}
