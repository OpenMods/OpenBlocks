package openblocks.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

public class EnchantmentLastStand extends Enchantment {

	public EnchantmentLastStand(int id) {
		super(id, 2, EnumEnchantmentType.armor);
		setName("openblocks.laststand");
	}

	@Override
	public int getMaxLevel() {
		return 2;
	}

	@Override
	public int getMinEnchantability(int level) {
		switch (level) {
			case 1:
				return 15;
			default:
				return 25;
		}
	}

	@Override
	public int getMaxEnchantability(int level) {
		return getMinEnchantability(level) + 10;
	}
}
