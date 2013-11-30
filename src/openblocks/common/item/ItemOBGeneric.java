package openblocks.common.item;

import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.item.ItemGeneric;

public class ItemOBGeneric extends ItemGeneric {

	public ItemOBGeneric() {
		super(Config.itemGenericId);
        setMaxStackSize(64);
        setCreativeTab(OpenBlocks.tabOpenBlocks);
	}
}
