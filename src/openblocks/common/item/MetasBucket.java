package openblocks.common.item;

import net.minecraft.item.ItemStack;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Items;
import openmods.item.IMetaItem;

public enum MetasBucket {
	xpbucket {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("xpbucket");
		}
	};

	public ItemStack newItemStack(int size) {
		return new ItemStack(OpenBlocks.Items.filledBucket, size, ordinal());
	}

	public ItemStack newItemStack() {
		return new ItemStack(OpenBlocks.Items.filledBucket, 1, ordinal());
	}

	public boolean isA(ItemStack stack) {
		return (stack.getItem() instanceof ItemFilledBucket) && (stack.getItemDamage() == ordinal());
	}

	protected abstract IMetaItem createMetaItem();

	protected boolean isEnabled() {
		return true;
	}

	public static void registerItems() {
		for (MetasBucket m : values())
			if (m.isEnabled()) Items.filledBucket.registerItem(m.ordinal(), m.createMetaItem());
	}
}