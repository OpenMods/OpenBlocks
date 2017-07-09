package openblocks.common.item;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
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
import org.apache.commons.lang3.tuple.Pair;

@BookDocumentation
public class ItemDevNull extends Item {

	private static final String BOX_END = "\u255A";

	private static final String BOX_MIDDLE = "\u2551";

	private static final String BOX_START = "\u2554";

	private static final LoadingCache<ItemStack, Pair<ItemStack, Integer>> cache = CacheBuilder.newBuilder().softValues().expireAfterAccess(10, TimeUnit.SECONDS).build(new CacheLoader<ItemStack, Pair<ItemStack, Integer>>() {
		@Override
		public Pair<ItemStack, Integer> load(ItemStack container) throws Exception {
			ItemStack stack = container;
			int depth = 0;

			while (depth < STACK_LIMIT) {
				if (stack == null || !(stack.getItem() instanceof ItemDevNull)) return Pair.of(stack, depth);
				stack = new ItemInventory(stack, 1).getStackInSlot(0);
				depth++;
			}

			return Pair.of(null, depth);
		}
	});

	public static class NestedItemColorHandler implements IItemColor {
		private static final int NO_COLOR = 0xFFFFFFFF;

		private final ItemColors itemColors;

		public NestedItemColorHandler(ItemColors itemColors) {
			this.itemColors = itemColors;
		}

		@Override
		public int getColorFromItemstack(ItemStack stack, int tintIndex) {
			if (tintIndex < NESTED_ITEM_TINT_DELTA)
				return NO_COLOR;

			final Pair<ItemStack, Integer> contents = getContents(stack);
			if (contents.getRight() > STACK_LIMIT) return NO_COLOR;

			final ItemStack nestedItem = contents.getLeft();
			if (nestedItem == null) return NO_COLOR;
			return itemColors.getColorFromItemstack(nestedItem, tintIndex - NESTED_ITEM_TINT_DELTA);
		}

	}

	public static final int NESTED_ITEM_TINT_DELTA = 1;

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
				checkStack(containerStack);
			}
		}

		@Override
		public boolean isItemValidForSlot(int i, ItemStack stack) {
			return getContents(stack).getRight() < STACK_LIMIT;
		}

		private void checkStack(ItemStack stack) {
			if (getContents(stack).getRight() >= STACK_LIMIT)
				player.addStat(OpenBlocks.stackAchievement);
		}
	}

	public ItemDevNull() {
		setMaxStackSize(1);
	}

	public static Pair<ItemStack, Integer> getContents(ItemStack container) {
		if (container == null) return Pair.of(null, 0);
		return cache.getUnchecked(container);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote && (!Config.devNullSneakGui || player.isSneaking())) player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.devNull.ordinal(), world, player.inventory.currentItem, 0, 0);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	// TODO 1.10 removed onItemUseFirst, verify if it was needed?

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (hand == EnumHand.OFF_HAND) return EnumActionResult.PASS;

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
		return EnumActionResult.PASS;
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

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		final Pair<ItemStack, Integer> contents = getContents(stack);
		final ItemStack containedStack = contents.getLeft();
		if (containedStack != null) {
			final List<String> innerTooltip = containedStack.getTooltip(playerIn, advanced);
			if (innerTooltip.isEmpty()) {
				tooltip.add(containedStack.stackSize + " * " + containedStack.getDisplayName());
			} else {
				innerTooltip.set(0, containedStack.stackSize + " * " + innerTooltip.get(0));
				box(tooltip, innerTooltip);
			}
		} else if (contents.getRight() >= STACK_LIMIT) {
			tooltip.add(BOX_START);
			tooltip.add(BOX_MIDDLE + TextFormatting.OBFUSCATED + "WHOOPS" + TextFormatting.RESET);
			tooltip.add(BOX_END);
		}
	}

	private static void box(List<String> output, List<String> input) {
		output.add(BOX_START);
		for (String s : input)
			output.add(BOX_MIDDLE + s);
		output.add(BOX_END);
	}
}
