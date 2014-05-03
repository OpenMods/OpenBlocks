package openblocks.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

public class EnchantmentFlimFlam extends Enchantment {

	public EnchantmentFlimFlam(int id) {
		super(id, 2, EnumEnchantmentType.all);
		setName("openblocks.flimflam");
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getMinEnchantability(int level) {
		return 31 + level * 10;
	}

	@Override
	public int getMaxEnchantability(int level) {
		return getMinEnchantability(level) + 10;
	}
}
