package openblocks.common.item;

import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.liquids.SingleFluidBucketHandler;

public class ItemXpBucket extends Item {

	public static class CapabilityProvider implements ICapabilityProvider {

		private final IFluidHandlerItem fluidHandler;

		public CapabilityProvider(ItemStack container) {
			this.fluidHandler = new SingleFluidBucketHandler(container, new ItemStack(Items.BUCKET), OpenBlocks.Fluids.xpJuice, Fluid.BUCKET_VOLUME);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
				return (T)fluidHandler;

			return null;
		}
	}

	public ItemXpBucket() {
		setContainerItem(Items.BUCKET);
		setMaxStackSize(1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new CapabilityProvider(stack);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (Config.showXpBucketInCreative)
			super.getSubItems(tab, items);
	}

}
