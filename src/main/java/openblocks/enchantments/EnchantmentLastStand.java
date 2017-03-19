package openblocks.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentLastStand extends Enchantment {
	private static final EntityEquipmentSlot[] ALL_ARMOR = new EntityEquipmentSlot[] { EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD };

	public EnchantmentLastStand() {
		super(Rarity.UNCOMMON, EnumEnchantmentType.ARMOR, ALL_ARMOR);
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
