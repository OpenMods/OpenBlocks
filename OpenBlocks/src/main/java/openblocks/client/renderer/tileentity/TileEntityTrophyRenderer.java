package openblocks.client.renderer.tileentity;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
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
	public void render(TileEntityTrophy trophy, double x, double y, double z, float partialTick, int destroyProgress, float alpha) {
		Trophy type = trophy != null? trophy.getTrophy() : trophyType;
		if (type != null) {
			float angle = trophy != null? BlockUtils.getRotationFromOrientation(trophy.getOrientation()) : INVENTORY_ROTATION;
			renderTrophy(type, x + 0.5, y, z + 0.5, angle);
		}

		if (trophy == null) renderStaticPart(x, y, z);
	}

	private void renderStaticPart(double x, double y, double z) {
		final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		BlockState state = Blocks.trophy.getDefaultState();

		IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(state);

		Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder wr = tessellator.getBuffer();
		bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);

		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		wr.setTranslation(x, y, z);

		for (Direction face : Direction.values())
			renderQuads(wr, model.getQuads(state, face, 0));

		renderQuads(wr, model.getQuads(state, null, 0));
		tessellator.draw();

		wr.setTranslation(0, 0, 0);
	}

	private static void renderQuads(BufferBuilder wr, List<BakedQuad> quads) {
		for (BakedQuad quad : quads)
			LightUtil.renderQuadColor(wr, quad, 0xFFFFFFFF);
	}

	private void renderTrophy(Trophy type, double x, double y, double z, float angle) {
		Entity entity = type.getEntity();
		if (entity != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y + type.getVerticalOffset() + 0.2, z);
			GL11.glRotatef(angle, 0, 1, 0);

			final double ratio = type.getScale();
			GL11.glScaled(ratio, ratio, ratio);
			World renderWorld = RenderUtils.getRenderWorld();
			if (renderWorld != null) {
				EntityRenderer<Entity> renderer = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entity);
				// yeah we don't care about fonts, but we do care that the
				// renderManager is available
				if (renderer != null && renderer.getFontRendererFromRenderManager() != null) {

					final boolean blurLast;
					final boolean mipmapLast;
					final Texture blocksTexture = getBlockTexture();
					if (blocksTexture != null) {
						blurLast = blocksTexture.blurLast;
						mipmapLast = blocksTexture.mipmapLast;
					} else {
						blurLast = false;
						mipmapLast = false;
					}

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

					if (blocksTexture != null) {
						blocksTexture.mipmapLast = mipmapLast;
						blocksTexture.blurLast = blurLast;
					}
				}
			}
			GL11.glPopMatrix();

		}
	}

	private Texture getBlockTexture() {
		final TextureManager texturemanager = rendererDispatcher.renderEngine;
		if (texturemanager != null) {
			final ITextureObject texture = texturemanager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			if (texture instanceof Texture)
				return (Texture)texture;
		}

		return null;
	}

}
