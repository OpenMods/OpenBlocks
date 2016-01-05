package openblocks.common.item;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.integration.TurtleIds;
import openblocks.integration.TurtleUtils;

public class MetaMiracleMagnet extends MetaGeneric {
	public MetaMiracleMagnet(String name, Object... recipes) {
		super(name, recipes);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		registerIcon(register, "crane_magnet"); // reuse!
	}

	@Override
	public boolean hasEffect(int renderPass) {
		return true;
	}

	@Override
	public void addToCreativeList(Item item, int meta, List<ItemStack> result) {
		super.addToCreativeList(item, meta, result);

		if (Config.enableCraneTurtles && Config.showCraneTurtles) {
			TurtleUtils.addUpgradedTurtles(result, TurtleIds.MAGNET_TURTLE_ID);
		}
	}
}
