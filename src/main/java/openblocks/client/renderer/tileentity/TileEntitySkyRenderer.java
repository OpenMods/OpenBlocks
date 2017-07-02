package openblocks.client.renderer.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.client.renderer.SkyBlockRenderer;
import openblocks.common.block.BlockSky;
import openblocks.common.tileentity.TileEntitySky;
import org.lwjgl.opengl.GL11;

public class TileEntitySkyRenderer extends TileEntitySpecialRenderer<TileEntitySky> {

	private static BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityAt(TileEntitySky te, double x, double y, double z, float partialTickTime, int destroyStage) {
		BlockPos pos = te.getPos();
		IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
		IBlockState state = world.getBlockState(pos).getActualState(world, pos);

		if (!BlockSky.isActive(state)) return;

		SkyBlockRenderer.INSTANCE.incrementUsers();

		final int stencilMask = SkyBlockRenderer.INSTANCE.getStencilMask();
		final boolean stencilActive = stencilMask >= 0 && SkyBlockRenderer.INSTANCE.hasSkyTexture();
		if (stencilActive) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			GL11.glStencilMask(stencilMask);

			GL11.glClearStencil(0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

			GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

			GL11.glColorMask(false, false, false, false);
		}

		renderModel(x, y, z, pos, world, state);

		if (stencilActive) {
			GL11.glColorMask(true, true, true, true);

			GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			SkyBlockRenderer.INSTANCE.renderSkyTexture();
			GL11.glDisable(GL11.GL_STENCIL_TEST);
		}
	}

	private void renderModel(double x, double y, double z, BlockPos pos, IBlockAccess world, IBlockState state) {
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(state);

		final Tessellator tessellator = Tessellator.getInstance();
		final VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
		renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
		blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, renderer, false);
		tessellator.draw();
		renderer.setTranslation(0, 0, 0);
	}
}
