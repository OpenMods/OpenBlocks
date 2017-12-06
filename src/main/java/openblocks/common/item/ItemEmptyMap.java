package openblocks.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks.Items;
import openblocks.common.MapDataManager;
import openmods.utils.ItemUtils;

public class ItemEmptyMap extends Item {

	public static final String TAG_SCALE = "Scale";
	public static final int MAX_SCALE = 4;

	public ItemEmptyMap() {}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> result, ITooltipFlag flag) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		result.add(String.format("Scale: 1:%d", 1 << tag.getByte(TAG_SCALE)));
	}

	@Nonnull
	public ItemStack createMap(int scale) {
		ItemStack result = new ItemStack(this);
		NBTTagCompound tag = ItemUtils.getItemTag(result);
		tag.setByte(TAG_SCALE, (byte)scale);
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
		if (isInCreativeTab(tab)) {
			for (int scale = 0; scale < ItemEmptyMap.MAX_SCALE; scale++)
				result.add(createMap(scale));
		}
	}

	@Nonnull
	public static ItemStack upgradeToMap(World world, ItemStack emptyMap) {
		NBTTagCompound tag = ItemUtils.getItemTag(emptyMap);
		byte scale = tag.getByte(TAG_SCALE);
		int newMapId = MapDataManager.createNewMap(world, scale);

		return new ItemStack(Items.heightMap, 1, newMapId);
	}
}
