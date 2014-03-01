package openblocks.common;

import java.util.Map;

import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.utils.EnchantmentUtils;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class LastStandEnchantmentsHandler {

	@ForgeSubscribe
	public void onDamage(LivingAttackEvent e) {
		if (e.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) e.entityLiving;
			int enchantmentLevels = countLastStandEnchantmentLevels(player);

			float healthAvailable = player.getHealth();
			healthAvailable -= e.ammount;
			
			if (healthAvailable < 0.5f) {
				
				int xpAvailable = EnchantmentUtils.getPlayerXP(player);
				
				float xpRequired = 0.5f - healthAvailable;

				xpRequired *= 50;
				xpRequired /= enchantmentLevels;

				xpRequired = Math.max(1, xpRequired);
				
				System.out.println(xpRequired);
				
				if (xpAvailable >= xpRequired) {
					player.setHealth(0.6f);
					EnchantmentUtils.drainPlayerXP(player, (int)xpRequired);
					e.setCanceled(true);
					player.attackEntityFrom(e.source, 0.1f);
				}
			}
			
		}
	}
	
	public static int countLastStandEnchantmentLevels(EntityPlayer player) {
		if (player != null) {	
			int count = 0;
			for (ItemStack stack : player.inventory.armorInventory) {
				if (stack != null) {
					Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					Integer ench = enchantments.get(OpenBlocks.lastStandEnch.effectId);
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
