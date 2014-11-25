package openblocks.api;

import java.util.*;

import net.minecraftforge.common.MinecraftForge;
import openblocks.Config;
import openmods.Log;
import openmods.config.properties.ConfigurationChange;

import com.google.common.collect.*;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class FlimFlamRegistry {

	private static final List<IFlimFlamEffect> FLIM_FLAMS = Lists.newArrayList();

	private static final List<IFlimFlamEffect> UNMODIFIABLE_VIEW = Collections.unmodifiableList(FLIM_FLAMS);

	private static final Map<String, IFlimFlamEffect> FLIM_FLAMS_BY_NAME = Maps.newHashMap();

	public static final Blacklist BLACKLIST = new Blacklist();

	public static class Blacklist {
		private Set<String> blacklist;

		private void loadBlacklist() {
			blacklist = Sets.newHashSet();
			Set<String> validNames = Sets.newHashSet(FlimFlamRegistry.getAllFlimFlamsNames());
			for (String s : Config.flimFlamBlacklist) {
				if (validNames.contains(s)) {
					blacklist.add(s);
					Log.info("Blacklisting flim-flam %s", s);
				} else Log.warn("Trying to blacklist unknown flimflam name '%s'", s);
			}
		}

		public boolean isBlacklisted(IFlimFlamEffect effect) {
			if (blacklist == null) loadBlacklist();
			return (Config.safeFlimFlams && !effect.isSafe()) || blacklist.contains(effect.name());
		}

		@SubscribeEvent
		public void onReconfig(ConfigurationChange.Post evt) {
			if (evt.check("tomfoolery", "flimFlamBlacklist")) blacklist = null;
		}

		public void init() {
			loadBlacklist();
			MinecraftForge.EVENT_BUS.register(this);
		}
	}

	public static class FlimFlamMeta implements IFlimFlamEffect {
		private boolean isSilent;
		private boolean isSafe;
		private final String name;
		private final int cost;
		private final int weight;
		private int lowerLuck;
		private int upperLuck;
		private final IFlimFlamAction effect;

		public FlimFlamMeta(String name, int cost, int weight, IFlimFlamAction effect) {
			this.name = name;
			this.cost = cost;
			this.weight = weight;
			this.effect = effect;
			if (cost < 0) setRange(Integer.MIN_VALUE, cost);
			else setRange(cost, Integer.MAX_VALUE);
		}

		public FlimFlamMeta markSafe() {
			isSafe = true;
			return this;
		}

		public FlimFlamMeta markSilent() {
			isSilent = true;
			return this;
		}

		public FlimFlamMeta setRange(int a, int b) {
			if (a < b) {
				lowerLuck = a;
				upperLuck = b;
			} else {
				lowerLuck = b;
				upperLuck = a;
			}
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

		@Override
		public boolean canApply(int luck) {
			return lowerLuck <= luck && luck <= upperLuck;
		}
	}

	public static FlimFlamMeta registerFlimFlam(String name, int cost, int weight, IFlimFlamAction effect) {
		final FlimFlamMeta meta = new FlimFlamMeta(name, cost, weight, effect);
		registerFlimFlam(name, meta);
		return meta;
	}

	protected static IFlimFlamEffect registerFlimFlam(String name, IFlimFlamEffect meta) {
		FLIM_FLAMS.add(meta);
		FLIM_FLAMS_BY_NAME.put(name, meta);
		return meta;
	}

	public static List<IFlimFlamEffect> getFlimFlams() {
		return UNMODIFIABLE_VIEW;
	}

	public static IFlimFlamEffect getFlimFlamByName(String name) {
		return FLIM_FLAMS_BY_NAME.get(name);
	}

	public static List<String> getAllFlimFlamsNames() {
		return ImmutableList.copyOf(FLIM_FLAMS_BY_NAME.keySet());
	}

}
