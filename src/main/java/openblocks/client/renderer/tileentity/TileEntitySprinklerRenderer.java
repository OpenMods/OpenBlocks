package openblocks.client.renderer.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.model.eval.EvalModelState;
import org.lwjgl.opengl.GL11;

public class TileEntitySprinklerRenderer extends FastTESR<TileEntitySprinkler> {

	protected static BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityFast(TileEntitySprinkler te, double x, double y, double z, float partialTick, int breakStage, VertexBuffer renderer) {
		if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

		final BlockPos pos = te.getPos();
		final IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
		IBlockState state = world.getBlockState(pos);

		if (state.getPropertyNames().contains(Properties.StaticProperty))
			state = state.withProperty(Properties.StaticProperty, false);

		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState exState = (IExtendedBlockState)state;
			if (exState.getUnlistedNames().contains(EvalModelState.PROPERTY)) {
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer vb = tessellator.getBuffer();
				bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				RenderHelper.disableStandardItemLighting();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GlStateManager.enableBlend();
				GlStateManager.disableCull();

				if (Minecraft.isAmbientOcclusionEnabled()) {
					GlStateManager.shadeModel(GL11.GL_SMOOTH);
				} else {
					GlStateManager.shadeModel(GL11.GL_FLAT);
				}

				vb.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

				exState = exState.withProperty(EvalModelState.PROPERTY, te.getRenderState());

				vb.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

				final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(exState.getClean());
				blockRenderer.getBlockModelRenderer().renderModel(world, model, exState, pos, vb, false);

				vb.setTranslation(0, 0, 0);

				tessellator.draw();

				RenderHelper.enableStandardItemLighting();
			}
		}
	}

}
