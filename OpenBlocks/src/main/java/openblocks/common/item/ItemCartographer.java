package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.entity.EntityAssistant;
import openblocks.common.entity.EntityCartographer;
import openmods.fixers.NestedItemStackWalker;

public class ItemCartographer extends Item {

	public ItemCartographer() {
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);

		if (hand != EnumHand.MAIN_HAND) return ActionResult.newResult(EnumActionResult.PASS, stack);

		if (!player.capabilities.isCreativeMode) stack.shrink(1);

		if (!world.isRemote) {
			EntityAssistant cartographer = new EntityCartographer(world, player, stack);
			world.spawnEntity(cartographer);
		}

		return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
	}

	public static void registerFixes(DataFixer dataFixer) {
		if (OpenBlocks.Items.cartographer != null)
			dataFixer.registerWalker(FixTypes.ITEM_INSTANCE, new NestedItemStackWalker(OpenBlocks.Items.cartographer, EntityCartographer.TAG_MAP_ITEM));
	}
}
