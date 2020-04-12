package openblocks.enchantments;

import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class EnchantmentFlimFlam extends Enchantment {

	private static final EquipmentSlotType[] ALL_ARMOR = new EquipmentSlotType[] { EquipmentSlotType.FEET, EquipmentSlotType.LEGS, EquipmentSlotType.CHEST, EquipmentSlotType.HEAD, EquipmentSlotType.MAINHAND };

	public EnchantmentFlimFlam() {
		super(Rarity.RARE, EnchantmentType.ALL, ALL_ARMOR);
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
		return (item instanceof ArmorItem) || (item instanceof SwordItem);
	}

}
