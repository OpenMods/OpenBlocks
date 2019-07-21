package openblocks.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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
		CompoundNBT tag = ItemUtils.getItemTag(stack);
		result.add(String.format("Scale: 1:%d", 1 << tag.getByte(TAG_SCALE)));
	}

	@Nonnull
	public static ItemStack createMap(Item item, int scale) {
		ItemStack result = new ItemStack(item);
		CompoundNBT tag = ItemUtils.getItemTag(result);
		tag.setByte(TAG_SCALE, (byte)scale);
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
		if (isInCreativeTab(tab)) {
			for (int scale = 0; scale < ItemEmptyMap.MAX_SCALE; scale++)
				result.add(createMap(this, scale));
		}
	}

	@Nonnull
	public static ItemStack upgradeToMap(World world, ItemStack emptyMap) {
		if (Items.heightMap == null) return emptyMap;

		CompoundNBT tag = ItemUtils.getItemTag(emptyMap);
		byte scale = tag.getByte(TAG_SCALE);
		int newMapId = MapDataManager.createNewMap(world, scale);

		final ItemStack result = new ItemStack(Items.heightMap);
		ItemUtils.getItemTag(result).setInteger(ItemHeightMap.TAG_MAP_ID, newMapId);
		return result;
	}
}
