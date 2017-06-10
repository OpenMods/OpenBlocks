package openblocks.common.item;

import com.google.common.base.Optional;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.IStencilPattern;
import openblocks.common.StencilPattern;

public class ItemStencil extends Item {

	public ItemStencil() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> list) {
		for (StencilPattern stencil : StencilPattern.values())
			list.add(new ItemStack(item, 1, stencil.ordinal()));
	}

	public static Optional<IStencilPattern> getPattern(ItemStack stack) {
		final int patternId = stack.getMetadata();
		try {
			return Optional.<IStencilPattern> of(StencilPattern.values()[patternId]);
		} catch (ArrayIndexOutOfBoundsException e) {
			return Optional.absent();
		}
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		// TODO use stencil patterns

		return EnumActionResult.PASS;
	}

}
