package openblocks.api;

import java.util.List;

import com.google.common.collect.Lists;

public class FlimFlamRegistry {

	private static List<IAttackFlimFlam> attackFlimFlams = Lists.newArrayList();

	public static void registerAttackFlimFlam(IAttackFlimFlam flimFlam) {
		attackFlimFlams.add(flimFlam);
	}

	public static List<IAttackFlimFlam> getAttackFlimFlams() {
		return attackFlimFlams;
	}

}
