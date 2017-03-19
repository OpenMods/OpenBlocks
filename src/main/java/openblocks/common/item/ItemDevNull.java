package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import openmods.infobook.BookDocumentation;
import openmods.inventory.ItemInventory;
import openmods.inventory.PlayerItemInventory;
import openmods.inventory.StackEqualityTesterBuilder;
import openmods.inventory.StackEqualityTesterBuilder.IEqualityTester;
import openmods.inventory.legacy.ItemDistribution;

@BookDocumentation
public class ItemDevNull extends Item {

	public static final int STACK_LIMIT = 5;

	public static class DevNullInventory extends PlayerItemInventory {

		private final EntityPlayer player;

		public DevNullInventory(EntityPlayer player, int protectedSlot) {
			super(player, 1, protectedSlot);
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
			if (calculateDepth(stack) > STACK_LIMIT) player.addStat(OpenBlocks.stackAchievement);
		}
	}

	public ItemDevNull() {
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
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote && (!Config.devNullSneakGui || player.isSneaking())) player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.devNull.ordinal(), world, player.inventory.currentItem, 0, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	// TODO 1.10 removed onItemUseFirst, verify if it was needed?

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (hand == EnumHand.OFF_HAND) return EnumActionResult.PASS; // TODO 1.10 verify if it works as expected?

		PlayerItemInventory inventory = new PlayerItemInventory(player, 1);
		ItemStack containedStack = inventory.getStackInSlot(0);
		if (containedStack != null) {
			Item item = containedStack.getItem();
			if (item instanceof ItemBlock) {
				EnumActionResult response = item.onItemUse(containedStack, player, world, pos, hand, facing, hitX, hitY, hitZ);
				if (containedStack.stackSize == 0) {
					inventory.setInventorySlotContents(0, null);
				}
				inventory.markDirty();
				return response;
			}
		}
		return EnumActionResult.PASS; // TODO 1.01 verify this result
	}

	private static final IEqualityTester tester = new StackEqualityTesterBuilder().useItem().useDamage().useNBT().build();

	@SubscribeEvent
	public void onItemPickUp(EntityItemPickupEvent evt) {

		final EntityPlayer player = evt.getEntityPlayer();
		final ItemStack pickedStack = evt.getItem().getEntityItem();

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
}
