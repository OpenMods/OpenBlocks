package openblocks;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class CorePlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "openblocks.asm.ClassTransformerEntityPlayer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	@Deprecated
	public String[] getLibraryRequestClass() {
		return null;
	}

}
