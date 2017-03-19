package openblocks.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public final class EnchantmentExplosive extends Enchantment {
	private static final EntityEquipmentSlot[] ALL_ARMOR = new EntityEquipmentSlot[] { EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD };

	public EnchantmentExplosive() {
		super(Rarity.RARE, EnumEnchantmentType.ARMOR, ALL_ARMOR);
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