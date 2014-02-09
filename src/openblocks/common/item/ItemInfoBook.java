package openblocks.common.item;

import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemInfoBook extends Item {

	public ItemInfoBook() {
		super(Config.itemInfoBookId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}
	
	@Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    	if (world.isRemote) {
    		player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.infoBook.ordinal(), player.worldObj, 0, 0, 0);
    	}
    	
    	return stack;
    }
}
