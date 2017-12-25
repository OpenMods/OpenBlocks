package openblocks.common;

import com.google.common.base.Function;
import net.minecraftforge.fluids.FluidStack;
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

	public static final Function<FluidStack, FluidStack> FLUID_TO_LEVELS = input -> {
		if (input == null) return null;
		// display levels instead of actual xp fluid level
		final FluidStack result = input.copy();
		result.amount = EnchantmentUtils.getLevelForExperience(liquidToXpRatio(input.amount));
		return result;
	};

}
