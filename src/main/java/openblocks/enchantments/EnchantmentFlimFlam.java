package openblocks.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import openblocks.OpenBlocks;

public class EnchantmentFlimFlam extends Enchantment {

	public EnchantmentFlimFlam(int id) {
		super(id, OpenBlocks.location("flimflam"), 2, EnumEnchantmentType.ALL);
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
