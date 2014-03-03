package openblocks.enchantments;

import java.util.*;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import openblocks.OpenBlocks.Enchantments;
import openblocks.api.FlimFlamRegistry;
import openblocks.api.IAttackFlimFlam;
import openblocks.api.IAttackFlimFlam.FlimFlammer;

public class FlimFlamEnchantmentsHandler {

	public static Random rnd = new Random();

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

			FlimFlammer whoBeFlimFlam = FlimFlammer.getFlimFlam(
					hasFlimFlamTool(sourcePlayer),
					hasFlimFlamArmor(targetPlayer)
					);

			if (whoBeFlimFlam != null) {

				List<IAttackFlimFlam> flimFlams = FlimFlamRegistry.getAttackFlimFlams();
				Collections.shuffle(flimFlams);

				for (IAttackFlimFlam flimFlam : flimFlams) {
					if (flimFlam.execute(sourcePlayer, targetPlayer, whoBeFlimFlam)) {
						break;
					}
				}
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

	@SuppressWarnings("unchecked")
	public static boolean hasFlimFlam(ItemStack stack) {
		if (stack == null) { return false; }
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		return enchantments.containsKey(Enchantments.flimFlam.effectId);
	}
}
