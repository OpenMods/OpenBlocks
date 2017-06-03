package openblocks.client.renderer.item.devnull;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import openblocks.OpenBlocks;

public class DevNullModelLoader implements ICustomModelLoader {

	private static final Set<String> models = ImmutableSet.of(
			"magic-devnull",
			"models/block/magic-devnull",
			"models/item/magic-devnull");

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {}

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return modelLocation.getResourceDomain().equals(OpenBlocks.MODID)
				&& models.contains(modelLocation.getResourcePath());
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws Exception {
		return DevNullModel.INSTANCE;
	}

}
