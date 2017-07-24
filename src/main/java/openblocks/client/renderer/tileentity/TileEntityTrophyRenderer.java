package openblocks.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.LightUtil;
import openblocks.OpenBlocks.Blocks;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.tileentity.TileEntityTrophy;
import openmods.utils.BlockUtils;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class TileEntityTrophyRenderer extends TileEntitySpecialRenderer<TileEntityTrophy> {

	private static final int INVENTORY_ROTATION = 0;

	private final Trophy trophyType;

	public TileEntityTrophyRenderer(Trophy trophyType) {
		this.trophyType = trophyType;
	}

	public TileEntityTrophyRenderer() {
		this(null);
	}

	@Override
	public void renderTileEntityAt(TileEntityTrophy trophy, double x, double y, double z, float partialTick, int destroyProgress) {
		Trophy type = trophy != null? trophy.getTrophy() : trophyType;
		if (type != null) {
			float angle = trophy != null? BlockUtils.getRotationFromOrientation(trophy.getOrientation()) : INVENTORY_ROTATION;
			renderTrophy(type, x + 0.5, y, z + 0.5, angle);
		}

		if (trophy == null) renderStaticPart(x + 0.5, y + 0.5, z + 0.5);
	}

	private void renderStaticPart(double x, double y, double z) {
		final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		IBlockState state = Blocks.trophy.getDefaultState();

		IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(state);

		Tessellator tessellator = Tessellator.getInstance();
		final VertexBuffer wr = tessellator.getBuffer();
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		wr.setTranslation(x, y, z);

		for (EnumFacing face : EnumFacing.values())
			renderQuads(wr, model.getQuads(state, face, 0));

		renderQuads(wr, model.getQuads(state, null, 0));
		tessellator.draw();

		wr.setTranslation(0, 0, 0);
	}

	private static void renderQuads(VertexBuffer wr, List<BakedQuad> quads) {
		for (BakedQuad quad : quads)
			LightUtil.renderQuadColor(wr, quad, 0xFFFFFFFF);
	}

	private static void renderTrophy(Trophy type, double x, double y, double z, float angle) {
		Entity entity = type.getEntity();
		if (entity != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y + type.getVerticalOffset() + 0.2, z);
			GL11.glRotatef(angle, 0, 1, 0);

			final double ratio = type.getScale();
			GL11.glScaled(ratio, ratio, ratio);
			World renderWorld = RenderUtils.getRenderWorld();
			if (renderWorld != null) {
				Render<Entity> renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
				// yeah we don't care about fonts, but we do care that the
				// renderManager is available
				if (renderer != null && renderer.getFontRendererFromRenderManager() != null) {
					GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
					final boolean lightmapEnabled = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);
					GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

					synchronized (entity) {
						entity.world = renderWorld;
						renderer.doRender(entity, 0, 0, 0, 0, 0);
						entity.world = null;
					}

					GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
					if (lightmapEnabled) GlStateManager.enableTexture2D();
					else GlStateManager.disableTexture2D();
					GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

				}
			}
			GL11.glPopMatrix();

		}
	}

}
