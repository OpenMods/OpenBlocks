package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import openmods.infobook.BookDocumentation;
import openmods.inventory.ItemInventory;
import openmods.inventory.PlayerItemInventory;
import openmods.inventory.legacy.ItemDistribution;
import openmods.utils.InventoryUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
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
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int sideId, float hitX, float hitY, float hitZ) {
		IInventory inventory = new ItemInventory(stack, 1);
		ItemStack containedStack = inventory.getStackInSlot(0);
		if (containedStack != null) {
			Item item = containedStack.getItem();
			if (item instanceof ItemBlock) {
				Block placedBlock = ((ItemBlock)item).field_150939_a;
				// logic based on ItemBlock.func_150936_a, so don't blame me for hardcoding
				Block clickedBlock = world.getBlock(x, y, z);

				if (clickedBlock == Blocks.snow_layer) sideId = 1; // UP
				else if (!clickedBlock.isReplaceable(world, x, y, z)) {
					ForgeDirection side = ForgeDirection.getOrientation(sideId);
					x += side.offsetX;
					y += side.offsetY;
					z += side.offsetZ;
				}

				return !world.canPlaceEntityOnSide(placedBlock, x, y, z, false, sideId, null, stack);
			}
		}

		return false;
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
						ItemDistribution.tryInsertStack(inventory, 0, pickedStack, true);
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
