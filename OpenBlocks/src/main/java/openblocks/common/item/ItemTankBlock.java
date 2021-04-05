package openblocks.common.item;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import openblocks.common.tileentity.TileEntityTank;
import openmods.model.textureditem.IItemTexture;
import openmods.model.textureditem.ItemTextureCapability;
import openmods.utils.MiscUtils;

public class ItemTankBlock extends BlockItem {
	@OnlyIn(Dist.CLIENT)
	public static class ColorHandler implements IItemColor {
		@Override
		public int getColor(@Nonnull ItemStack stack, int tintIndex) {
			if (tintIndex == 0) {
				final FluidTank tank = readTank(stack);
				final FluidStack fluid = tank.getFluid();
				if (!fluid.isEmpty()) {
					return fluid.getFluid().getAttributes().getColor(fluid);
				}
			}

			return 0xFFFFFFFF;
		}
	}

	public static final String TANK_TAG = "tank";

	public ItemTankBlock(Block block, final Item.Properties properties) {
		super(block, properties);
	}

	private static class FluidHandler implements IFluidHandlerItem {
		private final ItemStack container;

		public FluidHandler(ItemStack container) {
			this.container = container;
		}

		private FluidStack adjustSize(FluidStack stack) {
			if (!stack.isEmpty()) {
				stack = stack.copy();
				stack.setAmount(stack.getAmount() * container.getCount());
			}
			return stack;
		}

		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int id) {
			return adjustSize(readTank(container).getFluidInTank(id));
		}

		@Override
		public int getTankCapacity(int tank) {
			return readTank(container).getTankCapacity(tank) * container.getCount();
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
			return readTank(container).isFluidValid(tank, stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			FluidTank tank = readTank(container);
			final int count = container.getCount();
			if (count == 0) {
				return 0;
			}

			final int amountPerTank = resource.getAmount() / count;
			if (amountPerTank == 0) {
				return 0;
			}

			FluidStack resourcePerTank = resource.copy();
			resourcePerTank.setAmount(amountPerTank);

			int filledPerTank = tank.fill(resourcePerTank, action);
			if (action.execute()) {
				saveTank(container, tank);
			}
			return filledPerTank * count;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (resource.isEmpty()) {
				return FluidStack.EMPTY;
			}

			FluidTank tank = readTank(container);

			if (!resource.isFluidEqual(tank.getFluid())) {
				return FluidStack.EMPTY;
			}

			return drain(resource.getAmount(), action);
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (maxDrain <= 0) {
				return FluidStack.EMPTY;
			}

			FluidTank tank = readTank(container);
			return drain(tank, maxDrain, action);
		}

		private FluidStack drain(FluidTank tank, int maxDrain, FluidAction action) {
			final int count = container.getCount();
			if (count == 0) {
				return FluidStack.EMPTY;
			}

			final int amountPerTank = maxDrain / count;
			if (amountPerTank == 0) {
				return FluidStack.EMPTY;
			}

			FluidStack drained = tank.drain(amountPerTank, action);
			if (action.execute()) {
				saveTank(container, tank);
			}

			return adjustSize(drained);
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
			return getFluidTexture();
		}

		private Optional<ResourceLocation> getFluidTexture() {
			FluidTank tank = readTank(container);
			final FluidStack stack = tank.getFluid();
			if (stack.isEmpty()) {
				return Optional.empty();
			}

			final Fluid fluid = stack.getFluid();
			return Optional.ofNullable(fluid.getAttributes().getStillTexture(stack));
		}

	}

	private static class CapabilityProvider implements ICapabilityProvider {
		private final LazyOptional<FluidHandler> fluidHandler;
		private final LazyOptional<ItemTexture> itemTexture;

		public CapabilityProvider(final ItemStack stack) {
			this.fluidHandler = LazyOptional.of(() -> new FluidHandler(stack));
			this.itemTexture = LazyOptional.of(() -> new ItemTexture(stack));
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
				return fluidHandler.cast();
			}
			if (capability == ItemTextureCapability.CAPABILITY) {
				return itemTexture.cast();
			}
			return LazyOptional.empty();
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return new CapabilityProvider(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> result, ITooltipFlag flag) {
		FluidTank fakeTank = readTank(stack);
		FluidStack fluidStack = fakeTank.getFluid();
		final int amount = fluidStack.getAmount();
		if (amount > 0) {
			float percent = Math.max(100.0f / fakeTank.getCapacity() * amount, 1);
			result.add(new StringTextComponent(String.format("%d mB (%.0f%%)", amount, percent)));

			if (flag.isAdvanced()) {
				final Fluid fluid = fluidStack.getFluid();
				result.add(new TranslationTextComponent(fluid.getAttributes().getTranslationKey(fluidStack)).mergeStyle(TextFormatting.DARK_GRAY));
			}
		}
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		final FluidTank fakeTank = readTank(stack);
		final FluidStack fluidStack = fakeTank.getFluid();

		if (!fluidStack.isEmpty()) {
			final ITextComponent fluidName = MiscUtils.getTranslatedFluidName(fluidStack);
			return new TranslationTextComponent("block.openblocks.tank.filled", fluidName);
		}

		return super.getDisplayName(stack);
	}

	public static boolean fillTankItem(ItemStack result, Fluid fluid) {
		if (result.isEmpty() || !(result.getItem() instanceof ItemTankBlock)) {
			return false;
		}
		final int tankCapacity = TileEntityTank.getTankCapacity();
		FluidTank tank = new FluidTank(tankCapacity);
		tank.setFluid(new FluidStack(fluid, tankCapacity));
		saveTank(result, tank);
		return true;
	}

	public static FluidTank readTank(ItemStack stack) {
		FluidTank tank = new FluidTank(TileEntityTank.getTankCapacity());

		final CompoundNBT itemTag = stack.getTag();
		if (itemTag != null && itemTag.contains(TANK_TAG)) {
			tank.readFromNBT(itemTag.getCompound(TANK_TAG));
			return tank;
		}

		return tank;
	}

	private static void saveTank(@Nonnull ItemStack container, FluidTank tank) {
		CompoundNBT itemTag = container.getOrCreateTag();
		if (tank.getFluidAmount() > 0) {

			CompoundNBT tankTag = new CompoundNBT();
			tank.writeToNBT(tankTag);
			itemTag.put(TANK_TAG, tankTag);
		} else {
			itemTag.remove(TANK_TAG);
			if (itemTag.isEmpty()) {
				container.setTag(null);
			}
		}
	}
}
