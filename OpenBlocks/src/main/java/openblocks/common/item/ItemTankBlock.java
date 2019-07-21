package openblocks.common.item;

import com.google.common.base.Strings;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.tileentity.TileEntityTank;
import openmods.item.ItemOpenBlock;
import openmods.model.textureditem.IItemTexture;
import openmods.model.textureditem.ItemTextureCapability;
import openmods.utils.ItemUtils;
import openmods.utils.MiscUtils;
import openmods.utils.TranslationUtils;

public class ItemTankBlock extends ItemOpenBlock {

	@SideOnly(Side.CLIENT)
	public static class ColorHandler implements IItemColor {
		@Override
		public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
			if (tintIndex == 0) {
				final FluidTank tank = readTank(stack);
				final FluidStack fluid = tank.getFluid();
				if (fluid != null) return fluid.getFluid().getColor(fluid);
			}

			return 0xFFFFFFFF;
		}
	}

	public static final String TANK_TAG = "tank";

	public ItemTankBlock(Block block) {
		super(block);

		addPropertyOverride(new ResourceLocation("level"), (stack, worldIn, entityIn) -> {
			final FluidTank tank = readTank(stack);
			return 16.0f * tank.getFluidAmount() / tank.getCapacity();
		});
	}

	private static class FluidHandler implements IFluidHandlerItem {
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

		@Override
		public ItemStack getContainer() {
			return container;
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
			if (stack == null) return Optional.empty();

			final Fluid fluid = stack.getFluid();
			return Optional.ofNullable(fluid.getStill());
		}

	}

	private static class CapabilityProvider implements ICapabilityProvider {
		private final IFluidHandlerItem fluidHandler;
		private final IItemTexture itemTexture;

		public CapabilityProvider(IFluidHandlerItem fluidHandler, IItemTexture itemTexture) {
			this.fluidHandler = fluidHandler;
			this.itemTexture = itemTexture;
		}

		@Override
		public boolean hasCapability(Capability<?> capability, Direction facing) {
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ||
					capability == ItemTextureCapability.CAPABILITY;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T getCapability(Capability<T> capability, Direction facing) {
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
				return (T)fluidHandler;

			if (capability == ItemTextureCapability.CAPABILITY)
				return (T)itemTexture;

			return null;
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new CapabilityProvider(new FluidHandler(stack), new ItemTexture(stack));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> result, ITooltipFlag flag) {
		FluidTank fakeTank = readTank(stack);
		FluidStack fluidStack = fakeTank.getFluid();
		if (fluidStack != null && fluidStack.amount > 0) {
			float percent = Math.max(100.0f / fakeTank.getCapacity() * fluidStack.amount, 1);
			result.add(String.format("%d mB (%.0f%%)", fluidStack.amount, percent));

			if (flag.isAdvanced()) {
				final Fluid fluid = fluidStack.getFluid();
				result.add(TextFormatting.DARK_GRAY + FluidRegistry.getDefaultFluidName(fluid) + TextFormatting.RESET);
			}
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
	public static boolean fillTankItem(ItemStack result, Fluid fluid) {
		if (result.isEmpty() || !(result.getItem() instanceof ItemTankBlock)) return false;
		final int tankCapacity = TileEntityTank.getTankCapacity();
		FluidStack stack = FluidRegistry.getFluidStack(fluid.getName(), tankCapacity);
		if (stack == null) return false;

		FluidTank tank = new FluidTank(tankCapacity);
		tank.setFluid(stack);

		saveTank(result, tank);
		return true;
	}

	private static FluidTank readTank(@Nonnull ItemStack stack) {
		FluidTank tank = new FluidTank(TileEntityTank.getTankCapacity());

		final CompoundNBT itemTag = stack.getTagCompound();
		if (itemTag != null && itemTag.hasKey(TANK_TAG)) {
			tank.readFromNBT(itemTag.getCompoundTag(TANK_TAG));
			return tank;
		}

		return tank;
	}

	private static void saveTank(@Nonnull ItemStack container, FluidTank tank) {
		if (tank.getFluidAmount() > 0) {
			CompoundNBT itemTag = ItemUtils.getItemTag(container);

			CompoundNBT tankTag = new CompoundNBT();
			tank.writeToNBT(tankTag);
			itemTag.setTag(TANK_TAG, tankTag);
		} else {
			container.setTagCompound(null);
		}
	}

}
