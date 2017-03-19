package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelProjector;
import openblocks.client.renderer.HeightMapRenderer;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;
import openblocks.common.tileentity.TileEntityProjector;
import openmods.OpenMods;
import org.lwjgl.opengl.GL11;

public class TileEntityProjectorRenderer extends TileEntitySpecialRenderer<TileEntityProjector> {

	private final static ResourceLocation texture = OpenBlocks.location("textures/models/projector.png");

	private static final float BLOCK_CENTRE_TRANSLATION = 0.5F;

	private static ModelProjector model = new ModelProjector();

	@Override
	public void renderTileEntityAt(TileEntityProjector projector, double x, double y, double z, float partialTickTime, int destroyProgess) {
		int pass = MinecraftForgeClient.getRenderPass();

		GL11.glPushMatrix();
		GL11.glTranslated(x + BLOCK_CENTRE_TRANSLATION, y, z + BLOCK_CENTRE_TRANSLATION);
		GL11.glRotated(90 * projector.rotation(), 0, 1, 0);

		GL11.glTranslated(-BLOCK_CENTRE_TRANSLATION, 0, -BLOCK_CENTRE_TRANSLATION);
		GlStateManager.color(1, 1, 1);

		int mapId = projector.mapId();
		if (pass <= 0) {
			renderProjector(projector, partialTickTime, mapId >= 0);
		} else {
			renderMap(projector, mapId);
		}

		GL11.glPopMatrix();
	}

	private static void renderMap(final TileEntityProjector projector, int mapId) {
		final World world = projector.getWorld();
		if (world != null) {
			HeightMapData data = MapDataManager.getMapData(world, mapId);
			if (data.isValid()) {
				GL11.glTranslatef(0, 1, 0);
				HeightMapRenderer.instance.render(mapId, data);
			}
		}
	}

	private void renderProjector(final TileEntityProjector projector, final float partialTickTime, final boolean active) {
		GL11.glTranslated(BLOCK_CENTRE_TRANSLATION / 2, BLOCK_CENTRE_TRANSLATION, BLOCK_CENTRE_TRANSLATION / 2);
		bindTexture(texture);
		if (active) {
			long ticks = OpenMods.proxy.getTicks(projector.getWorld());
			model.render(ticks * 0.01f, ticks * 0.3f, 0.25f * MathHelper.sin(ticks * 0.005f) + 0.25f);
		} else {
			model.render(0, 0, 0);
		}
	}
}
