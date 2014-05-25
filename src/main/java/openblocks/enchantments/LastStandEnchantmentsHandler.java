package openblocks.enchantments;

import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import openblocks.OpenBlocks.Enchantments;
import openmods.utils.EnchantmentUtils;

public class LastStandEnchantmentsHandler {

	@ForgeSubscribe
	public void onHurt(LivingHurtEvent e) {

		if (e.entityLiving instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer)e.entityLiving;

			int enchantmentLevels = countLastStandEnchantmentLevels(player);

			if (enchantmentLevels > 0) {

				float healthAvailable = player.getHealth();
				healthAvailable -= e.ammount;

				if (healthAvailable < 1f) {

					int xpAvailable = EnchantmentUtils.getPlayerXP(player);

					float xpRequired = 1f - healthAvailable;

					xpRequired *= 50;
					xpRequired /= enchantmentLevels;
					xpRequired = Math.max(1, xpRequired);

					if (xpAvailable >= xpRequired) {
						player.setHealth(1f);
						EnchantmentUtils.addPlayerXP(player, -(int)xpRequired);
						e.ammount = 0;
					}
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
