package openblocks;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import openblocks.common.PlayerDeathHandler;
import openblocks.common.block.*;
import openblocks.common.entity.EntityGhost;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.item.*;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.network.PacketHandler;
import openblocks.sync.SyncableManager;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid = "OpenBlocks", name = "OpenBlocks", version = "@VERSION@")
@NetworkMod(serverSideRequired = true, clientSideRequired = true, channels = { "OpenBlocks" }, packetHandler = PacketHandler.class)
public class OpenBlocks {

	@Instance(value = "OpenBlocks")
	public static OpenBlocks instance;

	@SidedProxy(clientSide = "openblocks.client.ClientProxy", serverSide = "openblocks.common.ServerProxy")
	public static IProxy proxy;

	public static class Blocks {
		public static BlockLadder ladder;
		public static BlockGuide guide;
		public static BlockElevator elevator;
		public static BlockHeal heal;
		public static BlockLightbox lightbox;
		public static BlockTarget target;
		public static BlockGrave grave;
		public static BlockFlag flag;
		public static BlockTank tank;
		public static BlockTrophy trophy;
		public static BlockBearTrap bearTrap;
		public static BlockSprinkler sprinkler;
		public static BlockCannon cannon;
		public static BlockVacuumHopper vacuumHopper;
		public static BlockSponge sponge;
		public static BlockBigButton bigButton;
		public static BlockImaginary imaginary;
		public static BlockFan fan;
	}

	public static class Items {
		public static ItemHangGlider hangGlider;
		public static ItemGeneric generic;
		public static ItemLuggage luggage;
		public static ItemSonicGlasses sonicGlasses;
		public static ItemImaginationGlasses pencilGlasses;
		public static ItemCrayonGlasses crayonGlasses;
		public static ItemImaginationGlasses technicolorGlasses;
		public static ItemImaginationGlasses seriousGlasses;
		public static ItemCraneControl craneControl;
		public static ItemCraneBackpack craneBackpack;
	}
	
	public static class Fluids {
		public static Fluid XPJuice;
		public static Fluid openBlocksXPJuice;
	}

	public static enum Gui {
		Lightbox,
		Luggage,
		Sprinkler,
		VacuumHopper,
		BigButton
	}

	public static CreativeTabs tabOpenBlocks = new CreativeTabs("tabOpenBlocks") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(ObjectUtils.firstNonNull(OpenBlocks.Blocks.flag, Block.sponge), 1, 0);
		}
	};

	public static int renderId;

	public static SyncableManager syncableManager;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		Log.logger = evt.getModLog();

		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());

		Config.readConfig(configFile);

		configFile.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		Config.register();

		NetworkRegistry.instance().registerGuiHandler(instance, proxy.createGuiHandler());
		if (Config.enableGraves) {
			MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
		}

		if (Config.enableGraves) {
			EntityRegistry.registerModEntity(EntityGhost.class, "Ghost", 700, OpenBlocks.instance, 64, 1, true);
		}
		if (Config.itemLuggageId > 0) {
			EntityRegistry.registerModEntity(EntityLuggage.class, "Luggage", 702, OpenBlocks.instance, 64, 1, true);
		}
		EntityRegistry.registerModEntity(EntityHangGlider.class, "Hang Glider", 701, OpenBlocks.instance, 64, 1, true);

		if (Config.itemCraneId > 0) {
			EntityRegistry.registerModEntity(EntityMagnet.class, "Magnet", 703, OpenBlocks.instance, 64, 1, true);
		}
		
		Fluids.openBlocksXPJuice = new Fluid("xpjuice").setLuminosity(10).setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(Fluids.openBlocksXPJuice);
		Fluids.XPJuice = FluidRegistry.getFluid("xpjuice");

		OpenBlocks.Items.generic.initRecipes();

		proxy.init();

		proxy.registerRenderInformation();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.postInit();
	}

	/*
	 * TODO: These either need amending or depreciating, maybe move it from
	 * CompatibilityUtils to here - NC
	 */

	public static String getResourcesPath() {
		return "/mods/openblocks";
	}

	public static String getLanguagePath() {
		return String.format("%s/languages", getResourcesPath());
	}

	public static String getTexturesPath() {
		return String.format("%s/textures", getResourcesPath());
	}

	public static String getTexturesPath(String path) {
		return String.format("%s/%s", getTexturesPath(), path);
	}

	public static File getBaseDir() {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getFile(".");
	}

	public static File getWorldDir(World world) {
		return proxy.getWorldDir(world);
	}

	public static String getModId() {
		return OpenBlocks.class.getAnnotation(Mod.class).modid();
	}
}
