package openblocks.enchantments;

import com.google.common.base.Supplier;
import info.openmods.calc.Environment;
import info.openmods.calc.ExprType;
import info.openmods.calc.SingleExprEvaluator;
import info.openmods.calc.SingleExprEvaluator.EnvironmentConfigurator;
import info.openmods.calc.types.fp.DoubleCalculatorFactory;
import jline.internal.Log;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks.Enchantments;
import openmods.config.properties.ConfigurationChange;
import openmods.entity.PlayerDamageEvent;
import openmods.utils.EnchantmentUtils;

public class LastStandEnchantmentsHandler {

	private static final String VAR_ENCH_LEVEL = "ench";
	private static final String VAR_PLAYER_XP = "xp";
	private static final String VAR_PLAYER_HP = "hp";
	private static final String VAR_DAMAGE = "dmg";

	private final SingleExprEvaluator<Double, ExprType> reductionCalculator = SingleExprEvaluator.create(DoubleCalculatorFactory.createDefault());

	{
		updateReductionFormula();
	}

	@SubscribeEvent
	public void onConfigChange(ConfigurationChange.Post evt) {
		if (evt.check("features", "lastStandFormula"))
			updateReductionFormula();
	}

	private void updateReductionFormula() {
		reductionCalculator.setExpr(ExprType.INFIX, Config.lastStandEnchantmentFormula);

		if (!reductionCalculator.isExprValid())
			Log.warn("Invalid lastStandFormula: ", Config.lastStandEnchantmentEnabled);
	}

	@SubscribeEvent
	public void onHurt(final PlayerDamageEvent e) {
		final int enchantmentLevels = countLastStandEnchantmentLevels(e.player);

		if (enchantmentLevels > 0) {
			final float playerHealth = e.player.getHealth();
			final float healthAvailable = playerHealth - e.amount;

			if (healthAvailable < 1f) {
				final int xpAvailable = EnchantmentUtils.getPlayerXP(e.player);

				float xpRequired = reductionCalculator.evaluate(
						new EnvironmentConfigurator<Double>() {
							@Override
							public void accept(Environment<Double> env) {
								env.setGlobalSymbol(VAR_ENCH_LEVEL, Double.valueOf(enchantmentLevels));
								env.setGlobalSymbol(VAR_PLAYER_XP, Double.valueOf(xpAvailable));
								env.setGlobalSymbol(VAR_PLAYER_HP, Double.valueOf(playerHealth));
								env.setGlobalSymbol(VAR_DAMAGE, Double.valueOf(e.amount));
							}
						},
						new Supplier<Double>() {
							@Override
							public Double get() {
								float xpRequired = 1f - healthAvailable;
								xpRequired *= 50;
								xpRequired /= enchantmentLevels;
								xpRequired = Math.max(1, xpRequired);
								return Double.valueOf(xpRequired);
							}
						}).floatValue();

				if (xpAvailable >= xpRequired) {
					e.player.setHealth(1f);
					EnchantmentUtils.addPlayerXP(e.player, -(int)xpRequired);
					e.amount = 0;
					e.setCanceled(true);
				}
			}
		}
	}

	public static int countLastStandEnchantmentLevels(EntityPlayer player) {
		if (player != null) {
			int count = 0;
			for (ItemStack stack : player.inventory.armorInventory)
				count += EnchantmentHelper.getEnchantmentLevel(Enchantments.lastStand, stack);
			return count;
		}
		return 0;
	}

}
