package openblocks.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import openblocks.OpenBlocks;

public final class EnchantmentExplosive extends Enchantment {
	public EnchantmentExplosive(int id) {
		super(id, OpenBlocks.location("explosive"), 2, EnumEnchantmentType.ARMOR);
		setName("openblocks.explosive");
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public int getMinEnchantability(int level) {
		switch (level) {
			case 1:
				return 15;
			case 2:
				return 25;
			default:
				return 100; // unavailable through enchanting
		}
	}

	@Override
	public int getMaxEnchantability(int level) {
		return getMinEnchantability(level) + 10;
	}
}