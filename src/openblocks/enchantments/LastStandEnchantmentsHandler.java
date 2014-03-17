package openblocks.enchantments;

import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import openblocks.OpenBlocks.Enchantments;
import openblocks.utils.HackyUtils;
import openmods.utils.EnchantmentUtils;

public class LastStandEnchantmentsHandler {

	@ForgeSubscribe
	public void onDamage(LivingAttackEvent e) {
		
		if (e.entityLiving instanceof EntityPlayer) {
			
			float damageAmount = e.ammount;
			
			// lord forgive me
			if (HackyUtils.isCalledFromClass(EntityLivingBase.class)) {
				return;
			}
			
			EntityPlayer player = (EntityPlayer)e.entityLiving;

			int enchantmentLevels = countLastStandEnchantmentLevels(player);

			if (e.source.isDifficultyScaled()) {
				
				World world = e.entityLiving.worldObj;
				
				// scale the damage based on their difficulty setting.
                if (world.difficultySetting == 0) {
                	damageAmount = 0.0F;
                } else if (world.difficultySetting == 1) {
                	damageAmount = damageAmount / 2.0F + 1.0F;
                } else if (world.difficultySetting == 3) {
                	damageAmount = damageAmount * 3.0F / 2.0F;
                }
                
            }			
			
			if (enchantmentLevels > 0 && e.ammount > 1f) {
			
				float healthAvailable = player.getHealth();
				healthAvailable -= damageAmount;
	
				if (healthAvailable < 1f) {
	
					int xpAvailable = EnchantmentUtils.getPlayerXP(player);
	
					float xpRequired = 1f - healthAvailable;
	
					xpRequired *= 50;
					xpRequired /= enchantmentLevels;
					xpRequired = Math.max(1, xpRequired);
	
					if (xpAvailable >= xpRequired) {
						player.setHealth(1f);
						EnchantmentUtils.drainPlayerXP(player, (int)xpRequired);
						e.setCanceled(true);
						player.attackEntityFrom(e.source, 0f);
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
