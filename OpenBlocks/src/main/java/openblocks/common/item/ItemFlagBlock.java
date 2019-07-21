package openblocks.common.item;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.NavigableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openmods.item.ItemOpenBlock;

public class ItemFlagBlock extends ItemOpenBlock {

	private static final NavigableSet<Block> BLOCKS = Sets.newTreeSet(Ordering.natural().onResultOf(Block::getRegistryName));

	public ItemFlagBlock(Block block) {
		super(block);
		BLOCKS.add(block);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) return ActionResultType.PASS;

		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		if (hand == Hand.MAIN_HAND) {
			return ActionResult.newResult(ActionResultType.SUCCESS, new ItemStack(nextBlock(), stack.getCount()));
		} else {
			return ActionResult.newResult(ActionResultType.PASS, stack);
		}

	}

	private Block nextBlock() {
		final Block next = BLOCKS.higher(block);
		return next != null? next : BLOCKS.first();
	}

}
