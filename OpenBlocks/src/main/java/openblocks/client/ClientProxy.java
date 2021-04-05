package openblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import openblocks.IOpenBlocksProxy;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiVacuumHopper;
import openblocks.client.renderer.tileentity.guide.GuideModelHolder;
import openblocks.client.renderer.tileentity.guide.TileEntityBuilderGuideRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityGuideRenderer;
import openblocks.client.renderer.tileentity.tank.TileEntityTankRenderer;
import openblocks.common.ElevatorActionHandler;
import openblocks.common.item.SlimalyzerItem;
import openblocks.common.item.ItemTankBlock;

public class ClientProxy implements IOpenBlocksProxy {
	private final GuideModelHolder holder = new GuideModelHolder();

	@Override
	public void eventInit() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(holder::onModelBake);
		modBus.addListener(holder::onModelRegister);
		modBus.addListener(BlockColorHandlerRegistration::registerItemColorHandlers);

		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(ElevatorActionHandler::onPlayerMovement);
	}

	@Override
	public void clientInit(FMLClientSetupEvent event) {
		ClientRegistry.bindTileEntityRenderer(OpenBlocks.TileEntities.guide, dispatcher -> new TileEntityGuideRenderer<>(dispatcher, holder));
		ClientRegistry.bindTileEntityRenderer(OpenBlocks.TileEntities.builderGuide, dispatcher -> new TileEntityBuilderGuideRenderer(dispatcher, holder));
		ClientRegistry.bindTileEntityRenderer(OpenBlocks.TileEntities.tank, TileEntityTankRenderer::new);

		RenderTypeLookup.setRenderLayer(OpenBlocks.Blocks.guide, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(OpenBlocks.Blocks.builderGuide, RenderType.getTranslucent());

		Minecraft.getInstance().deferTask(() -> {
					ScreenManager.registerFactory(OpenBlocks.Containers.vacuumHopper, GuiVacuumHopper::new);
					ItemModelsProperties.registerProperty(OpenBlocks.Items.tank, new ResourceLocation("level"), (stack, worldIn, entityIn) -> {
						final FluidTank tank = ItemTankBlock.readTank(stack);
						return 16.0f * tank.getFluidAmount() / tank.getCapacity();
					});
				}
		);
		event.enqueueWork(() -> {
				ItemModelsProperties.registerProperty(OpenBlocks.Items.slimalyzer, new ResourceLocation(OpenBlocks.MODID, "active"), (ItemStack stack, ClientWorld worldIn, LivingEntity entityIn) -> SlimalyzerItem.isActive(stack)? 2 : 0);
		});
	}
}
