package openblocks.enchantments;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Map;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openblocks.Config;
import openblocks.OpenBlocks.Enchantments;
import openmods.Log;
import openmods.calc.Calculator;
import openmods.calc.ExprType;
import openmods.calc.IExecutable;
import openmods.calc.types.fp.DoubleCalculatorFactory;
import openmods.config.properties.ConfigurationChange;
import openmods.entity.PlayerDamageEvent;
import openmods.utils.EnchantmentUtils;

public class LastStandEnchantmentsHandler {

	private static final String VAR_ENCH_LEVEL = "ench";
	private static final String VAR_PLAYER_XP = "xp";
	private static final String VAR_PLAYER_HP = "hp";
	private static final String VAR_DAMAGE = "dmg";

	private final Calculator<Double, ExprType> reductionCalculator = DoubleCalculatorFactory.createSimple();

	private boolean useBuiltIn;
	private IExecutable<Double> formula;

	@SubscribeEvent
	public void onConfigChange(ConfigurationChange.Post evt) {
		if (evt.check("features", "lastStandFormula")) {
			useBuiltIn = false;
			formula = null;
		}
	}

	private static float builtInFormula(float healthAvailable, float enchantmentLevels) {
		float xpRequired = 1f - healthAvailable;

		xpRequired *= 50;
		xpRequired /= enchantmentLevels;
		xpRequired = Math.max(1, xpRequired);
		return xpRequired;
	}

	@SubscribeEvent
	public void onHurt(PlayerDamageEvent e) {
		final int enchantmentLevels = countLastStandEnchantmentLevels(e.player);

		if (enchantmentLevels > 0) {
			final float playerHealth = e.player.getHealth();
			final float healthAvailable = playerHealth - e.amount;

			if (healthAvailable < 1f) {
				final int xpAvailable = EnchantmentUtils.getPlayerXP(e.player);

				float xpRequired = builtInFormula(healthAvailable, enchantmentLevels);

				if (!useBuiltIn) {
					if (formula == null) {
						try {
							formula = reductionCalculator.compilers.compile(ExprType.INFIX, Config.lastStandEnchantmentFormula);
						} catch (Exception ex) {
							useBuiltIn = true;
							Log.warn(ex, "Failed to compile formula %s", Config.lastStandEnchantmentFormula);
						}
					}

					reductionCalculator.environment.setGlobalSymbol(VAR_ENCH_LEVEL, Double.valueOf(enchantmentLevels));
					reductionCalculator.environment.setGlobalSymbol(VAR_PLAYER_XP, Double.valueOf(xpAvailable));
					reductionCalculator.environment.setGlobalSymbol(VAR_PLAYER_HP, Double.valueOf(playerHealth));
					reductionCalculator.environment.setGlobalSymbol(VAR_DAMAGE, Double.valueOf(e.amount));

					try {
						xpRequired = reductionCalculator.environment.executeAndPop(formula).floatValue();
					} catch (Exception ex) {
						useBuiltIn = true;
						Log.warn(ex, "Failed to execute formula %s", Config.lastStandEnchantmentFormula);
					}
				}

				if (xpAvailable >= xpRequired) {
					e.player.setHealth(1f);
					EnchantmentUtils.addPlayerXP(e.player, -(int)xpRequired);
					e.amount = 0;
					e.setCanceled(true);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static int countLastStandEnchantmentLevels(EntityPlayer player) {
		if (player != null) {
			int count = 0;
			for (ItemStack stack : player.inventory.armorInventory) {
				if (stack != null) {
					Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					Integer ench = enchantments.get(Enchantments.lastStand.effectId);
					if (ench != null) {
						count += ench;
					}
				}
			}
			return count;
		}
		return 0;
	}

}
