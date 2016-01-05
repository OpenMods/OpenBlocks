package openblocks.common.item;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Items;
import openmods.colors.ColorMeta;
import openmods.item.IMetaItem;

public enum MetasGenericUnstackable {
	pointer {
		@Override
		protected IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			final ItemStack whiteWool = ColorMeta.WHITE.createStack(Blocks.wool, 1);
			return new MetaPointer("pointer", new ShapedOreRecipe(result, "w  ", "ww ", "w  ", 'w', whiteWool));
		}
	};
	public ItemStack newItemStack(int size) {
		return new ItemStack(OpenBlocks.Items.genericUnstackable, size, ordinal());
	}

	public ItemStack newItemStack() {
		return new ItemStack(OpenBlocks.Items.genericUnstackable, 1, ordinal());
	}

	public boolean isA(ItemStack stack) {
		return (stack.getItem() == OpenBlocks.Items.genericUnstackable) && (stack.getItemDamage() == ordinal());
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