package openblocks.common.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.item.ItemOpenBlock;

public class ItemElevator extends ItemOpenBlock {

	public ItemElevator(Block block) {
		super(block);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> result) {
		for (int i = 0; i < 16; i++)
			result.add(new ItemStack(this, 1, i));
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

}
