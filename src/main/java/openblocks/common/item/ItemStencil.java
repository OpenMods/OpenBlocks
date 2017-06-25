package openblocks.common.item;

import com.google.common.base.Optional;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.IStencilPattern;
import openblocks.common.StencilPattern;
import openblocks.common.block.BlockCanvas;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.utils.render.PaintUtils;

public class ItemStencil extends Item {

	public ItemStencil() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List<ItemStack> list) {
		for (StencilPattern stencil : StencilPattern.values())
			list.add(createItemStack(stencil));
	}

	public ItemStack createItemStack(StencilPattern stencil) {
		return new ItemStack(this, 1, stencil.ordinal());
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

		if (PaintUtils.instance.isAllowedToReplace(world, pos)) {
			BlockCanvas.replaceBlock(world, pos);
		}

		final TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			int stencilId = stack.getItemDamage();
			StencilPattern stencil;
			try {
				stencil = StencilPattern.values()[stencilId];
			} catch (ArrayIndexOutOfBoundsException e) {
				return EnumActionResult.FAIL;
			}

			if (canvas.useStencil(facing, stencil)) {
				stack.stackSize--;
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

}
