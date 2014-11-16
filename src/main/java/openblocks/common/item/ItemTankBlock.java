package openblocks.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.*;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;
import openmods.item.ItemOpenBlock;
import openmods.utils.ItemUtils;

public class ItemTankBlock extends ItemOpenBlock implements IFluidContainerItem {

	public static final String TANK_TAG = "tank";

	public ItemTankBlock(Block block) {
		super(block);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		FluidTank fakeTank = readTank(stack);
		FluidStack liquid = fakeTank.getFluid();
		if (liquid != null && liquid.amount > 0) {
			float percent = Math.max(100.0f / fakeTank.getCapacity() * liquid.amount, 1);
			list.add(String.format("%d mB (%.0f%%)", liquid.amount, percent));
		}
	}

	public static ItemStack createFilledTank(Fluid fluid) {
		final int tankCapacity = TileEntityTank.getTankCapacity();
		FluidStack stack = new FluidStack(fluid, tankCapacity);
		FluidTank tank = new FluidTank(tankCapacity);
		tank.setFluid(stack);

		ItemStack item = new ItemStack(OpenBlocks.Blocks.tank);
		saveTank(item, tank);
		return item;
	}

	private static FluidTank readTank(ItemStack stack) {
		FluidTank tank = new FluidTank(TileEntityTank.getTankCapacity());

		final NBTTagCompound itemTag = stack.getTagCompound();
		if (itemTag != null && itemTag.hasKey(TANK_TAG)) {
			tank.readFromNBT(itemTag.getCompoundTag(TANK_TAG));
			return tank;
		}

		return tank;
	}

	private static void saveTank(ItemStack container, FluidTank tank) {
		if (tank.getFluidAmount() > 0) {
			NBTTagCompound itemTag = ItemUtils.getItemTag(container);

			NBTTagCompound tankTag = new NBTTagCompound();
			tank.writeToNBT(tankTag);
			itemTag.setTag(TANK_TAG, tankTag);
		} else {
			container.stackTagCompound = null;
		}
	}

	@Override
	public FluidStack getFluid(ItemStack container) {
		FluidTank tank = readTank(container);
		if (tank == null) return null;
		FluidStack result = tank.getFluid();
		if (result != null) result.amount *= container.stackSize;
		return result;
	}

	@Override
	public int getCapacity(ItemStack container) {
		FluidTank tank = readTank(container);
		return tank != null? tank.getCapacity() * container.stackSize : 0;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		if (resource == null) return 0;

		FluidTank tank = readTank(container);
		if (tank == null) return 0;

		final int count = container.stackSize;
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
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		if (maxDrain <= 0) return null;

		FluidTank tank = readTank(container);
		if (tank == null) return null;

		final int count = container.stackSize;
		if (count == 0) return null;

		final int amountPerTank = maxDrain / count;
		if (amountPerTank == 0) return null;

		FluidStack drained = tank.drain(amountPerTank, doDrain);
		if (doDrain) saveTank(container, tank);

		if (drained != null) drained.amount *= count;

		return drained;
	}

}
