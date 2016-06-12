package openblocks.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import openblocks.Config;
import openblocks.IOpenBlocksProxy;
import openblocks.OpenBlocks;
import openblocks.client.bindings.KeyInputHandler;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.client.model.ModelCraneBackpack;
import openblocks.client.renderer.block.BlockCanvasRenderer;
import openblocks.client.renderer.block.BlockGuideRenderer;
import openblocks.client.renderer.block.BlockPaintCanRenderer;
import openblocks.client.renderer.block.BlockPathRenderer;
import openblocks.client.renderer.block.BlockRopeLadderRenderer;
import openblocks.client.renderer.block.BlockSkyRenderer;
import openblocks.client.renderer.block.BlockTankRenderer;
import openblocks.client.renderer.block.BlockWorkingProjectorRenderer;
import openblocks.client.renderer.entity.EntityCartographerRenderer;
import openblocks.client.renderer.entity.EntityGoldenEyeRenderer;
import openblocks.client.renderer.entity.EntityHangGliderRenderer;
import openblocks.client.renderer.entity.EntityLuggageRenderer;
import openblocks.client.renderer.entity.EntityMagnetRenderer;
import openblocks.client.renderer.entity.EntityMiniMeRenderer;
import openblocks.client.renderer.entity.EntitySelectionHandler;
import openblocks.client.renderer.item.ItemRendererDevNull;
import openblocks.client.renderer.item.ItemRendererHangGlider;
import openblocks.client.renderer.item.ItemRendererLuggage;
import openblocks.client.renderer.item.ItemRendererPaintCan;
import openblocks.client.renderer.item.ItemRendererTank;
import openblocks.client.renderer.item.ItemRendererTrophy;
import openblocks.client.renderer.tileentity.TileEntityAutoAnvilRenderer;
import openblocks.client.renderer.tileentity.TileEntityAutoEnchantmentTableRenderer;
import openblocks.client.renderer.tileentity.TileEntityBearTrapRenderer;
import openblocks.client.renderer.tileentity.TileEntityCannonRenderer;
import openblocks.client.renderer.tileentity.TileEntityDonationStationRenderer;
import openblocks.client.renderer.tileentity.TileEntityFanRenderer;
import openblocks.client.renderer.tileentity.TileEntityFlagRenderer;
import openblocks.client.renderer.tileentity.TileEntityGoldenEggRenderer;
import openblocks.client.renderer.tileentity.TileEntityGraveRenderer;
import openblocks.client.renderer.tileentity.TileEntityImaginaryRenderer;
import openblocks.client.renderer.tileentity.TileEntityPaintMixerRenderer;
import openblocks.client.renderer.tileentity.TileEntityProjectorRenderer;
import openblocks.client.renderer.tileentity.TileEntitySkyRenderer;
import openblocks.client.renderer.tileentity.TileEntitySprinklerRenderer;
import openblocks.client.renderer.tileentity.TileEntityTankRenderer;
import openblocks.client.renderer.tileentity.TileEntityTargetRenderer;
import openblocks.client.renderer.tileentity.TileEntityTrophyRenderer;
import openblocks.client.renderer.tileentity.TileEntityVacuumHopperRenderer;
import openblocks.client.renderer.tileentity.TileEntityVillageHighlighterRenderer;
import openblocks.client.renderer.tileentity.TileEntityXPShowerRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityBuilderGuideRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityGuideRenderer;
import openblocks.common.block.BlockGuide;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityGoldenEye;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.entity.EntityMiniMe;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.common.tileentity.TileEntityBuilderGuide;
import openblocks.common.tileentity.TileEntityCannon;
import openblocks.common.tileentity.TileEntityDonationStation;
import openblocks.common.tileentity.TileEntityFan;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityGoldenEgg;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openblocks.common.tileentity.TileEntityProjector;
import openblocks.common.tileentity.TileEntitySky;
import openblocks.common.tileentity.TileEntitySprinkler;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openblocks.common.tileentity.TileEntityXPShower;
import openblocks.enchantments.flimflams.LoreFlimFlam;
import openmods.entity.EntityBlock;
import openmods.entity.renderer.EntityBlockRenderer;
import openmods.renderer.BlockRenderingHandler;
import openmods.renderer.BlockRenderingValidator;
import openmods.renderer.IBlockRenderer;
import openmods.renderer.RotatedBlockRenderer;

public class ClientProxy implements IOpenBlocksProxy {

	public ClientProxy() {}

	private static final ResourceLocation RADIO_VILLAGER_TEXTURE = new ResourceLocation("openblocks", "textures/models/king-ish.png");

	public static class Icons {
		public static IIcon xpJuiceStill;
		public static IIcon xpJuiceFlowing;
	}

	@SubscribeEvent
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 0) {
			Icons.xpJuiceFlowing = event.map.registerIcon("openblocks:xpjuiceflowing");
			Icons.xpJuiceStill = event.map.registerIcon("openblocks:xpjuicestill");

			OpenBlocks.Fluids.xpJuice.setIcons(Icons.xpJuiceStill, Icons.xpJuiceFlowing);
		}
	}

	@Override
	public void preInit() {
		new KeyInputHandler().setup();

		if (Config.radioVillagerId > 0) {
			VillagerRegistry.instance().registerVillagerSkin(Config.radioVillagerId, RADIO_VILLAGER_TEXTURE);
		}

		if (Config.flimFlamEnchantmentId > 0) {
			MinecraftForge.EVENT_BUS.register(new LoreFlimFlam.DisplayHandler());
		}
	}

	@Override
	public void init() {
		FMLCommonHandler.instance().bus().register(new ClientTickHandler());
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EntityMiniMe.OwnerChangeHandler());
	}

	@Override
	public void postInit() {
		SoundEventsManager.instance.init();
	}

	@Override
	public void registerRenderInformation() {

		{
			OpenBlocks.renderIdFull = RenderingRegistry.getNextAvailableRenderId();
			final BlockRenderingHandler blockRenderingHandler = new BlockRenderingHandler(OpenBlocks.renderIdFull, true);

			blockRenderingHandler.addRenderer(OpenBlocks.Blocks.path, new BlockPathRenderer());

			{
				final BlockCanvasRenderer canvasRenderer = new BlockCanvasRenderer();
				blockRenderingHandler.addRenderer(OpenBlocks.Blocks.canvas, canvasRenderer);
				blockRenderingHandler.addRenderer(OpenBlocks.Blocks.canvasGlass, canvasRenderer);
			}

			blockRenderingHandler.addRenderer(OpenBlocks.Blocks.paintCan, new BlockPaintCanRenderer());
			blockRenderingHandler.addRenderer(OpenBlocks.Blocks.sky, new BlockSkyRenderer());
			blockRenderingHandler.addRenderer(OpenBlocks.Blocks.tank, new BlockTankRenderer());

			{
				final IBlockRenderer<BlockGuide> guideBlockRenderer = RotatedBlockRenderer.wrap(new BlockGuideRenderer());
				blockRenderingHandler.addRenderer(OpenBlocks.Blocks.guide, guideBlockRenderer);
				blockRenderingHandler.addRenderer(OpenBlocks.Blocks.builderGuide, guideBlockRenderer);
			}

			blockRenderingHandler.addRenderer(OpenBlocks.Blocks.workingProjector, new BlockWorkingProjectorRenderer());

			RenderingRegistry.registerBlockHandler(blockRenderingHandler);
		}

		{
			OpenBlocks.renderIdFlat = RenderingRegistry.getNextAvailableRenderId();
			final BlockRenderingHandler blockRenderingHandler = new BlockRenderingHandler(OpenBlocks.renderIdFlat, false);

			blockRenderingHandler.addRenderer(OpenBlocks.Blocks.ropeLadder, new BlockRopeLadderRenderer());

			RenderingRegistry.registerBlockHandler(blockRenderingHandler);
		}

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class, new TileEntityGuideRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBuilderGuide.class, new TileEntityBuilderGuideRenderer());
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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDonationStation.class, new TileEntityDonationStationRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintMixer.class, new TileEntityPaintMixerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjector.class, new TileEntityProjectorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySky.class, new TileEntitySkyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityXPShower.class, new TileEntityXPShowerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGoldenEgg.class, new TileEntityGoldenEggRenderer());

		if (OpenBlocks.Blocks.tank != null) MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(OpenBlocks.Blocks.tank), new ItemRendererTank());

		if (OpenBlocks.Items.luggage != null) {
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.luggage, new ItemRendererLuggage());
			RenderingRegistry.registerEntityRenderingHandler(EntityLuggage.class, new EntityLuggageRenderer());
		}

		if (OpenBlocks.Blocks.paintCan != null) {
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(OpenBlocks.Blocks.paintCan), new ItemRendererPaintCan());
		}

		if (OpenBlocks.Items.hangGlider != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityHangGlider.class, new EntityHangGliderRenderer());
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.hangGlider, new ItemRendererHangGlider());
			MinecraftForge.EVENT_BUS.register(new GliderPlayerRenderHandler());
		}

		if (OpenBlocks.Items.sonicGlasses != null) {
			MinecraftForge.EVENT_BUS.register(SoundEventsManager.instance);
		}

		if (OpenBlocks.Items.craneBackpack != null) {
			ModelCraneBackpack.instance.init();
			RenderingRegistry.registerEntityRenderingHandler(EntityMagnet.class, new EntityMagnetRenderer());
			RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, new EntityBlockRenderer());
		}

		if (OpenBlocks.Blocks.goldenEgg != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityMiniMe.class, new EntityMiniMeRenderer());
		}

		if (OpenBlocks.Items.devNull != null) {
			MinecraftForgeClient.registerItemRenderer(OpenBlocks.Items.devNull, new ItemRendererDevNull());
		}

		if (OpenBlocks.Items.sleepingBag != null) {
			MinecraftForge.EVENT_BUS.register(new SleepingBagRenderHandler());
		}

		MinecraftForge.EVENT_BUS.register(new EntitySelectionHandler());

		if (OpenBlocks.Items.cartographer != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityCartographer.class, new EntityCartographerRenderer());
			EntitySelectionHandler.registerRenderer(EntityCartographer.class, new EntityCartographerRenderer.Selection());
		}

		if (OpenBlocks.Blocks.trophy != null) {
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(OpenBlocks.Blocks.trophy), new ItemRendererTrophy());
		}

		if (OpenBlocks.Items.goldenEye != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityGoldenEye.class, new EntityGoldenEyeRenderer());
		}

		new BlockRenderingValidator().verifyBlocks(OpenBlocks.Blocks.class);
	}

	private static void spawnParticle(EntityFX spray) {
		Minecraft.getMinecraft().effectRenderer.addEffect(spray);
	}

	@Override
	public void spawnLiquidSpray(World worldObj, Fluid fluid, double x, double y, double z, float scale, float gravity, Vec3 velocity) {
		spawnParticle(new FXLiquidSpray(worldObj, fluid, x, y, z, scale, gravity, velocity));
	}

	@Override
	public void spawnParticleSpray(World worldObj, IIcon icon, double x, double y, double z, float scale, float gravity, Vec3 velocity) {
		if (icon != null) spawnParticle(new FXLiquidSpray(worldObj, icon, x, y, z, scale, gravity, velocity));
	}
}
