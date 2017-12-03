package openblocks.common.item;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openblocks.OpenBlocks;
import openmods.colors.ColorMeta;
import openmods.item.IMetaItem;
import openmods.item.IMetaItemFactory;

public enum MetasGenericUnstackable implements IMetaItemFactory {
	pointer {
		@Override
		public IMetaItem createMetaItem() {
			ItemStack result = newItemStack();
			final ItemStack whiteWool = ColorMeta.WHITE.createStack(Blocks.WOOL, 1);
			return new MetaPointer("pointer", new ShapedOreRecipe(result, "w  ", "ww ", "w  ", 'w', whiteWool));
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

	@Override
	public int getMeta() {
		return ordinal();
	}

}