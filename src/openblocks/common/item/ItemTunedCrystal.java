package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;
import openmods.utils.ItemUtils;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTunedCrystal extends Item {

	private static final String TAG_HIDDEN = "Hidden";
	public static final String TAG_URL = "URL";
	private List<ItemStack> predefinedStations;

	public ItemTunedCrystal() {
		super(Config.itemTunedCrystalId);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	public List<ItemStack> getPredefinedStations() {
		if (predefinedStations == null) {
			ImmutableList.Builder<ItemStack> stations = ImmutableList.builder();
			for (String stationDesc : Config.radioStations) {
				if (stationDesc.startsWith("\"") && stationDesc.endsWith("\"")) stationDesc = stationDesc.substring(1, stationDesc.length() - 1);
				stationDesc = StringUtils.strip(stationDesc);

				List<String> fields = ImmutableList.copyOf(Splitter.on(';').split(stationDesc));
				Preconditions.checkState(fields.size() > 0 && fields.size() <= 3, "Invalid radio station descripion: %s", stationDesc);

				String url = fields.get(0);
				String name = (fields.size() > 1)? fields.get(1) : "";
				Iterable<String> attributes = (fields.size() > 2)? Splitter.on(",").split(fields.get(2)) : ImmutableList.<String> of();

				stations.add(createStack(url, name, attributes));
			}
			predefinedStations = stations.build();
		}
		return predefinedStations;
	}

	public ItemStack createStack(String url, String name, Iterable<String> attributes) {
		ColorMeta color = ColorUtils.vanillaToColor(ColorUtils.WHITE);
		boolean hidden = false;

		for (String attribute : attributes) {
			attribute = StringUtils.strip(attribute);
			ColorMeta possibleColor = ColorUtils.nameToColor(attribute);
			if (possibleColor != null) {
				color = possibleColor;
			} else {
				attribute = attribute.toLowerCase();

				if ("hidden".equals(attribute)) hidden = true;
				else throw new IllegalArgumentException("Unknown tuned crystal attribute: " + attribute);
			}
		}

		ItemStack result = new ItemStack(this, 1, color.vanillaId);
		NBTTagCompound tag = ItemUtils.getItemTag(result);
		tag.setString(TAG_URL, url);
		if (!Strings.isNullOrEmpty(name)) result.setItemName(name);
		if (hidden) tag.setBoolean(TAG_HIDDEN, true);
		return result;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		int damage = stack.getItemDamage();
		ColorMeta color = ColorUtils.vanillaToColor(damage);
		return color != null? color.rgb : 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addInformation(ItemStack stack, EntityPlayer player, List result, boolean extended) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		boolean hidden = tag.getBoolean(TAG_HIDDEN);
		if (!hidden || player.capabilities.isCreativeMode) {
			String url = tag.getString(TAG_URL);
			result.add(StatCollector.translateToLocalFormatted("openblocks.misc.url", url));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegistry) {
		itemIcon = iconRegistry.registerIcon("openblocks:tuned_crystal");
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int itemId, CreativeTabs tab, List result) {
		result.addAll(getPredefinedStations());
	}

}
