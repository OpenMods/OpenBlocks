package openblocks;

import java.util.Map;

import net.minecraft.launchwrapper.Launch;
import openmods.core.OpenModsCorePlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

//must be higher than one in openmodslib
@SortingIndex(32)
public class OpenBlocksCorePlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		if (!Launch.blackboard.containsKey(OpenModsCorePlugin.CORE_MARKER)) throw new IllegalStateException("OpenModsLib not present or not yet loaded");
		return new String[] { "openblocks.asm.OpenBlocksClassTransformer" };
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
	public String getAccessTransformerClass() {
		return null;
	}

}
