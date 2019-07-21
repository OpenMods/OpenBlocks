package openblocks.common.item;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.OpenBlocksGuiHandler;
import openblocks.advancements.Criterions;
import openmods.fixers.ItemInventoryWalker;
import openmods.infobook.BookDocumentation;
import openmods.inventory.ItemInventory;
import openmods.inventory.PlayerItemInventory;
import openmods.inventory.StackEqualityTesterBuilder;
import openmods.inventory.StackEqualityTesterBuilder.IEqualityTester;
import org.apache.commons.lang3.tuple.Pair;

@BookDocumentation
public class ItemDevNull extends Item {

	private static final String BOX_END = "\u255A";

	private static final String BOX_MIDDLE = "\u2551";

	private static final String BOX_START = "\u2554";

	private static final LoadingCache<ItemStack, Pair<ItemStack, Integer>> cache = CacheBuilder.newBuilder().softValues().expireAfterAccess(10, TimeUnit.SECONDS).build(new CacheLoader<ItemStack, Pair<ItemStack, Integer>>() {
		@Override
		public Pair<ItemStack, Integer> load(@Nonnull ItemStack container) {
			ItemStack stack = container;
			int depth = 0;

			while (depth < STACK_LIMIT) {
				if (stack.isEmpty() || !(stack.getItem() instanceof ItemDevNull)) return Pair.of(stack, depth);
				stack = new ItemInventory(stack, 1).getStackInSlot(0);
				depth++;
			}

			return Pair.of(ItemStack.EMPTY, depth);
		}
	});

	public static class NestedItemColorHandler implements IItemColor {
		private static final int NO_COLOR = 0xFFFFFFFF;

		private final ItemColors itemColors;

		public NestedItemColorHandler(ItemColors itemColors) {
			this.itemColors = itemColors;
		}

		@Override
		public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
			if (tintIndex < NESTED_ITEM_TINT_DELTA)
				return NO_COLOR;

			final Pair<ItemStack, Integer> contents = getContents(stack);
			if (contents.getRight() > STACK_LIMIT) return NO_COLOR;

			final ItemStack nestedItem = contents.getLeft();
			if (nestedItem.isEmpty()) return NO_COLOR;
			return itemColors.colorMultiplier(nestedItem, tintIndex - NESTED_ITEM_TINT_DELTA);
		}

	}

	public static final int NESTED_ITEM_TINT_DELTA = 1;

	public static final int STACK_LIMIT = 5;

	public static class DevNullInventory extends PlayerItemInventory {

		private final PlayerEntity player;

		public DevNullInventory(PlayerEntity player, int protectedSlot) {
			super(player, 1, protectedSlot);
			this.player = player;
		}

		@Override
		public void onInventoryChanged(int slotNumber) {
			super.onInventoryChanged(slotNumber);
			if (!player.world.isRemote && slotNumber == 0) {
				checkStack(containerStack);
			}
		}

		@Override
		public boolean isItemValidForSlot(int i, @Nonnull ItemStack stack) {
			return getContents(stack).getRight() < STACK_LIMIT;
		}

		private void checkStack(@Nonnull ItemStack stack) {
			if (player instanceof ServerPlayerEntity) {
				final int depth = getContents(stack).getRight();
				Criterions.devNullStack.trigger((ServerPlayerEntity)player, depth);
			}
		}
	}

	public ItemDevNull() {
		setMaxStackSize(1);
	}

	public static Pair<ItemStack, Integer> getContents(@Nonnull ItemStack container) {
		if (container.isEmpty()) return Pair.of(ItemStack.EMPTY, 0);
		return cache.getUnchecked(container);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);

		if (hand != Hand.MAIN_HAND) return ActionResult.newResult(ActionResultType.PASS, stack);

		if (!world.isRemote && (!Config.devNullSneakGui || player.isSneaking())) {
			player.openGui(OpenBlocks.instance, OpenBlocksGuiHandler.GuiId.devNull.ordinal(), world, player.inventory.currentItem, 0, 0);
		}

		return ActionResult.newResult(ActionResultType.SUCCESS, stack);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		final ItemStack containerStack = player.getHeldItem(hand);
		final ItemInventory inventory = new ItemInventory(containerStack, 1);
		ItemStack containedStack = inventory.getStackInSlot(0);
		if (!containedStack.isEmpty() && containedStack.getItem() instanceof BlockItem) {
			player.setHeldItem(hand, containedStack);
			ActionResultType response = containedStack.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
			containedStack = player.getHeldItem(hand);
			inventory.setInventorySlotContents(0, containedStack);
			player.setHeldItem(hand, containerStack);
			return response;
		}
		return ActionResultType.PASS;
	}

	private static final IEqualityTester tester = new StackEqualityTesterBuilder().useItem().useDamage().useNBT().build();

	@SubscribeEvent
	public void onItemPickUp(EntityItemPickupEvent evt) {

		final PlayerEntity player = evt.getEntityPlayer();
		final ItemStack pickedStack = evt.getItem().getItem();

		if (pickedStack.isEmpty() || player == null) return;

		boolean foundMatchingContainer = false;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			final ItemStack stack = player.inventory.getStackInSlot(i);

			if (stack.getItem() == this) {
				final ItemInventory inventory = new ItemInventory(stack, 1);
				final ItemStack containedStack = inventory.getStackInSlot(0);
				if (!containedStack.isEmpty()) {
					final boolean isMatching = tester.isEqual(pickedStack, containedStack);
					if (isMatching) {
						final int totalItems = containedStack.getCount() + pickedStack.getCount();
						final int containedItems = Math.min(containedStack.getMaxStackSize(), totalItems);
						containedStack.setCount(containedItems);
						pickedStack.setCount(Math.max(totalItems - containedItems, 0));
						inventory.setInventorySlotContents(0, containedStack);
						if (pickedStack.getCount() == 0) return;
						foundMatchingContainer = true;
					}
				}
			}
		}

		if (foundMatchingContainer) pickedStack.setCount(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		final Pair<ItemStack, Integer> contents = getContents(stack);
		final ItemStack containedStack = contents.getLeft();
		if (!containedStack.isEmpty()) {
			final List<String> innerTooltip = containedStack.getTooltip(null, flagIn);
			if (innerTooltip.isEmpty()) {
				tooltip.add(containedStack.getCount() + " * " + containedStack.getDisplayName());
			} else {
				innerTooltip.set(0, containedStack.getCount() + " * " + innerTooltip.get(0));
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

	public static void registerFixes(DataFixer dataFixer) {
		if (OpenBlocks.Items.devNull != null)
			dataFixer.registerWalker(FixTypes.ITEM_INSTANCE, new ItemInventoryWalker(OpenBlocks.Items.devNull));
	}
}
