package openblocks.common.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Items;
import openblocks.common.MapDataManager;
import openmods.utils.ItemUtils;

public class ItemEmptyMap extends Item {

	public static final String TAG_SCALE = "Scale";
	public static final int MAX_SCALE = 4;

	public ItemEmptyMap() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack item, EntityPlayer player, List result, boolean extended) {
		NBTTagCompound tag = ItemUtils.getItemTag(item);
		result.add(String.format("Scale: 1:%d", 1 << tag.getByte(TAG_SCALE)));
	}

	public ItemStack createMap(int scale) {
		ItemStack result = new ItemStack(this);
		NBTTagCompound tag = ItemUtils.getItemTag(result);
		tag.setByte(TAG_SCALE, (byte)scale);
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs tab, List result) {
		for (int scale = 0; scale < ItemEmptyMap.MAX_SCALE; scale++)
			result.add(OpenBlocks.Items.emptyMap.createMap(scale));
	}

	public static ItemStack upgradeToMap(World world, ItemStack emptyMap) {
		NBTTagCompound tag = ItemUtils.getItemTag(emptyMap);
		byte scale = tag.getByte(TAG_SCALE);
		int newMapId = MapDataManager.createNewMap(world, scale);

		return new ItemStack(Items.heightMap, 1, newMapId);
	}
}
