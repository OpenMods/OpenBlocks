package openblocks.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FlimFlamRegistry {

	private static final List<IFlimFlamEffect> flimFlams = Lists.newArrayList();

	private static final List<IFlimFlamEffect> unmodifiableView = Collections.unmodifiableList(flimFlams);

	private static final Map<String, IFlimFlamEffect> flimFlamsByName = Maps.newHashMap();

	public static class FlimFlamMeta implements IFlimFlamEffect {
		private boolean isSilent;
		private boolean isSafe;
		private final String name;
		private final int cost;
		private final int weight;
		private final IFlimFlamAction effect;
		
		public FlimFlamMeta(String name, int cost, int weight, IFlimFlamAction effect) {
			this.name = name;
			this.cost = cost;
			this.weight = weight;
			this.effect = effect;
		}
		
		public FlimFlamMeta markSafe() {
			isSafe = true;
			return this;
		}
		
		public FlimFlamMeta markSilent() {
			isSilent = true;
			return this;
		}

		@Override
		public String name() {
			return name;
		}

		@Override
		public int weight() {
			return weight;
		}

		@Override
		public int cost() {
			return cost;
		}

		@Override
		public boolean isSafe() {
			return isSafe;
		}

		@Override
		public boolean isSilent() {
			return isSilent;
		}

		@Override
		public IFlimFlamAction action() {
			return effect;
		}
	}
	
	public static FlimFlamMeta registerFlimFlam(String name, int cost, int weight, IFlimFlamAction effect) {
		final FlimFlamMeta meta = new FlimFlamMeta(name, cost, weight, effect);
		registerFlimFlam(name, meta);
		return meta;
	}

	protected static IFlimFlamEffect registerFlimFlam(String name, IFlimFlamEffect meta) {
		flimFlams.add(meta);
		flimFlamsByName.put(name, meta);
		return meta;
	}

	public static List<IFlimFlamEffect> getFlimFlams() {
		return unmodifiableView;
	}

	public static IFlimFlamEffect getFlimFlamByName(String name) {
		return flimFlamsByName.get(name);
	}

	public static List<String> getAllFlimFlamsNames() {
		return ImmutableList.copyOf(flimFlamsByName.keySet());
	}

}
