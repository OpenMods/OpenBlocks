package openblocks.client.renderer.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.model.eval.EvalModelState;

public class TileEntitySprinklerRenderer extends FastTESR<TileEntitySprinkler> {

	protected static BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityFast(TileEntitySprinkler te, double x, double y, double z, float partialTick, int breakStage, float alpha, BufferBuilder vb) {
		if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

		final BlockPos pos = te.getPos();
		final IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
		BlockState state = world.getBlockState(pos);

		if (state.getPropertyKeys().contains(Properties.StaticProperty))
			state = state.withProperty(Properties.StaticProperty, false);

		if (state instanceof IExtendedBlockState) {
			IExtendedBlockState exState = (IExtendedBlockState)state;
			if (exState.getUnlistedNames().contains(EvalModelState.PROPERTY)) {
				exState = exState.withProperty(EvalModelState.PROPERTY, te.getRenderState());

				vb.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

				final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(exState.getClean());
				blockRenderer.getBlockModelRenderer().renderModel(world, model, exState, pos, vb, false);

				vb.setTranslation(0, 0, 0);
			}
		}
	}

}
