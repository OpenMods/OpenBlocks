package openblocks.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockTank;

public class ItemTankBlock extends ItemOpenBlock {
	private LiquidTank fakeTank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME
			* OpenBlocks.Config.bucketsPerTank);

	public ItemTankBlock(int id) {
		super(id);
		BlockTank.itemId = id;
	}

	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		fakeTank.setLiquid(null);
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tank")) {
			fakeTank.readFromNBT(stack.getTagCompound().getCompoundTag("tank"));
			LiquidStack liquid = fakeTank.getLiquid();
			int amount = 0;
			if (liquid != null) {
				amount = liquid.amount;
			}
			double percent = 100.0 / fakeTank.getCapacity() * amount;
			list.add(Math.round(percent) + "%");
			list.add(amount + "mB");
		}
	}

}
