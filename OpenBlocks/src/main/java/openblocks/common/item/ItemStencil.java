package openblocks.common.item;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openblocks.common.CanvasReplaceBlacklist;
import openblocks.common.StencilPattern;
import openblocks.common.block.BlockCanvas;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.utils.ItemUtils;

public class ItemStencil extends Item {

	private static final String TAG_PATTERN = "Pattern";

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (isInCreativeTab(tab)) {
			for (StencilPattern stencil : StencilPattern.values())
				list.add(createItemStack(this, stencil));
		}
	}

	public static ItemStack createItemStack(Item item, StencilPattern stencil) {
		final ItemStack result = new ItemStack(item);
		ItemUtils.getItemTag(result).setString(TAG_PATTERN, stencil.id.toString());
		return result;
	}

	public static Optional<StencilPattern> getPattern(@Nonnull ItemStack stack) {
		final NBTTagCompound tag = stack.getTagCompound();
		if (tag != null) {
			final String id = tag.getString(TAG_PATTERN);
			return Optional.ofNullable(StencilPattern.ID_TO_PATTERN.get(new ResourceLocation(id)));
		}

		return Optional.empty();
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		final ItemStack stack = player.getHeldItem(hand);
		final Optional<StencilPattern> pattern = getPattern(stack);

		if (!pattern.isPresent()) { return EnumActionResult.FAIL; }

		if (CanvasReplaceBlacklist.instance.isAllowedToReplace(world, pos)) {
			BlockCanvas.replaceBlock(world, pos);
		}

		final TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;

			if (canvas.useStencil(facing, pattern.get())) {
				stack.shrink(1);
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

}
