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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Deprecated
	public String[] getLibraryRequestClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
