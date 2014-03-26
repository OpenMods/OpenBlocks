package openblocks.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import openmods.ItemInventory;
import openmods.utils.InventoryUtils;

public class ItemDevNull extends Item {

	public ItemDevNull() {
		super(Config.itemDevNullId);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.devNull.ordinal(), world, player.inventory.currentItem, 0, 0);
			}
		}
		return stack;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (player.isSneaking()) {
			ItemInventory inventory = new ItemInventory(player, 1);
			ItemStack containedStack = inventory.getStackInSlot(0);
			if (containedStack != null) {
				Item item = containedStack.getItem();
				if (item instanceof ItemBlock) {
					boolean response = ((ItemBlock)item).onItemUse(containedStack, player, world, x, y, z, par7, par8, par9, par10);
					if (containedStack.stackSize == 0) {
						inventory.setInventorySlotContents(0, null);
					}
					inventory.onInventoryChanged();
					return response;
				}
			}
		}
		return true;
	}

	@ForgeSubscribe
	public void onItemPickUp(EntityItemPickupEvent evt) {

		EntityPlayer player = evt.entityPlayer;
		ItemStack pickedStack = evt.item.getEntityItem();

		final ItemStack compareStack = new ItemStack(this);

		boolean foundMatchingContainer = false;

		if (pickedStack != null && player != null) {

			for (int i = 0; i < player.inventory.getSizeInventory(); i++) {

				ItemStack stack = player.inventory.getStackInSlot(i);

				if (stack != null && stack.isItemEqual(compareStack)) {

					ItemInventory inventory = new ItemInventory(player, 1, i);
					ItemStack containedStack = inventory.getStackInSlot(0);
					if (containedStack != null) {
						boolean isMatching = pickedStack.isItemEqual(containedStack)
								&& ItemStack.areItemStackTagsEqual(pickedStack, containedStack);
						foundMatchingContainer |= isMatching;
						if (isMatching) {
							InventoryUtils.tryInsertStack(inventory, 0, pickedStack, true);
						}
					}
				}
			}
		}

		if (foundMatchingContainer) {
			pickedStack.stackSize = 0;
		}
	}
	
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister register) {
        itemIcon = register.registerIcon("openblocks:devnull");
    }
}
