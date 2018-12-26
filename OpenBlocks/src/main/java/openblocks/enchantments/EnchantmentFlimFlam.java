package openblocks.enchantments;

import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class EnchantmentFlimFlam extends Enchantment {

	private static final EntityEquipmentSlot[] ALL_ARMOR = new EntityEquipmentSlot[] { EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.MAINHAND };

	public EnchantmentFlimFlam() {
		super(Rarity.RARE, EnumEnchantmentType.ALL, ALL_ARMOR);
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

	@Override
	@Nonnull
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		final Item item = stack.getItem();
		return (item instanceof ItemArmor) || (item instanceof ItemSword);
	}

}
