package openblocks;

import java.lang.reflect.Method;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.OpenBlocks.Items;
import codechicken.nei.api.IConfigureNEI;

import com.google.common.base.Throwables;

public class NEIOpenBlocksConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		try {
			// I have no idea how to link with NEI API
			Class<?> cls = Class.forName("codechicken.nei.api.API");
			Method hide = cls.getMethod("hideItem", ItemStack.class);

			if (Items.heightMap != null) hide.invoke(null, new ItemStack(Items.heightMap, 1, OreDictionary.WILDCARD_VALUE));
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}

	@Override
	public String getName() {
		return "OpenBlocks-ItemHider";
	}

	@Override
	public String getVersion() {
		return "0.0";
	}

}
