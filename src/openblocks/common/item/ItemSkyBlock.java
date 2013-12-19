package openblocks.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import openmods.utils.render.RenderUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSkyBlock extends ItemBlock {

	public ItemSkyBlock(int id) {
		super(id);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(int par1, CreativeTabs tab, List result) {
		result.add(new ItemStack(this, 1, 0));
		result.add(new ItemStack(this, 1, 1));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return RenderUtils.getFogColor().getColor();
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return (stack.getItemDamage() == 1)? "tile.openblocks.sky.inverted" : "tile.openblocks.sky.normal";
	}

}
