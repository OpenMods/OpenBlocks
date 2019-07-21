package openblocks.client.renderer.tileentity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.client.renderer.SkyBlockRenderer;
import openblocks.common.block.BlockSky;
import openblocks.common.tileentity.TileEntitySky;
import openmods.renderer.CachedRendererFactory;
import openmods.renderer.CachedRendererFactory.CachedRenderer;
import openmods.utils.FakeBlockAccess;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class TileEntitySkyRenderer extends TileEntitySpecialRenderer<TileEntitySky> {

	public TileEntitySkyRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	private CachedRenderer skyTextureRenderer = null;

	@Override
	public void render(TileEntitySky te, double x, double y, double z, float partialTickTime, int destroyStage, float alpha) {
		BlockPos pos = te.getPos();
		IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
		BlockState state = world.getBlockState(pos).getActualState(world, pos);

		final Block block = state.getBlock();
		if (!(block instanceof BlockSky) || !((BlockSky)block).isActive(state)) return;

		SkyBlockRenderer.INSTANCE.incrementUsers();

		final int stencilMask = SkyBlockRenderer.INSTANCE.getStencilMask();
		final boolean stencilActive = stencilMask >= 0 && SkyBlockRenderer.INSTANCE.hasSkyTexture();
		if (stencilActive) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			GL11.glStencilMask(stencilMask);
			GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

			GL11.glColorMask(false, false, false, false);
		}

		GlStateManager.enableTexture2D();
		renderModel(x, y, z, pos, world, state);

		if (stencilActive) {
			GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
			// clears our bit
			GL11.glStencilOp(GL11.GL_ZERO, GL11.GL_ZERO, GL11.GL_ZERO);

			SkyBlockRenderer.INSTANCE.bindSkyTexture();

			if (skyTextureRenderer == null) {
				skyTextureRenderer = createSkyTextureRenderer();
			}

			skyTextureRenderPre();
			skyTextureRenderer.render();
			skyTextureRenderPost();
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
	}

	private static CachedRenderer createSkyTextureRenderer() {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		bufferbuilder.pos(-1, -1, 0).tex(0, 0).endVertex();
		bufferbuilder.pos(-1, 1, 0).tex(0, 1).endVertex();
		bufferbuilder.pos(1, 1, 0).tex(1, 1).endVertex();
		bufferbuilder.pos(1, -1, 0).tex(1, 0).endVertex();

		return new CachedRendererFactory().createRenderer(tessellator);
	}

	private static void skyTextureRenderPre() {
		GlStateManager.disableFog();
		RenderUtils.disableLightmap();

		GlStateManager.colorMask(true, true, true, false);
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);

		GlStateManager.disableBlend();

		GlStateManager.disableLighting();
		GlStateManager.disableAlpha();

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.disableCull();

		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();

		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
	}

	private static void skyTextureRenderPost() {
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.enableLighting();
		GlStateManager.enableAlpha();

		GlStateManager.disableCull();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);

		RenderUtils.enableLightmap();
		GlStateManager.enableFog();
	}

	private final Map<BlockState, CachedRenderer> renderers = Maps.newHashMap();

	private static CachedRenderer createRenderer(BlockState state) {
		final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(state);

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		final IBlockAccess fakeBlockAccess = new FakeBlockAccess(state);
		blockRenderer.getBlockModelRenderer().renderModel(fakeBlockAccess, model, state, FakeBlockAccess.ORIGIN, renderer, false);

		return new CachedRendererFactory().createRenderer(tessellator);
	}

	private void renderModel(double x, double y, double z, BlockPos pos, IBlockAccess world, BlockState state) {
		bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		final CachedRenderer renderer = renderers.computeIfAbsent(state, TileEntitySkyRenderer::createRenderer);

		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		renderer.render();
		GL11.glPopMatrix();
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent evt) {
		for (CachedRenderer renderer : renderers.values())
			renderer.dispose();
		renderers.clear();

		if (skyTextureRenderer != null) {
			skyTextureRenderer.dispose();
			skyTextureRenderer = null;
		}
	}
}
