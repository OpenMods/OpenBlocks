package openblocks.common.item;

import openblocks.OpenBlocks;
import openmods.infobook.BookDocumentation;
import openmods.item.ItemGeneric;

@BookDocumentation(customProvider = MetasGeneric.DocProvider.class)
public class ItemOBGeneric extends ItemGeneric {

	public ItemOBGeneric() {
		setMaxStackSize(64);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}
}
