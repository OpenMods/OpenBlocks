package openblocks.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockTank;
import openblocks.sync.SyncableTank;

public class ItemTankBlock extends ItemOpenBlock {
	private SyncableTank fakeTank = new SyncableTank(LiquidContainerRegistry.BUCKET_VOLUME * 16);
	public ItemTankBlock(int id) {
		super(id);
		BlockTank.itemId = id;
	}
	
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
    	fakeTank.clear();
    	if (stack.hasTagCompound()) {
    		fakeTank.readFromNBT(stack.getTagCompound(), "tank");
    		list.add(Math.round(fakeTank.getPercentFull() * 100) + "%");
    	}
    }

}
