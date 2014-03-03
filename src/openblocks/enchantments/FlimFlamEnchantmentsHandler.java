package openblocks.enchantments;

import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import openblocks.OpenBlocks.Enchantments;

public class FlimFlamEnchantmentsHandler {

	@ForgeSubscribe
	public void onDamage(LivingAttackEvent e) {
		if (e.entityLiving instanceof EntityPlayer) {

			EntityPlayer targetPlayer = (EntityPlayer)e.entityLiving;

			if (e.source == null) { return; }
			Entity damageSource = e.source.getEntity();

			if (!(damageSource instanceof EntityPlayer)) { return; }
			EntityPlayer sourcePlayer = (EntityPlayer)damageSource;

			// flim flam yerself?
			if (sourcePlayer == targetPlayer) {
				// return;
			}

			boolean sourceHasFlimFlam = hasFlimFlamTool(sourcePlayer);
			boolean targetHasFlimFlam = hasFlimFlamArmor(targetPlayer);

			if (sourceHasFlimFlam) {
				System.out.println("Source has flim flam");
			}

			if (targetHasFlimFlam) {
				System.out.println("Target has flim flam");
			}
		}
	}

	public static boolean hasFlimFlamTool(EntityPlayer player) {
		if (player != null) { return hasFlimFlam(player.getHeldItem()); }
		return false;
	}

	public static boolean hasFlimFlamArmor(EntityPlayer player) {
		if (player != null) {
			for (ItemStack stack : player.inventory.armorInventory) {
				if (hasFlimFlam(stack)) return true;
			}
		}
		return false;
	}

	public static boolean hasFlimFlam(ItemStack stack) {
		if (stack == null) { return false; }
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		return enchantments.containsKey(Enchantments.flimFlam.effectId);
	}
}
