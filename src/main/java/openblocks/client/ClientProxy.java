package openblocks.client;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.IOpenBlocksProxy;
import openblocks.OpenBlocks;
import openblocks.client.bindings.KeyInputHandler;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.client.model.ModelAutoAnvil;
import openblocks.client.model.ModelBearTrap;
import openblocks.client.model.ModelCraneBackpack;
import openblocks.client.model.ModelPiggy;
import openblocks.client.model.ModelSprinkler;
import openblocks.client.model.ModelXPShower;
import openblocks.client.renderer.SkyBlockRenderer;
import openblocks.client.renderer.TextureUploader;
import openblocks.client.renderer.block.PathModel;
import openblocks.client.renderer.block.canvas.CanvasTextureManager;
import openblocks.client.renderer.block.canvas.ModelCanvas;
import openblocks.client.renderer.entity.EntityCartographerRenderer;
import openblocks.client.renderer.entity.EntityHangGliderRenderer;
import openblocks.client.renderer.entity.EntityLuggageRenderer;
import openblocks.client.renderer.entity.EntityMagnetRenderer;
import openblocks.client.renderer.entity.EntityMiniMeRenderer;
import openblocks.client.renderer.entity.EntitySelectionHandler;
import openblocks.client.renderer.item.devnull.DevNullModel;
import openblocks.client.renderer.item.stencil.ModelStencil;
import openblocks.client.renderer.item.stencil.StencilItemOverride;
import openblocks.client.renderer.item.stencil.StencilTextureManager;
import openblocks.client.renderer.tileentity.TileEntityAutoEnchantmentTableRenderer;
import openblocks.client.renderer.tileentity.TileEntityCannonRenderer;
import openblocks.client.renderer.tileentity.TileEntityFanRenderer;
import openblocks.client.renderer.tileentity.TileEntityFlagRenderer;
import openblocks.client.renderer.tileentity.TileEntityGoldenEggRenderer;
import openblocks.client.renderer.tileentity.TileEntityGraveRenderer;
import openblocks.client.renderer.tileentity.TileEntityImaginaryRenderer;
import openblocks.client.renderer.tileentity.TileEntityPaintMixerRenderer;
import openblocks.client.renderer.tileentity.TileEntityProjectorRenderer;
import openblocks.client.renderer.tileentity.TileEntitySkyRenderer;
import openblocks.client.renderer.tileentity.TileEntityTankRenderer;
import openblocks.client.renderer.tileentity.TileEntityTrophyRenderer;
import openblocks.client.renderer.tileentity.TileEntityVillageHighlighterRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityBuilderGuideRenderer;
import openblocks.client.renderer.tileentity.guide.TileEntityGuideRenderer;
import openblocks.common.StencilPattern;
import openblocks.common.TrophyHandler.Trophy;
import openblocks.common.block.BlockCanvas;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockPaintCan;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityGoldenEye;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.entity.EntityMiniMe;
import openblocks.common.item.ItemDevNull;
import openblocks.common.item.ItemElevator;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginationGlasses;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemPaintCan;
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
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openblocks.common.tileentity.TileEntityXPShower;
import openblocks.enchantments.flimflams.LoreFlimFlam;
import openmods.block.OpenBlock;
import openmods.entity.EntityBlock;
import openmods.entity.renderer.EntityBlockRenderer;
import openmods.model.MappedModelLoader;
import openmods.model.ModelUtils;
import openmods.renderer.SimpleModelTileEntityRenderer;
import openmods.utils.render.MarkerClassGenerator;

public class ClientProxy implements IOpenBlocksProxy {

	public ClientProxy() {}

	@Override
	public void preInit() {
		new KeyInputHandler().setup();

		if (Config.flimFlamEnchantmentEnabled) {
			MinecraftForge.EVENT_BUS.register(new LoreFlimFlam.DisplayHandler());
		}

		if (OpenBlocks.Blocks.trophy != null) {
			Item trophyItem = Item.getItemFromBlock(OpenBlocks.Blocks.trophy);
			for (Trophy trophy : Trophy.VALUES)
				registerTrophyItemRenderer(trophyItem, trophy);
		}

		tempHackRegisterTesrStateMappers();

		ModelLoaderRegistry.registerLoader(MappedModelLoader.builder()
				.put("magic-devnull", DevNullModel.INSTANCE)
				.put("magic-path", PathModel.INSTANCE)
				.put("magic-stencil", ModelStencil.INSTANCE)
				.put("magic-canvas", ModelCanvas.INSTANCE)
				.build(OpenBlocks.MODID));

		if (OpenBlocks.Items.hangGlider != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityHangGlider.class, new IRenderFactory<EntityHangGlider>() {
				@Override
				public Render<EntityHangGlider> createRenderFor(RenderManager manager) {
					return new EntityHangGliderRenderer(manager);
				}
			});
		}

		if (OpenBlocks.Items.luggage != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityLuggage.class, new IRenderFactory<EntityLuggage>() {
				@Override
				public Render<EntityLuggage> createRenderFor(RenderManager manager) {
					return new EntityLuggageRenderer(manager);
				}
			});
		}

		if (OpenBlocks.Items.craneBackpack != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityMagnet.class, new IRenderFactory<EntityMagnet>() {
				@Override
				public Render<? super EntityMagnet> createRenderFor(RenderManager manager) {
					return new EntityMagnetRenderer(manager);
				}
			});

			RenderingRegistry.registerEntityRenderingHandler(EntityBlock.class, new IRenderFactory<EntityBlock>() {
				@Override
				public Render<? super EntityBlock> createRenderFor(RenderManager manager) {
					return new EntityBlockRenderer(manager);
				}
			});
		}

		if (OpenBlocks.Blocks.goldenEgg != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityMiniMe.class, new IRenderFactory<EntityMiniMe>() {
				@Override
				public Render<? super EntityMiniMe> createRenderFor(RenderManager manager) {
					return new EntityMiniMeRenderer(manager);
				}
			});
		}

		if (OpenBlocks.Items.cartographer != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityCartographer.class, new IRenderFactory<EntityCartographer>() {
				@Override
				public Render<? super EntityCartographer> createRenderFor(RenderManager manager) {
					return new EntityCartographerRenderer(manager);
				}
			});
		}

		if (OpenBlocks.Items.goldenEye != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityGoldenEye.class, new IRenderFactory<EntityGoldenEye>() {
				@Override
				public Render<? super EntityGoldenEye> createRenderFor(RenderManager manager) {
					return new RenderSnowball<EntityGoldenEye>(manager, OpenBlocks.Items.goldenEye, Minecraft.getMinecraft().getRenderItem());
				}
			});
		}

		if (OpenBlocks.Items.stencil != null) {
			StencilTextureManager.INSTANCE.register(StencilItemOverride.BACKGROUND_TEXTURE, StencilPattern.values().length);
			MinecraftForge.EVENT_BUS.register(StencilTextureManager.INSTANCE);
			ModelUtils.registerMetaInsensitiveModel(OpenBlocks.Items.stencil);
		}

		if (OpenBlocks.Blocks.canvas != null) {
			MinecraftForge.EVENT_BUS.register(CanvasTextureManager.INSTANCE);
		}

		if (OpenBlocks.Items.stencil != null || OpenBlocks.Blocks.canvas != null) {
			MinecraftForge.EVENT_BUS.register(TextureUploader.INSTANCE);
		}

		if (OpenBlocks.Blocks.elevator != null) {
			ModelUtils.registerMetaInsensitiveModel(Item.getItemFromBlock(OpenBlocks.Blocks.elevator));
		}

		if (OpenBlocks.Blocks.elevatorRotating != null) {
			ModelUtils.registerMetaInsensitiveModel(Item.getItemFromBlock(OpenBlocks.Blocks.elevatorRotating));
		}

		if (OpenBlocks.Blocks.sky != null) {
			ModelUtils.registerMetaInsensitiveModel(OpenBlocks.Blocks.sky);
			SkyBlockRenderer.INSTANCE.setup();
		}
	}

	@Override
	public void init() {
		MinecraftForge.EVENT_BUS.register(new ClientTickHandler());
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new EntityMiniMe.OwnerChangeHandler());
	}

	@Override
	public void postInit() {
		SoundEventsManager.instance.init();
	}

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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class, new TileEntityGuideRenderer<TileEntityGuide>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBuilderGuide.class, new TileEntityBuilderGuideRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrave.class, new TileEntityGraveRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlag.class, new TileEntityFlagRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TileEntityTankRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophy.class, new TileEntityTrophyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBearTrap.class, SimpleModelTileEntityRenderer.create(new ModelBearTrap(), OpenBlocks.location("textures/models/beartrap.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySprinkler.class, SimpleModelTileEntityRenderer.create(new ModelSprinkler(), OpenBlocks.location("textures/models/sprinkler.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class, new TileEntityCannonRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityImaginary.class, new TileEntityImaginaryRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFan.class, new TileEntityFanRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVillageHighlighter.class, new TileEntityVillageHighlighterRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAutoAnvil.class, SimpleModelTileEntityRenderer.create(new ModelAutoAnvil(), OpenBlocks.location("textures/models/autoanvil.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAutoEnchantmentTable.class, new TileEntityAutoEnchantmentTableRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDonationStation.class, SimpleModelTileEntityRenderer.create(new ModelPiggy(), OpenBlocks.location("textures/models/piggy.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPaintMixer.class, new TileEntityPaintMixerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjector.class, new TileEntityProjectorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySky.class, new TileEntitySkyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityXPShower.class, SimpleModelTileEntityRenderer.create(new ModelXPShower(), OpenBlocks.location("textures/models/xpshower.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGoldenEgg.class, new TileEntityGoldenEggRenderer());

		tempHackRegisterTesrItemRenderers();

		if (OpenBlocks.Blocks.tank != null) {
			MinecraftForge.EVENT_BUS.register(new FluidTextureRegisterListener());
		}

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

		final ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
		final BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

		if (OpenBlocks.Items.paintBrush != null) {
			itemColors.registerItemColorHandler(new ItemPaintBrush.ColorHandler(), OpenBlocks.Items.paintBrush);
		}

		if (OpenBlocks.Items.crayonGlasses != null) {
			itemColors.registerItemColorHandler(new ItemImaginationGlasses.CrayonColorHandler(), OpenBlocks.Items.crayonGlasses);
		}

		if (OpenBlocks.Blocks.paintCan != null) {
			itemColors.registerItemColorHandler(new ItemPaintCan.ItemColorHandler(), OpenBlocks.Blocks.paintCan);
			blockColors.registerBlockColorHandler(new BlockPaintCan.BlockColorHandler(), OpenBlocks.Blocks.paintCan);
		}

		if (OpenBlocks.Blocks.imaginary != null) {
			itemColors.registerItemColorHandler(new ItemImaginary.CrayonColorHandler(), OpenBlocks.Blocks.imaginary);
			MinecraftForge.EVENT_BUS.register(new TileEntityImaginaryRenderer.CacheFlushListener());
		}

		if (OpenBlocks.Items.devNull != null) {
			itemColors.registerItemColorHandler(new ItemDevNull.NestedItemColorHandler(itemColors), OpenBlocks.Items.devNull);
		}

		if (OpenBlocks.Blocks.elevator != null) {
			blockColors.registerBlockColorHandler(new BlockElevator.BlockColorHandler(), OpenBlocks.Blocks.elevator);
			itemColors.registerItemColorHandler(new ItemElevator.ItemColorHandler(), OpenBlocks.Blocks.elevator);
		}

		if (OpenBlocks.Blocks.elevatorRotating != null) {
			blockColors.registerBlockColorHandler(new BlockElevator.BlockColorHandler(), OpenBlocks.Blocks.elevatorRotating);
			itemColors.registerItemColorHandler(new ItemElevator.ItemColorHandler(), OpenBlocks.Blocks.elevatorRotating);
		}

		if (OpenBlocks.Blocks.canvas != null) {
			blockColors.registerBlockColorHandler(new BlockCanvas.InnerBlockColorHandler(blockColors), OpenBlocks.Blocks.canvas);
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

	private interface BlockConsumer {
		public void nom(OpenBlock block);
	}

	// TODO kill it with fire!
	// most of those blocks don't require TESR, but have complex models, that now can be done in data
	private static void visitTempHackTesrBlocks(BlockConsumer consumer) {
		consumer.nom(OpenBlocks.Blocks.autoAnvil);
		consumer.nom(OpenBlocks.Blocks.bearTrap);
		consumer.nom(OpenBlocks.Blocks.cannon);
		consumer.nom(OpenBlocks.Blocks.donationStation);
		consumer.nom(OpenBlocks.Blocks.fan);
		consumer.nom(OpenBlocks.Blocks.flag);
		consumer.nom(OpenBlocks.Blocks.goldenEgg);
		consumer.nom(OpenBlocks.Blocks.grave);
		consumer.nom(OpenBlocks.Blocks.paintMixer);
		consumer.nom(OpenBlocks.Blocks.sprinkler);
		consumer.nom(OpenBlocks.Blocks.villageHighlighter);
		consumer.nom(OpenBlocks.Blocks.xpShower);
	}

	@SuppressWarnings("deprecation")
	private static void tempHackRegisterTesrItemRenderers() {
		visitTempHackTesrBlocks(new BlockConsumer() {
			@Override
			public void nom(OpenBlock block) {
				Item item = Item.getItemFromBlock(block);
				ForgeHooksClient.registerTESRItemStack(item, 0, block.getTileClass());
			}
		});
	}

	private static void tempHackRegisterTesrStateMappers() {
		// TODO differentiate models to get proper particles
		final ModelResourceLocation location = new ModelResourceLocation(OpenBlocks.location("temp"), "dummy");
		visitTempHackTesrBlocks(new BlockConsumer() {
			@Override
			public void nom(OpenBlock block) {
				ImmutableMap.Builder<IBlockState, ModelResourceLocation> statesBuilder = ImmutableMap.builder();
				for (IBlockState state : block.getBlockState().getValidStates())
					statesBuilder.put(state, location);

				final Map<IBlockState, ModelResourceLocation> states = statesBuilder.build();
				ModelLoader.setCustomStateMapper(block, new IStateMapper() {
					@Override
					public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
						return states;
					}
				});
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
