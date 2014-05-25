package openblocks.common.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import openblocks.Config;
import openblocks.integration.TurtleIds;
import openblocks.integration.TurtleUtils;

public class MetaMiracleMagnet extends MetaGeneric {
	public MetaMiracleMagnet(String name, Object... recipes) {
		super(name, recipes);
	}

	@Override
	public void registerIcons(IIconRegister register) {
		registerIcon(register, "crane_magnet"); // reuse!
	}

	@Override
	public boolean hasEffect(int renderPass) {
		return true;
	}

	@Override
	public void addToCreativeList(int itemId, int meta, List<ItemStack> result) {
		super.addToCreativeList(itemId, meta, result);

		if (Config.enableCraneTurtles && Config.showCraneTurtles) {
			TurtleUtils.addUpgradedTurtles(result, TurtleIds.MAGNET_TURTLE_ID);
		}
	}
}
