package openblocks.common.item;

import net.minecraft.item.ItemStack;
import openblocks.OpenBlocks;
import openmods.item.IMetaItem;
import openmods.item.IMetaItemFactory;

public enum MetasGenericUnstackable implements IMetaItemFactory {
	pointer {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaPointer("pointer");
		}
	};
	public ItemStack newItemStack(int size) {
		return new ItemStack(OpenBlocks.Items.genericUnstackable, size, ordinal());
	}

	public ItemStack newItemStack() {
		return newItemStack(1);
	}

	public boolean isA(ItemStack stack) {
		return (stack.getItem() == OpenBlocks.Items.genericUnstackable) && (stack.getItemDamage() == ordinal());
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public boolean isAvailable() {
		return OpenBlocks.Items.genericUnstackable != null && isEnabled();
	}

	@Override
	public int getMeta() {
		return ordinal();
	}

}