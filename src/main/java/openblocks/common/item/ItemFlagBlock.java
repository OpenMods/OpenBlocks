package openblocks.common.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.colors.ColorMeta;
import openmods.item.ItemOpenBlock;

public class ItemFlagBlock extends ItemOpenBlock {

	@SideOnly(Side.CLIENT)
	public static class ItemColorHandler implements IItemColor {

		private static final int COLOR_WHITE = 0xFFFFFFFF;

		@Override
		public int colorMultiplier(ItemStack stack, int tintIndex) {
			return tintIndex == 0? ColorMeta.fromBlockMeta(stack.getMetadata()).rgb : COLOR_WHITE;
		}
	}

	public ItemFlagBlock(Block block) {
		super(block);
		setHasSubtypes(true);
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
			stack.setItemDamage((stack.getItemDamage() + 1) % ColorMeta.values().length);
			return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
		} else {
			return ActionResult.newResult(EnumActionResult.PASS, stack);
		}

	}

}
