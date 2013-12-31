package openblocks.common.item;

import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.item.ItemGeneric;

public class ItemOBGenericUnstackable extends ItemGeneric {

	public ItemOBGenericUnstackable() {
		super(Config.itemGenericUnstackableId);
		setMaxStackSize(1);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}
}
