package openblocks.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openblocks.Config;
import openblocks.OpenBlocks;

public class ItemFilledBucket extends ItemGeneric {

	public enum BucketMetas {
		xpbucket {
			@Override
			public IMetaItem createMetaItem() {
				return new MetaGeneric("xpbucket");
			}
		};

		public ItemStack newItemStack(int amount) {
			return OpenBlocks.Items.filledBucket.newItemStack(this, amount);
		}

		public ItemStack newItemStack() {
			return OpenBlocks.Items.filledBucket.newItemStack(this);
		}

		public abstract IMetaItem createMetaItem();

		public boolean isEnabled() {
			return true;
		}
	}

	public ItemFilledBucket() {
		super(Config.itemFilledBucketId);
		setMaxDamage(0);
		setContainerItem(Item.bucketEmpty);
		setMaxStackSize(1);
	}

	@Override
	public void registerItems() {
		for (BucketMetas m : BucketMetas.values())
			if (m.isEnabled()) metaitems.put(m.ordinal(), m.createMetaItem());
	}

	public ItemStack newItemStack(BucketMetas metaenum, int number) {
		return new ItemStack(this, number, metaenum.ordinal());
	}

	public ItemStack newItemStack(BucketMetas metaenum) {
		return new ItemStack(this, 1, metaenum.ordinal());
	}

	public boolean isA(ItemStack stack, BucketMetas meta) {
		return getMeta(stack) == metaitems.get(meta.ordinal());
	}
}
