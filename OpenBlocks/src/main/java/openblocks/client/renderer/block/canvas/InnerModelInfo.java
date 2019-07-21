package openblocks.client.renderer.block.canvas;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import openmods.geometry.AabbBuilder;

class InnerModelInfo {

	public final Map<BlockRenderLayer, ModelQuads> layers;

	public final AxisAlignedBB bounds;

	public final int maxTint;

	private InnerModelInfo(Map<BlockRenderLayer, ModelQuads> layers, AxisAlignedBB aabb, int maxTint) {
		this.layers = layers;
		this.bounds = aabb;
		this.maxTint = maxTint;
	}

	public static InnerModelInfo create(BlockState blockState, final IBakedModel baseModel, final Predicate<BlockRenderLayer> shouldCheckLayer) {
		int maxTint = -1;

		final AabbBuilder boundsBuilder = AabbBuilder.create();

		final ImmutableMap.Builder<BlockRenderLayer, ModelQuads> layers = ImmutableMap.builder();
		final BlockRenderLayer prevRenderLayer = MinecraftForgeClient.getRenderLayer();
		try {
			for (BlockRenderLayer layer : BlockRenderLayer.values()) {
				if (shouldCheckLayer.apply(layer)) {
					ForgeHooksClient.setRenderLayer(layer);

					final ModelQuads.Builder builder = ModelQuads.builder();

					for (Direction side : Direction.VALUES) {
						final List<BakedQuad> quads = baseModel.getQuads(blockState, side, 0);
						builder.addSidedQuads(side, quads);
						maxTint = processQuads(maxTint, boundsBuilder, quads);
					}

					final List<BakedQuad> generalQuads = baseModel.getQuads(blockState, null, 0);
					maxTint = processQuads(maxTint, boundsBuilder, generalQuads);
					builder.addGeneralQuads(generalQuads);

					layers.put(layer, builder.build());
				} else {
					layers.put(layer, ModelQuads.EMPTY);
				}
			}

		} finally {
			ForgeHooksClient.setRenderLayer(prevRenderLayer);
		}

		return new InnerModelInfo(layers.build(), boundsBuilder.build(), maxTint);
	}

	private static int processQuads(int maxTint, AabbBuilder boundsBuilder, List<BakedQuad> quads) {
		for (BakedQuad quad : quads) {
			maxTint = Math.max(maxTint, quad.getTintIndex());

			final int[] vertexData = quad.getVertexData();

			final int vertexSize = quad.getFormat().getIntegerSize();
			for (int i = 0; i < 4; i++) {
				final float x = Float.intBitsToFloat(vertexData[i * vertexSize + 0]);
				final float y = Float.intBitsToFloat(vertexData[i * vertexSize + 1]);
				final float z = Float.intBitsToFloat(vertexData[i * vertexSize + 2]);

				boundsBuilder.addPoint(x, y, z);
			}
		}

		return maxTint;
	}
}