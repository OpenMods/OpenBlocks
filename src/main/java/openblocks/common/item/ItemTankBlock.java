package openblocks.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;
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
		NBTTagCompound itemTag = ItemUtils.getItemTag(container);

		NBTTagCompound tankTag = new NBTTagCompound();
		tank.writeToNBT(tankTag);
		itemTag.setTag(TANK_TAG, tankTag);
	}

	@Override
	public FluidStack getFluid(ItemStack container) {
		FluidTank tank = readTank(container);
		return tank != null? tank.getFluid() : null;
	}

	@Override
	public int getCapacity(ItemStack container) {
		FluidTank tank = readTank(container);
		return tank != null? tank.getCapacity() : 0;
	}

	@Override
	public int fill(ItemStack container, FluidStack resource, boolean doFill) {
		FluidTank tank = readTank(container);
		if (tank == null) return 0;

		int result = tank.fill(resource, doFill);
		if (doFill) saveTank(container, tank);
		return result;
	}

	@Override
	public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
		FluidTank tank = readTank(container);
		if (tank == null) return null;

		FluidStack result = tank.drain(maxDrain, doDrain);
		if (doDrain) saveTank(container, tank);
		return result;
	}

}
