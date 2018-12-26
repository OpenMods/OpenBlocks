package openblocks.common.item;

import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openmods.infobook.BookDocumentation;

@BookDocumentation(customName = "epic_eraser")
public class ItemEpicEraser extends Item {

	private static final int MAX_DAMAGE = 15;

	public ItemEpicEraser() {
		setMaxStackSize(1);
		setMaxDamage(MAX_DAMAGE);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	@Nonnull
	public ItemStack getContainerItem(ItemStack itemStack) {
		final ItemStack result = itemStack.copy();
		result.setItemDamage(result.getItemDamage() + 1);
		return result;
	}

	@Override
	public boolean hasContainerItem(ItemStack stack) {
		return stack.getItemDamage() < MAX_DAMAGE - 1;
	}

}
