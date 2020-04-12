package openblocks.client;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import openblocks.IOpenBlocksProxy;
import openblocks.client.renderer.tileentity.guide.GuideModelHolder;
import openblocks.client.renderer.tileentity.guide.TileEntityBuilderGuideRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityGuideRenderer;
import openblocks.common.tileentity.TileEntityBuilderGuide;
import openblocks.common.tileentity.TileEntityGuide;

public class ClientProxy implements IOpenBlocksProxy {
	@Override
	public void clientInit() {
		final GuideModelHolder holder = new GuideModelHolder();
		holder.registerModels();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(holder::onModelBake);

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class, new TileEntityGuideRenderer<>(holder));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBuilderGuide.class, new TileEntityBuilderGuideRenderer(holder));

	}
}
