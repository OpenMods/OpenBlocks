package openblocks.common.item;

import net.minecraft.item.ItemStack;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Items;
import openmods.item.IMetaItem;

public enum MetasGenericUnstackable {
	pointer {
		@Override
		protected IMetaItem createMetaItem() {
			return new MetaPointer("pointer", new Object[0]);
		}
	};
	public ItemStack newItemStack(int size) {
		return new ItemStack(OpenBlocks.Items.genericUnstackable, size, ordinal());
	}

	public ItemStack newItemStack() {
		return new ItemStack(OpenBlocks.Items.genericUnstackable, 1, ordinal());
	}

	public boolean isA(ItemStack stack) {
		return (stack.getItem() instanceof ItemOBGenericUnstackable) && (stack.getItemDamage() == ordinal());
	}

	protected abstract IMetaItem createMetaItem();

	protected boolean isEnabled() {
		return true;
	}

	public static void registerItems() {
		for (MetasGenericUnstackable m : values())
			if (m.isEnabled()) Items.genericUnstackable.registerItem(m.ordinal(), m.createMetaItem());
	}
}