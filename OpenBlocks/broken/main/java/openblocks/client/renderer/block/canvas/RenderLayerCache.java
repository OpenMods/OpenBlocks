package openblocks.client.renderer.block.canvas;

import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;
import org.apache.commons.lang3.tuple.Pair;

public class RenderLayerCache {

	public static class LayerRenderInfo {
		public final List<BlockRenderLayer> layers;
		public final boolean renderCovers;

		public LayerRenderInfo(List<BlockRenderLayer> layers, boolean renderCovers) {
			this.layers = layers;
			this.renderCovers = renderCovers;
		}
	}

	private static final LayerRenderInfo EMPTY = new LayerRenderInfo(ImmutableList.of(), false);

	private final Map<BlockRenderLayer, LayerRenderInfo> baseRenderLayers;

	private final LoadingCache<Pair<BlockState, BlockRenderLayer>, LayerRenderInfo> renderLayers = CacheBuilder.newBuilder().build(new CacheLoader<Pair<BlockState, BlockRenderLayer>, LayerRenderInfo>() {
		@Override
		public LayerRenderInfo load(Pair<BlockState, BlockRenderLayer> key) {
			final BlockState state = key.getLeft();
			final Block block = state.getBlock();

			return getLayerRenderInfo(key.getRight(), input -> block.canRenderInLayer(state, input));
		}
	});

	public RenderLayerCache(Predicate<BlockRenderLayer> baseModelLayers) {
		ImmutableMap.Builder<BlockRenderLayer, LayerRenderInfo> baseBuilder = ImmutableMap.builder();

		for (BlockRenderLayer layer : BlockRenderLayer.values())
			baseBuilder.put(layer, getLayerRenderInfo(layer, baseModelLayers));

		this.baseRenderLayers = baseBuilder.build();
	}

	private static LayerRenderInfo getLayerRenderInfo(BlockRenderLayer renderLayer, Predicate<BlockRenderLayer> canRender) {
		if (canRender.apply(BlockRenderLayer.TRANSLUCENT)) {
			if (renderLayer == BlockRenderLayer.TRANSLUCENT)
				return new LayerRenderInfo(ImmutableList.of(BlockRenderLayer.TRANSLUCENT), true);
			else if (renderLayer == BlockRenderLayer.CUTOUT)
				return new LayerRenderInfo(checkSolidLayers(canRender), false);
		} else {
			if (renderLayer == BlockRenderLayer.CUTOUT) return new LayerRenderInfo(checkSolidLayers(canRender), true);
		}

		return EMPTY;
	}

	private static List<BlockRenderLayer> checkSolidLayers(Predicate<BlockRenderLayer> canRender) {
		final List<BlockRenderLayer> builder = Lists.newArrayListWithExpectedSize(3);
		checkRenderLayer(BlockRenderLayer.SOLID, canRender, builder);
		checkRenderLayer(BlockRenderLayer.CUTOUT_MIPPED, canRender, builder);
		checkRenderLayer(BlockRenderLayer.CUTOUT, canRender, builder);
		return ImmutableList.copyOf(builder);
	}

	private static void checkRenderLayer(BlockRenderLayer layer, Predicate<BlockRenderLayer> canRender, List<BlockRenderLayer> output) {
		if (canRender.apply(layer)) output.add(layer);
	}

	public LayerRenderInfo get(Optional<BlockState> state, BlockRenderLayer renderLayer) {
		return state.isPresent()
				? renderLayers.getUnchecked(Pair.of(state.get(), renderLayer))
				: baseRenderLayers.get(renderLayer);
	}
}
