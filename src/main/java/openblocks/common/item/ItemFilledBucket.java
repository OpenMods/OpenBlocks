package openblocks.common.item;

import net.minecraft.item.Item;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.item.ItemGeneric;

public class ItemFilledBucket extends ItemGeneric {

	public ItemFilledBucket() {
		super(Config.itemFilledBucketId);
		setContainerItem(Item.bucketEmpty);
		setMaxStackSize(1);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}
}
