package openblocks;

import java.lang.reflect.Method;

import codechicken.nei.api.IConfigureNEI;

import com.google.common.base.Throwables;

public class NEIOpenBlocksConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		if (Config.itemHeightMap > 0) {
			try {
				// I have no idea how to link with NEI API
				Class<?> cls = Class.forName("codechicken.nei.api.API");
				Method hide = cls.getMethod("hideItem", int.class);
				hide.invoke(null, Config.itemHeightMap + 256);
			} catch (Exception e) {
				Throwables.propagate(e);
			}
		}
	}

	@Override
	public String getName() {
		return "OpenBlocks";
	}

	@Override
	public String getVersion() {
		return "1.2.x";
	}

}
