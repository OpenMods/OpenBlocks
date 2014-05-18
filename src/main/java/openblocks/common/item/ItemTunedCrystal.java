package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.client.radio.RadioManager;
import openblocks.client.radio.RadioManager.RadioStation;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;
import openmods.utils.ItemUtils;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTunedCrystal extends Item {

	private static final String TAG_HIDDEN = "Hidden";
	private static final String TAG_URL = "URL";

	private Icon crystal;

	public ItemTunedCrystal() {
		super(Config.itemTunedCrystalId);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
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
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (renderPass == 1) return 0xFFFFFF;
		int damage = stack.getItemDamage();
		ColorMeta color = ColorUtils.vanillaToColor(damage);
		return color != null? color.rgb : 0xFFFFFF;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 2;
	}

	@Override
	public Icon getIcon(ItemStack stack, int pass) {
		return pass == 1? itemIcon : crystal;
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
		itemIcon = iconRegistry.registerIcon("openblocks:tuned_crystal_2");
		crystal = iconRegistry.registerIcon("openblocks:tuned_crystal_1");
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubItems(int itemId, CreativeTabs tab, List result) {
		for (RadioStation station : RadioManager.instance.getRadioStations())
			result.add(station.getStack());
	}

	public static String getUrl(ItemStack stack) {
		NBTTagCompound tag = ItemUtils.getItemTag(stack);
		if (tag == null) return "";
		String url = tag.getString(TAG_URL);
		if (Strings.isNullOrEmpty(url)) return "";
		String updatedUrl = RadioManager.instance.updateURL(url);
		if (!updatedUrl.equals(url)) tag.setString(TAG_URL, updatedUrl);
		return updatedUrl;
	}
}
