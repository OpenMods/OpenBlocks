package openblocks.common.item;

import openblocks.OpenBlocks;
import openmods.item.ItemGeneric;

public class ItemOBGenericUnstackable extends ItemGeneric {

	public ItemOBGenericUnstackable() {
		setMaxStackSize(1);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}
}
