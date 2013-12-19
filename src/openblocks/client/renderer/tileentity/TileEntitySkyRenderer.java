package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.client.StencilSkyRenderer;
import openblocks.common.tileentity.TileEntitySky;
import openmods.renderer.StencilRendererHandler;
import openmods.utils.ColorUtils.RGB;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class TileEntitySkyRenderer extends TileEntitySpecialRenderer {

	private static final double Z_FIGHTER = 0.001;
	private static final double DM = 0 - Z_FIGHTER;
	private static final double DP = 1 + Z_FIGHTER;

	private boolean disableStencil;
	private boolean initialized;

	private int stencilDisplayList;
	private StencilRendererHandler handler;

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTickTime) {
		if (disableStencil) return;

		TileEntitySky sky = (TileEntitySky)te;
		if (!sky.isPowered()) return;

		if (!initialized) {
			intialize();
			initialized = true;
		}

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		RGB fog = RenderUtils.getFogColor();
		GL11.glColor3ub(fog.r, fog.g, fog.b);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glCallList(stencilDisplayList);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
		handler.markForRender();

	}

	protected void intialize() {
		final int stencilBit = MinecraftForgeClient.reserveStencilBit();

		if (stencilBit >= 0) {
			final int mask = 1 << stencilBit;

			stencilDisplayList = GL11.glGenLists(1);
			GL11.glNewList(stencilDisplayList, GL11.GL_COMPILE);
			cutHoleInWorld(mask);
			GL11.glEndList();

			handler = new StencilSkyRenderer(mask);
		} else disableStencil = true;
	}

	private static void cutHoleInWorld(int stencilMask) {
		final Tessellator tes = new Tessellator();
		tes.startDrawingQuads();

		tes.addVertex(DM, DM, DM);
		tes.addVertex(DM, DP, DM);
		tes.addVertex(DP, DP, DM);
		tes.addVertex(DP, DM, DM);

		tes.addVertex(DM, DM, DP);
		tes.addVertex(DP, DM, DP);
		tes.addVertex(DP, DP, DP);
		tes.addVertex(DM, DP, DP);

		tes.addVertex(DM, DM, DM);
		tes.addVertex(DM, DM, DP);
		tes.addVertex(DM, DP, DP);
		tes.addVertex(DM, DP, DM);

		tes.addVertex(DP, DM, DM);
		tes.addVertex(DP, DP, DM);
		tes.addVertex(DP, DP, DP);
		tes.addVertex(DP, DM, DP);

		tes.addVertex(DM, DM, DM);
		tes.addVertex(DP, DM, DM);
		tes.addVertex(DP, DM, DP);
		tes.addVertex(DM, DM, DP);

		tes.addVertex(DM, DP, DM);
		tes.addVertex(DM, DP, DP);
		tes.addVertex(DP, DP, DP);
		tes.addVertex(DP, DP, DM);

		GL11.glStencilMask(stencilMask);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

		GL11.glCullFace(GL11.GL_FRONT);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		RenderUtils.disableLightmap();
		tes.draw();
		RenderUtils.enableLightmap();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glCullFace(GL11.GL_BACK);

		GL11.glStencilMask(0);
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}
}
