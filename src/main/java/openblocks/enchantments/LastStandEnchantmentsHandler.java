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
import openmods.calc.Calculator.ExprType;
import openmods.calc.Constant;
import openmods.calc.IExecutable;
import openmods.calc.types.fp.DoubleCalculator;
import openmods.config.properties.ConfigurationChange;
import openmods.entity.PlayerDamageEvent;
import openmods.utils.EnchantmentUtils;

public class LastStandEnchantmentsHandler {

	private static final String VAR_ENCH_LEVEL = "ench";
	private static final String VAR_PLAYER_XP = "xp";
	private static final String VAR_PLAYER_HP = "hp";
	private static final String VAR_DAMAGE = "dmg";

	private final Calculator<Double> reductionCalculator = new DoubleCalculator();

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
							formula = reductionCalculator.compile(ExprType.INFIX, Config.lastStandEnchantmentFormula);
						} catch (Exception ex) {
							useBuiltIn = true;
							Log.warn(ex, "Failed to compile formula %s", Config.lastStandEnchantmentFormula);
						}
					}

					reductionCalculator.setGlobalSymbol(VAR_ENCH_LEVEL, Constant.create(Double.valueOf(enchantmentLevels)));
					reductionCalculator.setGlobalSymbol(VAR_PLAYER_XP, Constant.create(Double.valueOf(xpAvailable)));
					reductionCalculator.setGlobalSymbol(VAR_PLAYER_HP, Constant.create(Double.valueOf(playerHealth)));
					reductionCalculator.setGlobalSymbol(VAR_DAMAGE, Constant.create(Double.valueOf(e.amount)));

					try {
						xpRequired = reductionCalculator.executeAndPop(formula).floatValue();
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
