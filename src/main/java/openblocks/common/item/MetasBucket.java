package openblocks.common.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Fluids;
import openblocks.OpenBlocks.Items;
import openmods.item.IMetaItem;

public enum MetasBucket {
	xpbucket(new FluidStack(Fluids.xpJuice, FluidContainerRegistry.BUCKET_VOLUME)) {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("xpbucket");
		}
	};

	private final FluidStack liquid;

	private MetasBucket(FluidStack liquid) {
		this.liquid = liquid;
	}

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
			if (m.isEnabled()) {
				Items.filledBucket.registerItem(m.ordinal(), m.createMetaItem());
				FluidContainerRegistry.registerFluidContainer(m.liquid.copy(), m.newItemStack(), FluidContainerRegistry.EMPTY_BUCKET);
			}
	}
}