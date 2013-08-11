package openblocks;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import openblocks.common.CommonProxy;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockFlag;
import openblocks.common.block.BlockGrave;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockHeal;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockLightbox;
import openblocks.common.block.BlockTank;
import openblocks.common.block.BlockTarget;
import openblocks.common.item.ItemHangGlider;
import openblocks.network.PacketHandler;
import openblocks.sync.SyncableManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "OpenBlocks", name = "OpenBlocks", version = "@VERSION@")
@NetworkMod(serverSideRequired = true, clientSideRequired = true, channels = { "OpenBlocks" }, packetHandler = PacketHandler.class)
public class OpenBlocks {

	@Instance(value = "OpenBlocks")
	public static OpenBlocks instance;

	@SidedProxy(clientSide = "openblocks.client.ClientProxy", serverSide = "openblocks.common.CommonProxy")
	public static CommonProxy proxy;

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
	}
	
	public static class Items {
		public static ItemHangGlider hangGlider;
	}

	public static class Config {
		public static int blockLadderId = 2540;
		public static int blockGuideId = 2541;
		public static int blockElevatorId = 2542;
		public static int blockHealId = 2543;
		public static int blockLightboxId = 2544;
		public static int blockTargetId = 2545;
		public static int blockGraveId = 2546;
		public static int blockFlagId = 2547;
		public static int blockTankId = 2548;
		public static int itemHangGliderId = 3620;
		public static int elevatorTravelDistance = 20;
		public static int bucketsPerTank = 16;
		public static boolean enableGraves = true;
		public static boolean elevatorBlockMustFaceDirection = false;
		public static int ghostSpawnProbability = 100;
		public static boolean tryHookPlayerRenderer = true;
	}

	public static enum Gui {
		Lightbox
	}

	public static CreativeTabs tabOpenBlocks = new CreativeTabs("tabOpenBlocks") {
		public ItemStack getIconItemStack() {
			return new ItemStack(OpenBlocks.Blocks.flag, 1, 0);
		}
	};

	public static int renderId;

	public static SyncableManager syncableManager;

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent evt) {

		Log.init();

		if (Mods.areInstalled("That", "I", "Dont", "Like")) {
			destroyTheWorld();
		}

		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());

		/*
		 * getBlock makes this mod anti-block-id-collision-forge-thingy
		 * compliant.. Don't be a redpower :P
		 */
		Property prop = configFile.getBlock("block", "blockLadderId", Config.blockLadderId, "The id of the ladder");
		Config.blockLadderId = prop.getInt();

		prop = configFile.getBlock("block", "blockGuideId", Config.blockGuideId, "The id of the guide");
		Config.blockGuideId = prop.getInt();

		prop = configFile.getBlock("block", "blockDropId", Config.blockElevatorId, "The id of the drop block");
		Config.blockElevatorId = prop.getInt();

		prop = configFile.getBlock("block", "blockHealId", Config.blockHealId, "The id of the heal block");
		Config.blockHealId = prop.getInt();

		prop = configFile.getBlock("block", "blockLightboxId", Config.blockLightboxId, "The id of the lightbox block");
		Config.blockLightboxId = prop.getInt();

		prop = configFile.getBlock("block", "blockTargetId", Config.blockTargetId, "The id of the target block");
		Config.blockTargetId = prop.getInt();

		prop = configFile.getBlock("block", "blockGraveId", Config.blockGraveId, "The id of the grave block");
		Config.blockGraveId = prop.getInt();

		prop = configFile.getBlock("block", "blockFlagId", Config.blockFlagId, "The id of the flag block");
		Config.blockFlagId = prop.getInt();

		prop = configFile.get("dropblock", "searchDistance", Config.elevatorTravelDistance, "The range of the drop block");
		Config.elevatorTravelDistance = prop.getInt();

		prop = configFile.get("dropblock", "mustFaceDirection", Config.elevatorBlockMustFaceDirection, "Must the user face the direction they want to travel?");
		Config.elevatorBlockMustFaceDirection = prop.getBoolean(Config.elevatorBlockMustFaceDirection);

		prop = configFile.get("grave", "ghostProbability", Config.ghostSpawnProbability, "Probabily that a ghost will spawn from breaking a grave, from 0 to 100.");
		Config.ghostSpawnProbability = prop.getInt();

		prop = configFile.get("grave", "enableGraves", Config.enableGraves, "Enable graves on player death");
		Config.enableGraves = prop.getBoolean(Config.enableGraves);
		
		prop = configFile.get("tanks", "bucketsPerTank", Config.bucketsPerTank, "The amount of buckets each tank can hold");
		Config.bucketsPerTank = prop.getInt();


		prop = configFile.get("hacks", "tryHookPlayerRenderer", Config.tryHookPlayerRenderer, "Allow OpenBlocks to hook the player renderer to apply special effects");
		Config.tryHookPlayerRenderer = prop.getBoolean(Config.tryHookPlayerRenderer);

		if (Config.ghostSpawnProbability > 100) Config.ghostSpawnProbability = 100;
		else if (Config.ghostSpawnProbability < 0) Config.ghostSpawnProbability = 0;

		configFile.save();

	}

	@Mod.Init
	public void init(FMLInitializationEvent evt) {
		proxy.init();
		proxy.registerRenderInformation();

	}

	public static void onSetBlock() {
		//System.out.println("Set block!");
	}

	public void destroyTheWorld() {
		boolean notReally;
		boolean stopBeingDicksEveryone;
	}

	public static class Mods {
		public static boolean areInstalled(String... mods) {
			return false;
		}
	}

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
}
