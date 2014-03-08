package openblocks.enchantments.flimflams;

import java.util.Map;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamEffect;
import openmods.utils.BlockUtils;

public class UselessToolFlimFlam implements IFlimFlamEffect {

	@Override
	public boolean execute(EntityPlayer target) {
		ItemStack diamondPick = new ItemStack(Item.pickaxeDiamond);
		@SuppressWarnings("unchecked")
		Map<Integer, EnchantmentData> enchantments = EnchantmentHelper.mapEnchantmentData(10, diamondPick);
		for (EnchantmentData entry : enchantments.values()) {
			diamondPick.addEnchantment(entry.enchantmentobj, entry.enchantmentLevel);
		}
		diamondPick.setItemDamage(diamondPick.getMaxDamage());
		BlockUtils.dropItemStackInWorld(target.worldObj, target.posX, target.posY, target.posZ, diamondPick);

		return true;
	}

	@Override
	public String name() {
		return "useless-tool";
	}

	@Override
	public float weight() {
		return 1;
	}

	@Override
	public float cost() {
		return 50;
	}

}
