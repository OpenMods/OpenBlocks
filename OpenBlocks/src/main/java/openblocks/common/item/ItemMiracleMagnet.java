package openblocks.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import openblocks.Config;
import openblocks.integration.TurtleIds;
import openblocks.integration.TurtleUtils;

public class ItemMiracleMagnet extends Item {

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> result) {
		super.getSubItems(tab, result);

		if (Config.enableCraneTurtles && Config.showCraneTurtles) {
			TurtleUtils.addUpgradedTurtles(result, TurtleIds.MAGNET_TURTLE_ID);
		}
	}

}
