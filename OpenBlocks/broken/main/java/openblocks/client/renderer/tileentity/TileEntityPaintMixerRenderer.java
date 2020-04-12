package openblocks.client.renderer.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.Properties;
import openblocks.common.tileentity.TileEntityPaintMixer;
import org.apache.commons.lang3.tuple.Pair;

public class TileEntityPaintMixerRenderer extends FastTESR<TileEntityPaintMixer> {

	protected static BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityFast(TileEntityPaintMixer te, double x, double y, double z, float partialTick, int breakStage, float alpha, BufferBuilder renderer) {
		if (te.hasPaint()) {
			if (!te.hasCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null)) { return; }
			if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

			final BlockPos pos = te.getPos();
			final IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
			BlockState state = world.getBlockState(pos);

			if (state.getPropertyKeys().contains(Properties.StaticProperty)) {
				state = state.withProperty(Properties.StaticProperty, false);
			}

			if (state instanceof IExtendedBlockState) {
				state = state.getBlock().getExtendedState(state, world, pos); // difference between this and AnimationTESR

				IExtendedBlockState exState = (IExtendedBlockState)state;
				if (exState.getUnlistedNames().contains(Properties.AnimationProperty)) {
					float time = Animation.getWorldTime(getWorld(), partialTick);
					Pair<IModelState, Iterable<Event>> pair = te.getCapability(CapabilityAnimation.ANIMATION_CAPABILITY, null).apply(time);

					IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(exState.getClean());
					exState = exState.withProperty(Properties.AnimationProperty, pair.getLeft());

					renderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

					blockRenderer.getBlockModelRenderer().renderModel(world, model, exState, pos, renderer, false);
				}
			}
		}
	}

}
