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
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import openmods.infobook.BookDocumentation;
import openmods.inventory.*;
import openmods.inventory.StackEqualityTesterBuilder.IEqualityTester;
import openmods.inventory.legacy.ItemDistribution;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@BookDocumentation
public class ItemDevNull extends Item {

	public static class Icons {
		public static IIcon iconFull;
		public static IIcon iconTransparent;
		public static IIcon iconOverload;
	}

	public static final int STACK_LIMIT = 5;

	public static class DevNullInventory extends PlayerItemInventory {

		private final EntityPlayer player;

		public DevNullInventory(EntityPlayer player) {
			super(player, 1);
			this.player = player;
		}

		@Override
		public void onInventoryChanged(int slotNumber) {
			super.onInventoryChanged(slotNumber);
			if (!player.worldObj.isRemote && slotNumber == 0) {
				ItemStack stack = getStackInSlot(0);
				checkStack(stack);
			}
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack stack) {
			return calculateDepth(stack) < STACK_LIMIT + 2;
		}

		private void checkStack(ItemStack stack) {
			if (calculateDepth(stack) > STACK_LIMIT) player.triggerAchievement(OpenBlocks.stackAchievement);
		}
	}

	public ItemDevNull() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setMaxStackSize(1);
	}

	private static int calculateDepth(ItemStack stack) {
		return calculateDepth(stack, 1);
	}

	private static int calculateDepth(ItemStack stack, int count) {
		if (stack == null) return count;
		if (stack.getItem() instanceof ItemDevNull) {
			final ItemStack innerStack = new ItemInventory(stack, 1).getStackInSlot(0);
			return calculateDepth(innerStack, count + 1);
		}

		return count;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote && (!Config.devNullSneakGui || player.isSneaking())) player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.devNull.ordinal(), world, player.inventory.currentItem, 0, 0);
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

	private static final IEqualityTester tester = new StackEqualityTesterBuilder().useItem().useDamage().useNBT().build();

	@SubscribeEvent
	public void onItemPickUp(EntityItemPickupEvent evt) {

		final EntityPlayer player = evt.entityPlayer;
		final ItemStack pickedStack = evt.item.getEntityItem();

		if (pickedStack == null || player == null) return;

		boolean foundMatchingContainer = false;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			final ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack != null && stack.getItem() == this) {
				final ItemInventory inventory = new ItemInventory(stack, 1);
				final ItemStack containedStack = inventory.getStackInSlot(0);
				if (containedStack != null) {
					final boolean isMatching = tester.isEqual(pickedStack, containedStack);
					if (isMatching) {
						ItemDistribution.tryInsertStack(inventory, 0, pickedStack, true);
						if (pickedStack.stackSize == 0) return;
						foundMatchingContainer = true;
					}
				}
			}
		}

		if (foundMatchingContainer) pickedStack.stackSize = 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		super.registerIcons(register);
		Icons.iconTransparent = register.registerIcon("openblocks:devnull");
		Icons.iconFull = register.registerIcon("openblocks:devfull");
		Icons.iconOverload = register.registerIcon("openblocks:devzerooverzero");
	}
}
