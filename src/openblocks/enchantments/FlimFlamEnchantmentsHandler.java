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

public class FlimFlamEnchantmentsHandler {

	public static Random rnd = new Random();

	@ForgeSubscribe
	public void onDamage(LivingAttackEvent e) {
		if (!(e.entityLiving instanceof EntityPlayer)) return;

		final EntityPlayer targetPlayer = (EntityPlayer)e.entityLiving;

		if (e.source == null) return;
		final Entity damageSource = e.source.getEntity();

		if (!(damageSource instanceof EntityPlayer)) return;
		final EntityPlayer sourcePlayer = (EntityPlayer)damageSource;

		// flim flam yerself?
		if (sourcePlayer == targetPlayer) return;

		final int sourceFlimFlam = getFlimFlamToolLevel(sourcePlayer);
		final int targetFlimFlam = getFlimFlamArmorLevel(targetPlayer);

		if (sourceFlimFlam + targetFlimFlam <= 0) return;

		List<IAttackFlimFlam> flimFlams = FlimFlamRegistry.getAttackFlimFlams();
		Collections.shuffle(flimFlams);

		// TODO Select n needed elements based on probability (probaly sample
		// with replacement, why not)

		Iterator<IAttackFlimFlam> it = flimFlams.iterator();

		for (int i = 0; i < sourceFlimFlam; i++) {
			if (!it.hasNext()) break;
			IAttackFlimFlam flimFlam = it.next();
			flimFlam.execute(sourcePlayer, targetPlayer);
		}

		for (int i = 0; i < targetFlimFlam; i++) {
			if (!it.hasNext()) break;
			IAttackFlimFlam flimFlam = it.next();
			flimFlam.execute(targetPlayer, sourcePlayer);
		}
	}

	private static int getFlimFlamToolLevel(EntityPlayer player) {
		return getFlimFlamLevel(player.getHeldItem());
	}

	private static int getFlimFlamArmorLevel(EntityPlayer player) {
		int sum = 0;
		for (ItemStack stack : player.inventory.armorInventory) {
			sum += getFlimFlamLevel(stack);
		}
		return sum;
	}

	@SuppressWarnings("unchecked")
	public static int getFlimFlamLevel(ItemStack stack) {
		if (stack == null) return 0;
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		Integer result = enchantments.get(Enchantments.flimFlam.effectId);
		return result != null? result : 0;
	}
}
