package openblocks.common.item;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.common.CanvasReplaceBlacklist;
import openblocks.common.IStencilPattern;
import openblocks.common.StencilPattern;
import openblocks.common.block.BlockCanvas;
import openblocks.common.tileentity.TileEntityCanvas;

public class ItemStencil extends Item {

	public ItemStencil() {
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) {
			for (StencilPattern stencil : StencilPattern.values())
				list.add(createItemStack(this, stencil));
		}
	}

	public static ItemStack createItemStack(Item item, StencilPattern stencil) {
		return new ItemStack(item, 1, stencil.ordinal());
	}

	public static Optional<IStencilPattern> getPattern(@Nonnull ItemStack stack) {
		final int patternId = stack.getMetadata();
		try {
			return Optional.<IStencilPattern> of(StencilPattern.values()[patternId]);
		} catch (ArrayIndexOutOfBoundsException e) {
			return Optional.empty();
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

		if (CanvasReplaceBlacklist.instance.isAllowedToReplace(world, pos)) {
			BlockCanvas.replaceBlock(world, pos);
		}

		final TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityCanvas) {
			final ItemStack stack = player.getHeldItem(hand);
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			int stencilId = stack.getItemDamage();
			StencilPattern stencil;
			try {
				stencil = StencilPattern.values()[stencilId];
			} catch (ArrayIndexOutOfBoundsException e) {
				return EnumActionResult.FAIL;
			}

			if (canvas.useStencil(facing, stencil)) {
				stack.shrink(1);
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

}
