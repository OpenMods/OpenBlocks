package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.common.block.BlockFlag;
import openmods.item.ItemOpenBlock;

public class ItemFlagBlock extends ItemOpenBlock {

	public ItemFlagBlock(Block block) {
		super(block);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) return EnumActionResult.PASS; // TODO 1.10 pass or fail? Should allow offhand use...

		return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		stack.setItemDamage((stack.getItemDamage() + 1) % BlockFlag.COLORS.length);
		return ActionResult.newResult(EnumActionResult.SUCCESS, stack); // TODO 1.10 verify
	}

}
