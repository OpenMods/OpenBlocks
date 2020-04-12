package openblocks.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class EnchantmentLastStand extends Enchantment {
	private static final EquipmentSlotType[] ALL_ARMOR = new EquipmentSlotType[] { EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD };

	public EnchantmentLastStand() {
		super(Rarity.UNCOMMON, EnchantmentType.ARMOR, ALL_ARMOR);
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
