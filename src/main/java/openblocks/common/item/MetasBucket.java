package openblocks.common.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import openblocks.OpenBlocks;
import openmods.item.IMetaItem;
import openmods.item.IMetaItemFactory;

public enum MetasBucket implements IMetaItemFactory {
	xpbucket {
		@Override
		public IMetaItem createMetaItem() {
			return new MetaGeneric("xpbucket");
		}
	};

	public ItemStack newItemStack(int size) {
		return new ItemStack(OpenBlocks.Items.filledBucket, size, ordinal());
	}

	public ItemStack newItemStack() {
		return new ItemStack(OpenBlocks.Items.filledBucket, 1, ordinal());
	}

	public boolean isA(ItemStack stack) {
		return (stack.getItem() instanceof ItemFilledBucket) && (stack.getItemDamage() == ordinal());
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int getMeta() {
		return ordinal();
	}

	public void registerAsBucketFor(Fluid fluid) {
		registerAsContainerFor(new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME), FluidContainerRegistry.EMPTY_BUCKET);
	}

	public void registerAsContainerFor(FluidStack fluid, ItemStack emptyContainer) {
		FluidContainerRegistry.registerFluidContainer(fluid.copy(), newItemStack(), emptyContainer);
	}
}