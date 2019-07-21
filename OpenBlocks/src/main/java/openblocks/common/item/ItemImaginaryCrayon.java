package openblocks.common.item;

import com.google.common.base.MoreObjects;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.block.BlockImaginary;
import openblocks.common.tileentity.TileEntityImaginaryCrayon;
import openmods.colors.ColorMeta;
import openmods.utils.ItemUtils;
import openmods.utils.TranslationUtils;

public class ItemImaginaryCrayon extends ItemImaginary {

	@SideOnly(Side.CLIENT)
	public static class ColorHandler implements IItemColor {
		@Override
		public int colorMultiplier(@Nonnull ItemStack stack, int tintIndex) {
			if (tintIndex == 1) {
				return getColor(stack);
			}

			return 0xFFFFFFFF;
		}
	}

	private static final String TAG_COLOR = "Color";

	public static int getColor(CompoundNBT tag) {
		return tag.getInteger(TAG_COLOR);
	}

	public static int getColor(@Nonnull ItemStack stack) {
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		return getColor(tag);
	}

	public ItemImaginaryCrayon(Block block) {
		super(block);
	}

	@Nonnull
	public static ItemStack setupValues(@Nonnull ItemStack result, int color, BlockImaginary.Shape shape, boolean isInverted, float uses) {
		return setupValues(result, color, MoreObjects.firstNonNull(SHAPE_TO_MODE.get(shape, isInverted), PlacementMode.BLOCK), uses);
	}

	@Nonnull
	public static ItemStack setupValues(@Nonnull ItemStack result, int color, PlacementMode mode, float uses) {
		CompoundNBT tag = ItemUtils.getItemTag(result);
		tag.setInteger(TAG_COLOR, color);
		tag.setInteger(TAG_MODE, mode.ordinal());
		tag.setFloat(TAG_USES, uses);
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, List<String> result, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, result, flagIn);
		result.add(TranslationUtils.translateToLocalFormatted("openblocks.misc.color", getColor(stack)));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
		if (isInCreativeTab(tab)) {
			for (ColorMeta color : ColorMeta.getAllColors())
				result.add(setupValues(new ItemStack(this), color.rgb, PlacementMode.BLOCK, ItemImaginary.DEFAULT_USE_COUNT));
		}
	}

	@Override
	protected void configureBlockEntity(TileEntity tileEntity, PlacementMode mode, CompoundNBT tag) {
		if (tileEntity instanceof TileEntityImaginaryCrayon) {
			((TileEntityImaginaryCrayon)tileEntity).setup(mode.isInverted, mode.shape, tag.getInteger(TAG_COLOR));
		}
	}
}
