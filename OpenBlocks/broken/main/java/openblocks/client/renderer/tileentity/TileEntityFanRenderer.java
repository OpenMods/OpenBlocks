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
import openblocks.common.tileentity.TileEntityFan;
import openmods.model.eval.EvalModelState;

public class TileEntityFanRenderer extends FastTESR<TileEntityFan> {

	protected static BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityFast(TileEntityFan te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, BufferBuilder renderer) {
		if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

		final BlockPos pos = te.getPos();
		final IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
		BlockState state = world.getBlockState(pos);

		if (state.getPropertyKeys().contains(Properties.StaticProperty)) {
			state = state.withProperty(Properties.StaticProperty, false);
		}

		if (state instanceof IExtendedBlockState) {
			state = state.getBlock().getExtendedState(state, world, pos);

			IExtendedBlockState exState = ((IExtendedBlockState)state).withProperty(EvalModelState.PROPERTY, te.getTesrRenderState(partialTicks));
			final IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(exState.getClean());
			renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
			blockRenderer.getBlockModelRenderer().renderModel(world, model, exState, pos, renderer, false);
			renderer.setTranslation(0, 0, 0);
		}
	}

}
