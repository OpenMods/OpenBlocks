package openblocks.enchantments.flimflams;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.api.FlimFlamDescriptionSimple;
import openblocks.api.IFlimFlamAction;
import openblocks.api.IFlimFlamDescription;
import openblocks.api.IFlimFlamRegistry;
import openmods.Log;
import openmods.access.ApiSingleton;
import openmods.config.properties.ConfigurationChange;

@ApiSingleton
public class FlimFlamRegistry implements IFlimFlamRegistry {

	public static final FlimFlamRegistry instance = new FlimFlamRegistry();

	private static final List<IFlimFlamDescription> FLIM_FLAMS = Lists.newArrayList();

	private static final List<IFlimFlamDescription> UNMODIFIABLE_VIEW = Collections.unmodifiableList(FLIM_FLAMS);

	private static final Map<String, IFlimFlamDescription> FLIM_FLAMS_BY_NAME = Maps.newHashMap();

	public static final FlimFlamChecker BLACKLIST = new FlimFlamChecker();

	public static class FlimFlamChecker {
		private Set<String> flimFlamList;

		private void loadList() {
			flimFlamList = Sets.newHashSet();
			Set<String> validNames = Sets.newHashSet(FlimFlamRegistry.instance.getAllFlimFlamsNames());
			for (String s : Config.flimFlamList) {
				if (validNames.contains(s)) {
					flimFlamList.add(s);
				} else Log.warn("Trying to blacklist unknown flimflam name '%s'", s);
			}

			Log.debug("Blacklisting/Whitelisting flim-flams %s", flimFlamList);
		}

		public boolean isBlacklisted(IFlimFlamDescription effect) {
			if (Config.safeFlimFlams && !effect.isSafe()) return true;

			if (flimFlamList == null) loadList();
			final boolean onList = flimFlamList.contains(effect.name());
			return Config.flimFlamWhitelist ^ onList;
		}

		@SubscribeEvent
		public void onReconfig(ConfigurationChange.Post evt) {
			if (evt.check("tomfoolery", "flimFlamBlacklist")) flimFlamList = null;
		}

		public void init() {
			loadList();
			MinecraftForge.EVENT_BUS.register(this);
		}
	}

	@Override
	public FlimFlamDescriptionSimple registerFlimFlam(String name, int cost, int weight, IFlimFlamAction effect) {
		final FlimFlamDescriptionSimple meta = new FlimFlamDescriptionSimple(name, cost, weight, effect);
		registerFlimFlam(name, meta);
		return meta;
	}

	@Override
	public void registerFlimFlam(String name, IFlimFlamDescription meta) {
		FLIM_FLAMS.add(meta);
		FLIM_FLAMS_BY_NAME.put(name, meta);
	}

	@Override
	public List<IFlimFlamDescription> getFlimFlams() {
		return UNMODIFIABLE_VIEW;
	}

	@Override
	public IFlimFlamDescription getFlimFlamByName(String name) {
		return FLIM_FLAMS_BY_NAME.get(name);
	}

	@Override
	public List<String> getAllFlimFlamsNames() {
		return ImmutableList.copyOf(FLIM_FLAMS_BY_NAME.keySet());
	}

}
