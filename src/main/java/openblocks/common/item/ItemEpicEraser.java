package openblocks.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;

@BookDocumentation(customName = "epic_eraser")
public class ItemEpicEraser extends Item {

	public ItemEpicEraser() {
		setMaxStackSize(1);
		setMaxDamage(15);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setTextureName("openblocks:epic_eraser");
	}

	@Override
	public boolean hasEffect(ItemStack p_77636_1_) {
		return true;
	}

	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack p_77630_1_) {
		return false;
	}

	@Override
	public ItemStack getContainerItem(ItemStack itemStack) {
		final ItemStack result = itemStack.copy();
		result.setItemDamage(result.getItemDamage() + 1);
		return result;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return stack.getItemDamage() < getMaxDamage() - 1;
	}

}
