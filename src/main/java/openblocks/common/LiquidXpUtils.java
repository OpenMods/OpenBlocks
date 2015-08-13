package openblocks.common;

import openblocks.Config;
import openmods.utils.EnchantmentUtils;

public class LiquidXpUtils {

	public static final int XP_PER_BOTTLE = 8;

	public static int liquidToXpRatio(int liquid) {
		return liquid / Config.xpToLiquidRatio;
	}

	public static int xpToLiquidRatio(int xp) {
		return xp * Config.xpToLiquidRatio;
	}

	public static int getLiquidForLevel(int level) {
		final int xp = EnchantmentUtils.getExperienceForLevel(level);
		return xpToLiquidRatio(xp);
	}

}
