package openblocks.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import openblocks.Config;
import openblocks.integration.TurtleIds;
import openblocks.integration.TurtleUtils;

public class MetaMiracleMagnet extends MetaGeneric {
	public MetaMiracleMagnet(String name, Object... recipes) {
		super(name, recipes);
	}

	@Override
	public boolean hasEffect() {
		return true;
	}

	@Override
	public void addToCreativeList(Item item, int meta, NonNullList<ItemStack> result) {
		super.addToCreativeList(item, meta, result);

		if (Config.enableCraneTurtles && Config.showCraneTurtles) {
			TurtleUtils.addUpgradedTurtles(result, TurtleIds.MAGNET_TURTLE_ID);
		}
	}
}
