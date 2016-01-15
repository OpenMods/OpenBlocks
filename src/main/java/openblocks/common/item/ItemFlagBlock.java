package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openblocks.common.block.BlockFlag;
import openmods.item.ItemOpenBlock;

public class ItemFlagBlock extends ItemOpenBlock {

	public ItemFlagBlock(Block block) {
		super(block);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) return false;

		return super.onItemUse(stack, player, world, pos, side, hitX, hitY, hitZ);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		stack.setItemDamage((stack.getItemDamage() + 1) % BlockFlag.COLORS.length);
		return stack.copy();
	}

}
