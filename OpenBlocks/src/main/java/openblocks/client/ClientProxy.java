package openblocks.client;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.IOpenBlocksProxy;
import openblocks.OpenBlocks;
import openblocks.client.bindings.KeyInputHandler;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.client.model.ModelCraneBackpack;
import openblocks.client.renderer.SkyBlockRenderer;
import openblocks.client.renderer.TextureUploader;
import openblocks.client.renderer.block.PathModel;
import openblocks.client.renderer.block.canvas.CanvasTextureManager;
import openblocks.client.renderer.block.canvas.ModelCanvas;
import openblocks.client.renderer.entity.EntityCartographerRenderer;
import openblocks.client.renderer.entity.EntityGlyphRenderer;
import openblocks.client.renderer.entity.EntityHangGliderRenderer;
import openblocks.client.renderer.entity.EntityLuggageRenderer;
import openblocks.client.renderer.entity.EntityMagnetRenderer;
import openblocks.client.renderer.entity.EntityMiniMeRenderer;
import openblocks.client.renderer.entity.EntitySelectionHandler;
import openblocks.client.renderer.item.ModelGlyph;
import openblocks.client.renderer.item.devnull.DevNullModel;
import openblocks.client.renderer.item.stencil.ModelStencil;
import openblocks.client.renderer.item.stencil.StencilItemOverride;
import openblocks.client.renderer.item.stencil.StencilTextureManager;
import openblocks.client.renderer.tileentity.TileEntityAutoEnchantmentTableRenderer;
import openblocks.client.renderer.tileentity.TileEntityBearTrapRenderer;
import openblocks.client.renderer.tileentity.TileEntityCannonRenderer;
import openblocks.client.renderer.tileentity.TileEntityFanRenderer;
import openblocks.client.renderer.tileentity.TileEntityGoldenEggRenderer;
import openblocks.client.renderer.tileentity.TileEntityGraveRenderer;
import openblocks.client.renderer.tileentity.TileEntityImaginaryRenderer;
import openblocks.client.renderer.tileentity.TileEntityPaintMixerRenderer;
import openblocks.client.renderer.tileentity.TileEntityProjectorRenderer;
import openblocks.client.renderer.tileentity.TileEntitySkyRenderer;
import openblocks.client.renderer.tileentity.TileEntitySprinklerRenderer;
import openblocks.client.renderer.tileentity.TileEntityTankRenderer;
import openblocks.client.renderer.tileentity.TileEntityTrophyRenderer;
import openblocks.client.renderer.tileentity.TileEntityVillageHighlighterRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityBuilderGuideRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityGuideRenderer;
import openblocks.common.StencilPattern;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityGlyph;
import openblocks.common.entity.EntityGoldenEye;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.entity.EntityMiniMe;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.common.tileentity.TileEntityBuilderGuide;
import openblocks.common.tileentity.TileEntityCannon;
import openblocks.common.tileentity.TileEntityFan;
import openblocks.common.tileentity.TileEntityGoldenEgg;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityImaginaryCrayon;
import openblocks.common.tileentity.TileEntityImaginaryPencil;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openblocks.common.tileentity.TileEntityProjector;
import openblocks.common.tileentity.TileEntitySky;
import openblocks.common.tileentity.TileEntitySprinkler;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openblocks.enchantments.flimflams.LoreFlimFlam;
import openmods.block.OpenBlock;
import openmods.entity.EntityBlock;
import openmods.entity.renderer.EntityBlockRenderer;
import openmods.model.MappedModelLoader;
import openmods.model.ModelUtils;
import openmods.utils.render.MarkerClassGenerator;

public class ClientProxy implements IOpenBlocksProxy {

	public ClientProxy() {}

	@Override
	public void preInit() {
		OBJLoader.INSTANCE.addDomain(OpenBlocks.MODID);
		new KeyInputHandler().setup();

		if (Config.flimFlamEnchantmentEnabled) {
			MinecraftForge.EVENT_BUS.register(new LoreFlimFlam.DisplayHandler());
		}

		if (OpenBlocks.Blocks.trophy != null) {
			Item trophyItem = Item.getItemFromBlock(OpenBlocks.Blocks.trophy);
			for (Trophy trophy : Trophy.VALUES)
				registerTrophyItemRenderer(trophyItem, trophy);
		}

		registerTesrStateMappers();

		ModelLoaderRegistry.registerLoader(MappedModelLoader.builder()
				.put("magic-devnull", DevNullModel.INSTANCE)
				.put("magic-path", PathModel.INSTANCE)
				.put("magic-stencil", ModelStencil.INSTANCE)
				.put("magic-canvas", ModelCanvas.INSTANCE)
				.put("magic-glyph", ModelGlyph.INSTANCE)
				.build(OpenBlocks.MODID));

		RenderingRegistry.registerEntityRenderingHandler(EntityHangGlider.class, EntityHangGliderRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityLuggage.class, EntityLuggageRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMagnet.class, EntityMagnetRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, EntityBlockRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityMiniMe.class, EntityMiniMeRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityGlyph.class, (manager) -> {
			final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
			return new EntityGlyphRenderer(manager, renderItem);
		});

		EntityCartographerRenderer.registerListener();
		RenderingRegistry.registerEntityRenderingHandler(EntityCartographer.class, EntityCartographerRenderer::new);

		if (OpenBlocks.Items.goldenEye != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityGoldenEye.class,
					(manager) -> {
						final RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
						return new RenderSnowball<>(manager, OpenBlocks.Items.goldenEye, renderItem);
					});
		}

		if (OpenBlocks.Items.stencil != null) {
			StencilTextureManager.INSTANCE.register(StencilItemOverride.BACKGROUND_TEXTURE, StencilPattern.values().length);
			MinecraftForge.EVENT_BUS.register(StencilTextureManager.INSTANCE);
		}

		if (OpenBlocks.Blocks.canvas != null) {
			MinecraftForge.EVENT_BUS.register(CanvasTextureManager.INSTANCE);
		}

		if (OpenBlocks.Items.stencil != null || OpenBlocks.Blocks.canvas != null) {
			MinecraftForge.EVENT_BUS.register(TextureUploader.INSTANCE);
		}

		if (OpenBlocks.Blocks.sky != null) {
			((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(SkyBlockRenderer.INSTANCE);
		}

		if (OpenBlocks.Items.heightMap != null) {
			ModelUtils.registerMetaInsensitiveModel(OpenBlocks.Items.heightMap);
		}

		if (OpenBlocks.Blocks.tank != null) {
			MinecraftForge.EVENT_BUS.register(new FluidTextureRegisterListener());
		}

		if (OpenBlocks.Items.glyph != null) {
			ModelUtils.registerMetaInsensitiveModel(OpenBlocks.Items.glyph);
			MinecraftForge.EVENT_BUS.register(new GlyphPlacementGridRenderer());
		}

		SoundEventsManager.instance.init();

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class, new TileEntityGuideRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBuilderGuide.class, new TileEntityBuilderGuideRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrave.class, new TileEntityGraveRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TileEntityTankRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophy.class, new TileEntityTrophyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBearTrap.class, new TileEntityBearTrapRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySprinkler.class, new TileEntitySprinklerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class, new TileEntityCannonRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityImaginaryCrayon.class, new TileEntityImaginaryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityImaginaryPencil.class, new TileEntityImaginaryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFan.class, new TileEntityFanRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVillageHighlighter.class, new TileEntityVillageHighlighterRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAutoEnchantmentTable.class, new TileEntityAutoEnchantmentTableRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintMixer.class, new TileEntityPaintMixerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySky.class, new TileEntitySkyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGoldenEgg.class, new TileEntityGoldenEggRenderer());

		if (OpenBlocks.Blocks.projector != null) {
			final ModelResourceLocation spinnerModel = new ModelResourceLocation(OpenBlocks.Blocks.projector.getRegistryName(), "spinner");
			final TileEntityProjectorRenderer renderer = new TileEntityProjectorRenderer(spinnerModel);
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjector.class, renderer);
			MinecraftForge.EVENT_BUS.register(renderer);
		}
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EntityMiniMe.OwnerChangeHandler());
	}

	@Override
	public void postInit() {}

	private static class FluidTextureRegisterListener {
		@SubscribeEvent
		public void onTextureStitch(TextureStitchEvent.Pre evt) {
			for (Fluid f : FluidRegistry.getRegisteredFluids().values()) {
				final ResourceLocation fluidTexture = f.getStill();
				if (fluidTexture != null)
					evt.getMap().registerSprite(fluidTexture);
			}
		}
	}

	@Override
	public void registerRenderInformation() {
		registerTesrItemRenderers();

		if (OpenBlocks.Items.hangGlider != null) {
			MinecraftForge.EVENT_BUS.register(new GliderPlayerRenderHandler());
		}

		if (OpenBlocks.Items.sonicGlasses != null) {
			MinecraftForge.EVENT_BUS.register(SoundEventsManager.instance);
		}

		if (OpenBlocks.Items.craneBackpack != null) {
			ModelCraneBackpack.instance.init();
		}

		if (OpenBlocks.Items.sleepingBag != null) {
			MinecraftForge.EVENT_BUS.register(new SleepingBagRenderHandler());
		}

		MinecraftForge.EVENT_BUS.register(new EntitySelectionHandler());

		if (OpenBlocks.Items.cartographer != null) {
			EntitySelectionHandler.registerRenderer(EntityCartographer.class, new EntityCartographerRenderer.Selection());
		}

		if (OpenBlocks.Blocks.imaginaryPencil != null || OpenBlocks.Blocks.imaginaryCrayon != null) {
			MinecraftForge.EVENT_BUS.register(new TileEntityImaginaryRenderer.CacheFlushListener());
		}

	}

	@SuppressWarnings("deprecation")
	private static void registerTrophyItemRenderer(Item item, Trophy trophy) {
		// this is probably enough to revoke my modding licence!
		final Class<? extends TileEntityTrophy> markerCls = MarkerClassGenerator.instance.createMarkerCls(TileEntityTrophy.class);
		final int meta = trophy.ordinal();
		ForgeHooksClient.registerTESRItemStack(item, meta, markerCls);
		ClientRegistry.bindTileEntitySpecialRenderer(markerCls, new TileEntityTrophyRenderer(trophy));
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(OpenBlocks.location("trophy"), "inventory"));
	}

	private static abstract class BlockConsumer {
		public final void nom(OpenBlock block) {
			if (block != null) nomNom(block);
		}

		protected abstract void nomNom(OpenBlock block);
	}

	private static void visitTesrBlocks(BlockConsumer consumer) {
		consumer.nom(OpenBlocks.Blocks.cannon);
	}

	@SuppressWarnings("deprecation")
	private static void registerTesrItemRenderers() {
		visitTesrBlocks(new BlockConsumer() {
			@Override
			public void nomNom(OpenBlock block) {
				Item item = Item.getItemFromBlock(block);
				ForgeHooksClient.registerTESRItemStack(item, 0, block.getTileClass());
			}
		});
	}

	private static void registerTesrStateMappers() {
		visitTesrBlocks(new BlockConsumer() {
			@Override
			public void nomNom(OpenBlock block) {
				ImmutableMap.Builder<IBlockState, ModelResourceLocation> statesBuilder = ImmutableMap.builder();
				final ModelResourceLocation location = new ModelResourceLocation(block.getRegistryName(), "dummy");
				for (IBlockState state : block.getBlockState().getValidStates())
					statesBuilder.put(state, location);

				final Map<IBlockState, ModelResourceLocation> states = statesBuilder.build();
				ModelLoader.setCustomStateMapper(block, blockIn -> states);
			}
		});
	}

	@Override
	public int getParticleSettings() {
		return Minecraft.getMinecraft().gameSettings.particleSetting;
	}

	private static void spawnParticle(Particle spray) {
		Minecraft.getMinecraft().effectRenderer.addEffect(spray);
	}

	@Override
	public void spawnLiquidSpray(World worldObj, FluidStack fluid, double x, double y, double z, float scale, float gravity, Vec3d velocity) {
		spawnParticle(new FXLiquidSpray(worldObj, fluid, x, y, z, scale, gravity, velocity));
	}
}
