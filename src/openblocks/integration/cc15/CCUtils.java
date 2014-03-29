package openblocks.integration.cc15;

import java.util.List;

import net.minecraft.item.ItemStack;
import openblocks.integration.CCCommonUtils;
import dan200.turtle.api.ITurtleUpgrade;

public class CCUtils {
	private static final int NUMBER_OF_TURTLE_TOOLS = 7;

	private static void addUpgradedTurtles(List<ItemStack> result, ITurtleUpgrade upgrade, boolean isAdvanced) {
		short upgradeId = (short)upgrade.getUpgradeID();
		CCCommonUtils.createTurtleItemStack(result, isAdvanced, upgradeId, null);
		for (int i = 1; i < NUMBER_OF_TURTLE_TOOLS; i++)
			CCCommonUtils.createTurtleItemStack(result, isAdvanced, upgradeId, (short)i);
	}

	public static void addUpgradedTurtles(List<ItemStack> result, ITurtleUpgrade upgrade) {
		addUpgradedTurtles(result, upgrade, false);
		addUpgradedTurtles(result, upgrade, true);
	}
}
