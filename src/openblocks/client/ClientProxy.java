package openblocks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.FluidStack;
import openblocks.Config;
import openblocks.IOpenBlocksProxy;
import openblocks.OpenBlocks;
import openblocks.client.billboards.BillboardEventsManager;
import openblocks.client.bindings.BrickBindings;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.client.model.ModelCraneBackpack;
import openblocks.client.radio.RadioManager;
import openblocks.client.renderer.BlockRenderingHandler;
import openblocks.client.renderer.entity.*;
import openblocks.client.renderer.item.*;
import openblocks.client.renderer.tileentity.*;
import openblocks.common.entity.*;
import openblocks.common.tileentity.*;
import openmods.binding.KeyDispatcherBuilder;
import openmods.entity.EntityBlock;
import openmods.entity.renderer.EntityBlockRenderer;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy implements IOpenBlocksProxy {

	public ClientProxy() {}

	private static final ResourceLocation RADIO_VILLAGER_TEXTURE = new ResourceLocation("openblocks", "textures/models/king-ish.png");

	public static class Icons {
		public static Icon xpJuiceStill;
		public static Icon xpJuiceFlowing;
	}

	@ForgeSubscribe
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.textureType == 0) {
			Icons.xpJuiceFlowing = event.map.registerIcon("openblocks:xpjuiceflowing");
			Icons.xpJuiceStill = event.map.registerIcon("openblocks:xpjuicestill");
			if (OpenBlocks.Fluids.openBlocksXPJuice != null) {
				OpenBlocks.Fluids.openBlocksXPJuice.setIcons(Icons.xpJuiceStill, Icons.xpJuiceFlowing);
			}
		}
	}

	@Override
	public void preInit() {
		if (!Config.soSerious) {
			KeyHandler handler = new KeyDispatcherBuilder().addBinding(new BrickBindings()).build();
			KeyBindingRegistry.registerKeyBinding(handler);
		}

		if (Config.radioVillagerId > 0) {
			VillagerRegistry.instance().registerVillagerSkin(Config.radioVillagerId, RADIO_VILLAGER_TEXTURE);
		}
	}

	@Override
	public void init() {
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		MinecraftForge.EVENT_BUS.register(new SoundLoader());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void postInit() {
		SoundEventsManager.instance.init();
	}

	@Override
	public void registerRenderInformation() {

		OpenBlocks.renderId = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new BlockRenderingHandler());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class, new TileEntityGuideRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarget.class, new TileEntityTargetRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrave.class, new TileEntityGraveRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlag.class, new TileEntityFlagRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TileEntityTankRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophy.class, new TileEntityTrophyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBearTrap.class, new TileEntityBearTrapRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySprinkler.class, new TileEntitySprinklerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class, new TileEntityCannonRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVacuumHopper.class, new TileEntityVacuumHopperRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityImaginary.class, new TileEntityImaginaryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFan.class, new TileEntityFanRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVillageHighlighter.class, new TileEntityVillageHighlighterRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAutoAnvil.class, new TileEntityAutoAnvilRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAutoEnchantmentTable.class, new TileEntityAutoEnchantmentTableRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRopeLadder.class, new TileEntityRopeLadderRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDonationStation.class, new TileEntityDonationStationRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityOreCrusher.class, new TileEntityOreCrusherRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintMixer.class, new TileEntityPaintMixerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjector.class, new TileEntityProjectorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySky.class, new TileEntitySkyRenderer());
		// ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGoldenEgg.class,
		// new TileEntityGoldenEggRenderer());

		if (OpenBlocks.Blocks.tank != null) MinecraftForgeClient.registerItemRenderer(OpenBlocks.Blocks.tank.blockID, new ItemRendererTank());

		if (OpenBlocks.Items.luggage != null) {
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.luggage.itemID, new ItemRendererLuggage());
			RenderingRegistry.registerEntityRenderingHandler(EntityLuggage.class, new EntityLuggageRenderer());
		}

		if (OpenBlocks.Blocks.paintCan != null) {
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Blocks.paintCan.blockID, new ItemRendererPaintCan());
		}

		if (OpenBlocks.Items.hangGlider != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityHangGlider.class, new EntityHangGliderRenderer());

			ItemRendererHangGlider hangGliderRenderer = new ItemRendererHangGlider();
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.hangGlider.itemID, hangGliderRenderer);
			MinecraftForge.EVENT_BUS.register(hangGliderRenderer);

			attachPlayerRenderer();
		}

		if (OpenBlocks.Items.sonicGlasses != null) {
			MinecraftForge.EVENT_BUS.register(SoundEventsManager.instance);
		}
		
		MinecraftForge.EVENT_BUS.register(BillboardEventsManager.instance);

		if (OpenBlocks.Items.craneBackpack != null) {
			ModelCraneBackpack.instance.init();
			RenderingRegistry.registerEntityRenderingHandler(EntityMagnet.class, new EntityMagnetRenderer());
			RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, new EntityBlockRenderer());
		}

		MinecraftForge.EVENT_BUS.register(new PlayerRenderEventHandler());
		MinecraftForge.EVENT_BUS.register(new EntitySelectionHandler());

		if (OpenBlocks.Items.cartographer != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityCartographer.class, new EntityCartographerRenderer());
			EntitySelectionHandler.registerRenderer(EntityCartographer.class, new EntityCartographerRenderer.Selection());
		}

		// RenderingRegistry.registerEntityRenderingHandler(EntityMutant.class,
		// new EntityMutantRenderer(new ModelMutant(), 0.7F));

		if (OpenBlocks.Items.goldenEye != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityGoldenEye.class, new EntityGoldenEyeRenderer());
		}

		if (OpenBlocks.Blocks.radio != null) {
			RadioManager.instance.init();
		}

		if (OpenBlocks.Blocks.elevator != null) {
			MinecraftForge.EVENT_BUS.register(new ElevatorMovementHandler());
		}
	}

	@SuppressWarnings("unchecked")
	private static void attachPlayerRenderer() {
		if (Config.tryHookPlayerRenderer) {
			// Get current renderer and check that it's Mojangs
			Render render = (Render)RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
			if (render.getClass().equals(net.minecraft.client.renderer.entity.RenderPlayer.class)) {
				EntityPlayerRenderer playerRenderer = new EntityPlayerRenderer();
				playerRenderer.setRenderManager(RenderManager.instance);
				RenderManager.instance.entityRenderMap.put(EntityPlayer.class, playerRenderer);
			}
		}
	}

	@Override
	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, ForgeDirection direction, float angleRadians, float spread) {
		FXLiquidSpray spray = new FXLiquidSpray(worldObj, water, x, y, z, direction, angleRadians, spread);
		Minecraft.getMinecraft().effectRenderer.addEffect(spray);
	}
}
