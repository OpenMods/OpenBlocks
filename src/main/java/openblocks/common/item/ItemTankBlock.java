package openblocks.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import openblocks.common.tileentity.TileEntityTank;
import openmods.item.ItemOpenBlock;

public class ItemTankBlock extends ItemOpenBlock {
	private FluidTank fakeTank = new FluidTank(TileEntityTank.getTankCapacity());

	public ItemTankBlock(int id) {
		super(id);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		fakeTank.setFluid(null);
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tank")) {
			fakeTank.readFromNBT(stack.getTagCompound().getCompoundTag("tank"));
			FluidStack liquid = fakeTank.getFluid();
			if (liquid != null && liquid.amount > 0) {
				double percent = Math.max(100.0 / fakeTank.getCapacity() * liquid.amount, 1);
				list.add(Math.round(percent) + "%");
				list.add(liquid.amount + "mB");
			}
		}
	}

}
