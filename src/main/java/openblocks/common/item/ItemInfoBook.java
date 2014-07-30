package openblocks.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInfoBook extends Item {

	public ItemInfoBook() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister registry) {
		itemIcon = registry.registerIcon("openblocks:info_book");
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.infoBook.ordinal(), player.worldObj, 0, 0, 0);

		return stack;
	}
}
