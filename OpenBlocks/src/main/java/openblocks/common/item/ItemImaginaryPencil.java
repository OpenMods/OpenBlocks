package openblocks.common.item;

import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockImaginary;
import openblocks.common.tileentity.TileEntityImaginaryPencil;
import openmods.utils.ItemUtils;

public class ItemImaginaryPencil extends ItemImaginary {

	public ItemImaginaryPencil(Block block) {
		super(block);
	}

	@Nonnull
	public static ItemStack setupValues(@Nonnull ItemStack result, BlockImaginary.Shape shape, boolean isInverted, float uses) {
		return setupValues(result, MoreObjects.firstNonNull(SHAPE_TO_MODE.get(shape, isInverted), PlacementMode.BLOCK), uses);
	}

	@Nonnull
	public static ItemStack setupValues(@Nonnull ItemStack result, PlacementMode mode, float uses) {
		CompoundNBT tag = ItemUtils.getItemTag(result);
		tag.setInteger(TAG_MODE, mode.ordinal());
		tag.setFloat(TAG_USES, uses);
		return result;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
		if (isInCreativeTab(tab)) {
			result.add(ItemImaginaryPencil.setupValues(new ItemStack(OpenBlocks.Blocks.imaginaryPencil), PlacementMode.BLOCK, ItemImaginary.DEFAULT_USE_COUNT));
		}
	}

	@Override
	protected void configureBlockEntity(TileEntity tileEntity, ItemImaginary.PlacementMode mode, CompoundNBT tag) {
		if (tileEntity instanceof TileEntityImaginaryPencil) {
			((TileEntityImaginaryPencil)tileEntity).setup(mode.isInverted, mode.shape);
		}
	}
}
