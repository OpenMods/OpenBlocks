package openblocks.common.item;

import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
		return createItemStack(item, 1, stencil);
	}

	public static ItemStack createItemStack(Item item, int size, StencilPattern stencil) {
		final ItemStack result = new ItemStack(item, size);
		ItemUtils.getItemTag(result).setString(TAG_PATTERN, stencil.id.toString());
		return result;
	}

	public static Optional<StencilPattern> getPattern(@Nonnull ItemStack stack) {
		final CompoundNBT tag = stack.getTagCompound();
		if (tag != null) {
			final String id = tag.getString(TAG_PATTERN);
			return Optional.ofNullable(StencilPattern.ID_TO_PATTERN.get(new ResourceLocation(id)));
		}

		return Optional.empty();
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
		final ItemStack stack = player.getHeldItem(hand);
		final Optional<StencilPattern> pattern = getPattern(stack);

		if (!pattern.isPresent()) { return ActionResultType.FAIL; }

		if (CanvasReplaceBlacklist.instance.isAllowedToReplace(world, pos)) {
			BlockCanvas.replaceBlock(world, pos);
		}

		final TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;

			if (canvas.useStencil(facing, pattern.get())) {
				stack.shrink(1);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

}
