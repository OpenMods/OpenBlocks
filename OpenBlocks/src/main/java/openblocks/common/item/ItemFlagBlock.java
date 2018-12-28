package openblocks.common.item;

import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import java.util.NavigableSet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) return EnumActionResult.PASS;

		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		final ItemStack stack = player.getHeldItem(hand);
		if (hand == EnumHand.MAIN_HAND) {
			return ActionResult.newResult(EnumActionResult.SUCCESS, new ItemStack(nextBlock(), stack.getCount()));
		} else {
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		}

	}

	private Block nextBlock() {
		final Block next = BLOCKS.higher(block);
		return next != null? next : BLOCKS.first();
	}

}
