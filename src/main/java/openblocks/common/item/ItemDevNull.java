package openblocks.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import openmods.ItemInventory;
import openmods.PlayerItemInventory;
import openmods.utils.InventoryUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDevNull extends Item {

	public static class Icons {
		public static IIcon iconFull;
		public static IIcon iconTransparent;
	}

	public ItemDevNull() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.devNull.ordinal(), world, player.inventory.currentItem, 0, 0);
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		PlayerItemInventory inventory = new PlayerItemInventory(player, 1);
		ItemStack containedStack = inventory.getStackInSlot(0);
		if (containedStack != null) {
			Item item = containedStack.getItem();
			if (item instanceof ItemBlock) {
				boolean response = ((ItemBlock)item).onItemUse(containedStack, player, world, x, y, z, par7, par8, par9, par10);
				if (containedStack.stackSize == 0) {
					inventory.setInventorySlotContents(0, null);
				}
				inventory.markDirty();
				return response;
			}
		}
		return true;
	}

	@SubscribeEvent
	public void onItemPickUp(EntityItemPickupEvent evt) {

		final EntityPlayer player = evt.entityPlayer;
		final ItemStack pickedStack = evt.item.getEntityItem();

		if (pickedStack == null || player == null) return;

		boolean foundMatchingContainer = false;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null && stack.getItem() == this) {
				ItemInventory inventory = new ItemInventory(stack, 1);
				ItemStack containedStack = inventory.getStackInSlot(0);
				if (containedStack != null) {
					boolean isMatching = InventoryUtils.areItemAndTagEqual(pickedStack, containedStack);
					if (isMatching) {
						foundMatchingContainer = true;
						InventoryUtils.tryInsertStack(inventory, 0, pickedStack, true);
					}
				}
			}
		}

		if (foundMatchingContainer) pickedStack.stackSize = 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		Icons.iconTransparent = itemIcon = register.registerIcon("openblocks:devnull");
		Icons.iconFull = register.registerIcon("openblocks:devfull");
	}
}
