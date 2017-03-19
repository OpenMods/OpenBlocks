package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.client.StencilSkyRenderer;
import openblocks.common.block.BlockSky;
import openblocks.common.tileentity.TileEntitySky;
import openmods.Log;
import openmods.colors.RGB;
import openmods.renderer.StencilRendererHandler;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class TileEntitySkyRenderer extends TileEntitySpecialRenderer<TileEntitySky> {

	private boolean initialized;

	private int displayListBase;
	private StencilRendererHandler handler;

	@Override
	public void renderTileEntityAt(TileEntitySky te, double x, double y, double z, float partialTickTime, int destroyStage) {
		if (!BlockSky.isActive(te.getWorld().getBlockState(te.getPos()))) return;

		if (!initialized) {
			intialize();
			initialized = true;
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		RGB fog = RenderUtils.getFogColor();
		GlStateManager.color(fog.getR(), fog.getG(), fog.getB());
		GL11.glCallList(displayListBase + MinecraftForgeClient.getRenderPass()); // fancy!

		GL11.glPopMatrix();
		handler.markForRender();

	}

	protected void intialize() {
		displayListBase = GL11.glGenLists(2);

		final Tessellator tes = new Tessellator(6 * 4 * 3 * 8);
		GL11.glNewList(displayListBase, GL11.GL_COMPILE);
		renderCube(tes);
		GL11.glEndList();

		GL11.glNewList(displayListBase + 1, GL11.GL_COMPILE);

		final int stencilBit = MinecraftForgeClient.reserveStencilBit();

		if (stencilBit >= 0) {
			Log.debug("Stencil bit %d allocated for skyblock rendering", stencilBit);
			cutHoleInWorld(tes, 1 << stencilBit);
		} else {
			Log.warn("Failed to allocate stencil bit for skyblock rendering");
		}
		GL11.glEndList();

		handler = stencilBit >= 0? new StencilSkyRenderer(1 << stencilBit) : StencilRendererHandler.DUMMY;
	}

	private static void addVertex(VertexBuffer wr, double x, double y, double z) {
		wr.pos(x, y, z).endVertex();
	}

	private static void renderCube(Tessellator tes) {
		final VertexBuffer wr = tes.getBuffer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

		addVertex(wr, 0, 0, 0);
		addVertex(wr, 0, 1, 0);
		addVertex(wr, 1, 1, 0);
		addVertex(wr, 1, 0, 0);

		addVertex(wr, 0, 0, 1);
		addVertex(wr, 1, 0, 1);
		addVertex(wr, 1, 1, 1);
		addVertex(wr, 0, 1, 1);

		addVertex(wr, 0, 0, 0);
		addVertex(wr, 0, 0, 1);
		addVertex(wr, 0, 1, 1);
		addVertex(wr, 0, 1, 0);

		addVertex(wr, 1, 0, 0);
		addVertex(wr, 1, 1, 0);
		addVertex(wr, 1, 1, 1);
		addVertex(wr, 1, 0, 1);

		addVertex(wr, 0, 0, 0);
		addVertex(wr, 1, 0, 0);
		addVertex(wr, 1, 0, 1);
		addVertex(wr, 0, 0, 1);

		addVertex(wr, 0, 1, 0);
		addVertex(wr, 0, 1, 1);
		addVertex(wr, 1, 1, 1);
		addVertex(wr, 1, 1, 0);

		GlStateManager.disableLighting();
		GlStateManager.disableTexture2D();
		RenderUtils.disableLightmap();
		tes.draw();
		RenderUtils.enableLightmap();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
	}

	private static void cutHoleInWorld(Tessellator tes, int stencilMask) {
		GL11.glStencilMask(stencilMask);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
		GlStateManager.colorMask(false, false, false, false);
		renderCube(tes);
		GlStateManager.colorMask(true, true, true, true);
		GL11.glStencilMask(0);
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
}
