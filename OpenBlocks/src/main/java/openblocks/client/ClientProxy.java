package openblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import openblocks.IOpenBlocksProxy;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiVacuumHopper;
import openblocks.client.renderer.tileentity.guide.GuideModelHolder;
import openblocks.client.renderer.tileentity.guide.TileEntityBuilderGuideRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityGuideRenderer;

public class ClientProxy implements IOpenBlocksProxy {
	private final GuideModelHolder holder = new GuideModelHolder();

	@Override
	public void eventInit() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(holder::onModelBake);
		eventBus.addListener(holder::onModelRegister);
	}

	@Override
	public void clientInit() {
		ClientRegistry.bindTileEntityRenderer(OpenBlocks.TileEntities.guide, dispatcher -> new TileEntityGuideRenderer<>(dispatcher, holder));
		ClientRegistry.bindTileEntityRenderer(OpenBlocks.TileEntities.builderGuide, dispatcher -> new TileEntityBuilderGuideRenderer(dispatcher, holder));

		RenderTypeLookup.setRenderLayer(OpenBlocks.Blocks.guide, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(OpenBlocks.Blocks.builderGuide, RenderType.getTranslucent());

		Minecraft.getInstance().deferTask(() ->
				ScreenManager.registerFactory(OpenBlocks.Containers.vacuumHopper, GuiVacuumHopper::new)
		);
	}
}
