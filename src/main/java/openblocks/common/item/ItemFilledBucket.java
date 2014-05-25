package openblocks.common.item;

import net.minecraft.init.Items;
import openblocks.OpenBlocks;
import openmods.item.ItemGeneric;

public class ItemFilledBucket extends ItemGeneric {

	public ItemFilledBucket() {
		setContainerItem(Items.bucket);
		setMaxStackSize(1);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}
}
