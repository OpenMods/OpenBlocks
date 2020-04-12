package openblocks.client.renderer.block.canvas;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.util.Direction;

class ModelQuads {
	private final List<BakedQuad> generalQuads;

	private final Map<Direction, List<BakedQuad>> sidedQuads;

	private ModelQuads(List<BakedQuad> generalQuads, Map<Direction, List<BakedQuad>> sidedQuads) {
		this.generalQuads = generalQuads;
		this.sidedQuads = sidedQuads;
	}

	public List<BakedQuad> get(@Nullable Direction side) {
		return side != null? sidedQuads.get(side) : generalQuads;
	}

	public static final ModelQuads EMPTY;

	static {
		final ImmutableMap.Builder<Direction, List<BakedQuad>> emptySides = ImmutableMap.builder();
		for (Direction side : Direction.VALUES)
			emptySides.put(side, ImmutableList.of());

		EMPTY = new ModelQuads(ImmutableList.of(), emptySides.build()) {
			@Override
			public List<BakedQuad> get(@Nullable Direction side) {
				return ImmutableList.of();
			}
		};
	}

	public static class Builder {
		private final List<BakedQuad> generalQuads = Lists.newArrayList();

		private final Map<Direction, List<BakedQuad>> sidedQuads = Maps.newHashMap();

		public Builder() {
			for (Direction side : Direction.VALUES)
				sidedQuads.put(side, Lists.newArrayList());
		}

		public Builder addSidedQuads(Direction side, Collection<BakedQuad> quads) {
			sidedQuads.get(side).addAll(quads);
			return this;
		}

		public Builder addGeneralQuads(Collection<BakedQuad> quads) {
			generalQuads.addAll(quads);
			return this;
		}

		public Builder merge(ModelQuads other) {
			generalQuads.addAll(other.generalQuads);

			for (Direction side : Direction.VALUES)
				sidedQuads.get(side).addAll(other.get(side));

			return this;
		}

		public ModelQuads build() {
			final ImmutableMap.Builder<Direction, List<BakedQuad>> sides = ImmutableMap.builder();
			for (Direction side : Direction.VALUES)
				sides.put(side, ImmutableList.copyOf(sidedQuads.get(side)));

			return new ModelQuads(ImmutableList.copyOf(generalQuads), sides.build());
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}