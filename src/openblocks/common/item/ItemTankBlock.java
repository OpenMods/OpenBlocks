package openblocks.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import openblocks.common.tileentity.TileEntityTank;

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
			int amount = 0;
			if (liquid != null) {
				amount = liquid.amount;
			}
			double percent = Math.max(100.0 / fakeTank.getCapacity() * amount, amount > 0? 1 : 0);
			list.add(Math.round(percent) + "%");
			list.add(amount + "mB");
		}
	}

}
