package openblocks.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openmods.colors.ColorMeta;
import openmods.item.ItemOpenBlock;
import openmods.utils.ItemUtils;

public class ItemPaintCan extends ItemOpenBlock {

	public static final String TAG_AMOUNT = "amount";
	public static final String TAG_COLOR = "color";
	public static final int FULL_CAN_SIZE = 30;

	private static final int COLOR_WHITE = 0xFFFFFF;

	@SideOnly(Side.CLIENT)
	public static class ItemColorHandler implements IItemColor {

		@Override
		public int getColorFromItemstack(@Nonnull ItemStack stack, int tintIndex) {
			return tintIndex == 1? getColorFromStack(stack) : COLOR_WHITE;
		}
	}

	public ItemPaintCan(Block block) {
		super(block);
		setMaxDamage(FULL_CAN_SIZE);
		setMaxStackSize(1);
	}

	public static int getColorFromStack(@Nonnull ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null? tag.getInteger(TAG_COLOR) : COLOR_WHITE;
	}

	public static int getAmountFromStack(@Nonnull ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound();
		return tag != null? tag.getInteger(TAG_AMOUNT) : 0;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> result) {
		for (ColorMeta meta : ColorMeta.getAllColors())
			result.add(createStack(meta.rgb, FULL_CAN_SIZE));
	}

	@Nonnull
	public static ItemStack createStack(int color, int amount) {
		ItemStack stack = new ItemStack(OpenBlocks.Blocks.paintCan);
		setColorAndAmount(stack, color, amount);
		return stack;
	}

	public static void setColorAndAmount(ItemStack stack, final int color, final int amount) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		tag.setInteger(TAG_COLOR, color);
		tag.setInteger(TAG_AMOUNT, amount);
	}

	@Override
	public void addInformation(@Nonnull ItemStack stack, EntityPlayer player, List<String> list, boolean extended) {
		list.add(String.format("#%06X", getColorFromStack(stack)));
	}

	@Override
	public int getDamage(@Nonnull ItemStack stack) {
		return FULL_CAN_SIZE - getAmountFromStack(stack);
	}

	@Override
	public double getDurabilityForDisplay(@Nonnull ItemStack stack) {
		return 1 - (double)getAmountFromStack(stack) / FULL_CAN_SIZE;
	}

	@Override
	public boolean isDamaged(@Nonnull ItemStack stack) {
		return stack.hasTagCompound() && getAmountFromStack(stack) < FULL_CAN_SIZE;
	}
}
