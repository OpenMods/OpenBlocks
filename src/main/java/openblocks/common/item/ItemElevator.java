package openblocks.common.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openmods.item.ItemOpenBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemElevator extends ItemOpenBlock {

	public ItemElevator(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		return field_150939_a.getRenderColor(stack.getItemDamage());
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs tab, List result) {
		for (int i = 0; i < 16; i++)
			result.add(new ItemStack(this, 1, i));
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

}
