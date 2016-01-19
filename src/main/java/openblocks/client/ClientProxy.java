package openblocks.client;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import openblocks.Config;
import openblocks.IOpenBlocksProxy;
import openblocks.OpenBlocks;
import openblocks.client.bindings.KeyInputHandler;
import openblocks.client.fx.FXLiquidSpray;
import openblocks.client.model.*;
import openblocks.client.renderer.entity.*;
import openblocks.client.renderer.tileentity.*;
import openblocks.common.entity.*;
import openblocks.common.tileentity.*;
import openblocks.enchantments.flimflams.LoreFlimFlam;
import openmods.config.game.RegisterBlock;
import openmods.entity.EntityBlock;
import openmods.entity.renderer.EntityBlockRenderer;
import openmods.renderer.SimpleModelTileEntityRenderer;

import com.google.common.base.Throwables;

public class ClientProxy implements IOpenBlocksProxy {

	public ClientProxy() {}

	@Override
	public void preInit() {
		new KeyInputHandler().setup();

		if (Config.flimFlamEnchantmentId > 0) {
			MinecraftForge.EVENT_BUS.register(new LoreFlimFlam.DisplayHandler());
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

	@Override
	public void registerRenderInformation() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGuide.class, new TileEntityGuideRenderer<TileEntityGuide>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBuilderGuide.class, new TileEntityBuilderGuideRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTarget.class, SimpleModelTileEntityRenderer.create(new ModelTarget(), OpenBlocks.location("textures/models/target.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGrave.class, new TileEntityGraveRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFlag.class, new TileEntityFlagRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTank.class, new TileEntityTankRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophy.class, new TileEntityTrophyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBearTrap.class, SimpleModelTileEntityRenderer.create(new ModelBearTrap(), OpenBlocks.location("textures/models/beartrap.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySprinkler.class, SimpleModelTileEntityRenderer.create(new ModelSprinkler(), OpenBlocks.location("textures/models/sprinkler.png")));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCannon.class, new TileEntityCannonRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVacuumHopper.class, SimpleModelTileEntityRenderer.create(new ModelVacuumHopper(), OpenBlocks.location("textures/models/vacuumhopper.png")));
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

		// TODO 1.8.9 Tank item rendering

		if (OpenBlocks.Items.luggage != null) {
			// TODO 1.8.9 Luggage item rendering
			RenderingRegistry.registerEntityRenderingHandler(EntityLuggage.class, new IRenderFactory<EntityLuggage>() {
				@Override
				public Render<EntityLuggage> createRenderFor(RenderManager manager) {
					return new EntityLuggageRenderer(manager);
				}
			});
		}

		// TODO 1.8.9 Paint can item rendering

		if (OpenBlocks.Items.hangGlider != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityHangGlider.class, new IRenderFactory<EntityHangGlider>() {

				@Override
				public Render<EntityHangGlider> createRenderFor(RenderManager manager) {
					return new EntityHangGliderRenderer(manager);
				}
			});

			// TODO 1.8.9 hang glider item rendering
			MinecraftForge.EVENT_BUS.register(new GliderPlayerRenderHandler());
		}

		if (OpenBlocks.Items.sonicGlasses != null) {
			MinecraftForge.EVENT_BUS.register(SoundEventsManager.instance);
		}

		if (OpenBlocks.Items.craneBackpack != null) {
			ModelCraneBackpack.instance.init();
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

		// TODO 1.8.9 /dev/null item rendering

		if (OpenBlocks.Items.sleepingBag != null) {
			MinecraftForge.EVENT_BUS.register(new SleepingBagRenderHandler());
		}

		MinecraftForge.EVENT_BUS.register(new EntitySelectionHandler());

		if (OpenBlocks.Items.cartographer != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityCartographer.class, new IRenderFactory<EntityCartographer>() {
				@Override
				public Render<? super EntityCartographer> createRenderFor(RenderManager manager) {
					return new EntityCartographerRenderer(manager);
				}
			});
			EntitySelectionHandler.registerRenderer(EntityCartographer.class, new EntityCartographerRenderer.Selection());
		}

		// TODO 1.8.9 trophy item rendering

		if (OpenBlocks.Items.goldenEye != null) {
			RenderingRegistry.registerEntityRenderingHandler(EntityGoldenEye.class, new IRenderFactory<EntityGoldenEye>() {

				@Override
				public Render<? super EntityGoldenEye> createRenderFor(RenderManager manager) {
					return new RenderSnowball<EntityGoldenEye>(manager, OpenBlocks.Items.goldenEye, Minecraft.getMinecraft().getRenderItem());
				}
			});
		}
	}

	@SuppressWarnings("deprecation")
	private static void tempHackRegisterTesrItemRenderers() {
		for (Field f : OpenBlocks.Blocks.class.getFields()) {
			RegisterBlock ann = f.getAnnotation(RegisterBlock.class);
			if (ann.tileEntity() != null) {
				try {
					Block block = (Block)f.get(null);
					if (block.getRenderType() == 2) {
						Item item = Item.getItemFromBlock(block);
						ForgeHooksClient.registerTESRItemStack(item, 0, ann.tileEntity());
					}
				} catch (Exception e) {
					throw Throwables.propagate(e);
				}
			}
		}
	}

	private static void spawnParticle(EntityFX spray) {
		Minecraft.getMinecraft().effectRenderer.addEffect(spray);
	}

	@Override
	public void spawnLiquidSpray(World worldObj, FluidStack fluid, double x, double y, double z, float scale, float gravity, Vec3 velocity) {
		spawnParticle(new FXLiquidSpray(worldObj, fluid, x, y, z, scale, gravity, velocity));
	}
}
