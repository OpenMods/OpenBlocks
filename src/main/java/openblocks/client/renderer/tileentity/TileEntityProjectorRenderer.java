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

	private final static ResourceLocation TEXTURE = new ResourceLocation("openblocks", "textures/models/projector.png");
	private static final float BLOCK_CENTRE_TRANSLATION = 0.5F;

	private static ModelProjector model = new ModelProjector();

	@Override
	public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float partialTickTime) {
		int pass = MinecraftForgeClient.getRenderPass();
		final TileEntityProjector projector = (TileEntityProjector)te;

		GL11.glPushMatrix();
		GL11.glTranslated(x + BLOCK_CENTRE_TRANSLATION, y, z + BLOCK_CENTRE_TRANSLATION);
		GL11.glRotated(90 * projector.rotation(), 0, 1, 0);
		GL11.glTranslated(-BLOCK_CENTRE_TRANSLATION, 0, -BLOCK_CENTRE_TRANSLATION);
		GL11.glColor3f(1, 1, 1);
		int mapId = projector.mapId();
		if (pass <= 0) {
			renderProjector(projector, partialTickTime, mapId >= 0);
		} else {
			renderMap(projector, mapId);
		}

		GL11.glPopMatrix();
	}

	private static void renderMap(final TileEntityProjector projector, final int mapId) {
		if (projector.getWorldObj() != null) {
			HeightMapData data = MapDataManager.getMapData(projector.getWorldObj(), mapId);
			if (data.isValid()) {
				GL11.glTranslatef(0, 1, 0);
				HeightMapRenderer.instance.render(mapId, data);
			}
		}
	}

	private void renderProjector(final TileEntityProjector projector, final float partialTickTime, final boolean active) {
		GL11.glTranslated(BLOCK_CENTRE_TRANSLATION / 2, BLOCK_CENTRE_TRANSLATION, BLOCK_CENTRE_TRANSLATION / 2);
		bindTexture(TEXTURE);
		if (active) {
			long ticks = OpenMods.proxy.getTicks(projector.getWorldObj());
			model.render(ticks * 0.01f, ticks * 0.3f, 0.25f * MathHelper.sin(ticks * 0.005f) + 0.25f);
		} else {
			model.render(0, 0, 0);
		}
	}
}
