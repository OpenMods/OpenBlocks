package openblocks.common.item;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;
import openmods.item.ItemOpenBlock;
import openmods.model.textureditem.IItemTexture;
import openmods.model.textureditem.ItemTextureCapability;
import openmods.utils.ItemUtils;
import openmods.utils.MiscUtils;
import openmods.utils.TranslationUtils;

public class ItemTankBlock extends ItemOpenBlock {

	public static final String TANK_TAG = "tank";

	public ItemTankBlock(Block block) {
		super(block);

		addPropertyOverride(new ResourceLocation("level"), new IItemPropertyGetter() {
			@Override
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
				final FluidTank tank = readTank(stack);
				return 16.0f * tank.getFluidAmount() / tank.getCapacity();
			}
		});
	}

	private static class FluidHandler implements IFluidHandler {
		private final ItemStack container;

		public FluidHandler(ItemStack container) {
			this.container = container;
		}

		private FluidStack getFluid(FluidTank tank) {
			FluidStack result = tank.getFluid();
			if (result != null) result.amount *= container.getCount();
			return result;
		}

		private int getCapacity(FluidTank tank) {
			return tank != null? tank.getCapacity() * container.getCount() : 0;
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {
			final FluidTank tank = readTank(container);
			return new IFluidTankProperties[] { new FluidTankProperties(getFluid(tank), getCapacity(tank), true, true) };
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if (resource == null) return 0;

			FluidTank tank = readTank(container);
			if (tank == null) return 0;

			final int count = container.getCount();
			if (count == 0) return 0;

			final int amountPerTank = resource.amount / count;
			if (amountPerTank == 0) return 0;

			FluidStack resourcePerTank = resource.copy();
			resourcePerTank.amount = amountPerTank;

			int filledPerTank = tank.fill(resourcePerTank, doFill);
			if (doFill) saveTank(container, tank);
			return filledPerTank * count;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if (resource == null) return null;

			FluidTank tank = readTank(container);
			if (tank == null) return null;

			if (!resource.isFluidEqual(tank.getFluid())) return null;

			return drain(resource.amount, doDrain);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			if (maxDrain <= 0) return null;

			FluidTank tank = readTank(container);
			if (tank == null) return null;

			return drain(tank, maxDrain, doDrain);
		}

		private FluidStack drain(FluidTank tank, int maxDrain, boolean doDrain) {
			final int count = container.getCount();
			if (count == 0) return null;

			final int amountPerTank = maxDrain / count;
			if (amountPerTank == 0) return null;

			FluidStack drained = tank.drain(amountPerTank, doDrain);
			if (doDrain) saveTank(container, tank);

			if (drained != null) drained.amount *= count;

			return drained;
		}

	}

	private static class ItemTexture implements IItemTexture {

		private final ItemStack container;

		public ItemTexture(ItemStack container) {
			this.container = container;
		}

		@Override
		public Optional<ResourceLocation> getTexture() {
			FluidTank tank = readTank(container);
			final FluidStack stack = tank.getFluid();
			if (stack == null) return Optional.absent();

			final Fluid fluid = stack.getFluid();
			return Optional.fromNullable(fluid.getStill());
		}

	}

	private static class CapabilityProvider implements ICapabilityProvider {
		private final IFluidHandler fluidHandler;
		private final IItemTexture itemTexture;

		public CapabilityProvider(IFluidHandler fluidHandler, IItemTexture itemTexture) {
			this.fluidHandler = fluidHandler;
			this.itemTexture = itemTexture;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
					capability == ItemTextureCapability.CAPABILITY;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
				return (T)fluidHandler;

			if (capability == ItemTextureCapability.CAPABILITY)
				return (T)itemTexture;

			return null;
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		return new CapabilityProvider(new FluidHandler(stack), new ItemTexture(stack));
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, EntityPlayer player, List<String> list, boolean extended) {
		FluidTank fakeTank = readTank(stack);
		FluidStack fluidStack = fakeTank.getFluid();
		if (fluidStack != null && fluidStack.amount > 0) {
			float percent = Math.max(100.0f / fakeTank.getCapacity() * fluidStack.amount, 1);
			list.add(String.format("%d mB (%.0f%%)", fluidStack.amount, percent));
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		final FluidTank fakeTank = readTank(stack);
		final FluidStack fluidStack = fakeTank.getFluid();
		final String unlocalizedName = getUnlocalizedName();

		if (fluidStack != null && fluidStack.amount > 0) {
			final String fluidName = MiscUtils.getTranslatedFluidName(fluidStack);
			if (!Strings.isNullOrEmpty(fluidName))
				return TranslationUtils.translateToLocalFormatted(unlocalizedName + ".filled.name", fluidName);
		}

		return super.getItemStackDisplayName(stack);
	}

	@Nonnull
	public static ItemStack createFilledTank(Fluid fluid) {
		final int tankCapacity = TileEntityTank.getTankCapacity();
		FluidStack stack = FluidRegistry.getFluidStack(fluid.getName(), tankCapacity);
		if (stack == null) return ItemStack.EMPTY;

		FluidTank tank = new FluidTank(tankCapacity);
		tank.setFluid(stack);

		ItemStack item = new ItemStack(OpenBlocks.Blocks.tank);
		saveTank(item, tank);
		return item;
	}

	private static FluidTank readTank(@Nonnull ItemStack stack) {
		FluidTank tank = new FluidTank(TileEntityTank.getTankCapacity());

		final NBTTagCompound itemTag = stack.getTagCompound();
		if (itemTag != null && itemTag.hasKey(TANK_TAG)) {
			tank.readFromNBT(itemTag.getCompoundTag(TANK_TAG));
			return tank;
		}

		return tank;
	}

	private static void saveTank(@Nonnull ItemStack container, FluidTank tank) {
		if (tank.getFluidAmount() > 0) {
			NBTTagCompound itemTag = ItemUtils.getItemTag(container);

			NBTTagCompound tankTag = new NBTTagCompound();
			tank.writeToNBT(tankTag);
			itemTag.setTag(TANK_TAG, tankTag);
		} else {
			container.setTagCompound(null);
		}
	}

}
