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

	public static void registerAttackFlimFlam(IFlimFlamEffect flimFlam) {
		flimFlams.add(flimFlam);
		flimFlamsByName.put(flimFlam.name(), flimFlam);
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
