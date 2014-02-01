package openblocks;

import java.lang.reflect.Method;

import codechicken.nei.api.IConfigureNEI;

import com.google.common.base.Throwables;

public class NEIOpenBlocksConfig implements IConfigureNEI {

	@Override
	public void loadConfig() {
		try {
			// I have no idea how to link with NEI API
			Class<?> cls = Class.forName("codechicken.nei.api.API");
			Method hide = cls.getMethod("hideItem", int.class);

			if (Config.itemHeightMap > 0) hide.invoke(null, Config.itemHeightMap + 256);
			if (Config.itemTunedCrystalId > 0) hide.invoke(null, Config.itemTunedCrystalId + 256);
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}

	@Override
	public String getName() {
		return "OpenBlocks";
	}

	@Override
	public String getVersion() {
		return "@VERSION@";
	}

}
