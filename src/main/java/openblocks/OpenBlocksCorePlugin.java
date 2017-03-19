package openblocks;

import java.util.Map;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import openmods.core.OpenModsCorePlugin;

//must be higher than one in openmodslib
@SortingIndex(32)
public class OpenBlocksCorePlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		if (!Launch.blackboard.containsKey(OpenModsCorePlugin.CORE_MARKER)) throw new IllegalStateException("OpenModsLib not present, not yet loaded or too old (needs at least 0.7.1)");
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
