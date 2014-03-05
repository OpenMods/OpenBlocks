package openblocks.enchantments.flimflams;

import java.util.Map;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openblocks.api.IAttackFlimFlam;
import openmods.utils.BlockUtils;

public class UselessToolFlimFlam implements IAttackFlimFlam {

	@Override
	public void execute(EntityPlayer source, EntityPlayer target) {
		if (source.worldObj.isRemote) return;
		if (source.worldObj.rand.nextDouble() > 0.05) return;
		ItemStack diamondPick = new ItemStack(Item.pickaxeDiamond);
		Map<Integer, EnchantmentData> enchantments = EnchantmentHelper.mapEnchantmentData(10, diamondPick);
		for (EnchantmentData entry : enchantments.values()) {
			diamondPick.addEnchantment(entry.enchantmentobj, entry.enchantmentLevel);
		}
		diamondPick.setItemDamage(diamondPick.getMaxDamage());
		BlockUtils.dropItemStackInWorld(source.worldObj, target.posX, target.posY, target.posZ, diamondPick);
	}

	@Override
	public String name() {
		return "uselesstool";
	}

	@Override
	public float weight() {
		return 1;
	}

}
