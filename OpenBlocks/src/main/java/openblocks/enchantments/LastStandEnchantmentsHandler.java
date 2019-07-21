package openblocks.enchantments;

import info.openmods.calc.ExprType;
import info.openmods.calc.SingleExprEvaluator;
import info.openmods.calc.types.fp.DoubleCalculatorFactory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks.Enchantments;
import openmods.Log;
import openmods.config.properties.ConfigurationChange;
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
	public void onHurt(final LivingHurtEvent e) {
		if (!(e.getEntityLiving() instanceof PlayerEntity)) return;
		PlayerEntity player = (PlayerEntity)e.getEntityLiving();

		final int enchantmentLevels = countLastStandEnchantmentLevels(player);

		if (enchantmentLevels > 0) {
			final float playerHealth = player.getHealth();
			final float healthAvailable = playerHealth - e.getAmount();

			if (healthAvailable < 1f) {
				final int xpAvailable = EnchantmentUtils.getPlayerXP(player);

				float xpRequired = reductionCalculator.evaluate(
						env -> {
							env.setGlobalSymbol(VAR_ENCH_LEVEL, Double.valueOf(enchantmentLevels));
							env.setGlobalSymbol(VAR_PLAYER_XP, Double.valueOf(xpAvailable));
							env.setGlobalSymbol(VAR_PLAYER_HP, Double.valueOf(playerHealth));
							env.setGlobalSymbol(VAR_DAMAGE, Double.valueOf(e.getAmount()));
						},
						() -> {
							float xp = 1f - healthAvailable;
							xp *= 50;
							xp /= enchantmentLevels;
							xp = Math.max(1, xp);
							return Double.valueOf(xp);
						}).floatValue();

				if (xpAvailable >= xpRequired) {
					player.setHealth(1f);
					EnchantmentUtils.addPlayerXP(player, -(int)xpRequired);
					e.setAmount(0);
					e.setCanceled(true);
				}
			}
		}
	}

	public static int countLastStandEnchantmentLevels(LivingEntity living) {
		if (living != null) {
			int count = 0;
			for (ItemStack stack : living.getArmorInventoryList())
				count += EnchantmentHelper.getEnchantmentLevel(Enchantments.lastStand, stack);
			return count;
		}
		return 0;
	}

}
