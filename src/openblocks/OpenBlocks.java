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
import openblocks.common.block.BlockTrophy;
import openblocks.common.item.ItemGeneric;
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
		public static BlockTrophy trophy;
	}

	public static class Items {
		public static ItemHangGlider hangGlider;
		public static ItemGeneric generic;
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
		public static int blockTrophyId = 2549;
		public static int itemHangGliderId = 14975;
		public static int itemGenericId = 14976;
		public static int elevatorTravelDistance = 20;
		public static boolean elevatorBlockMustFaceDirection = false;
		public static boolean elevatorIgnoreHalfBlocks = false;
		public static int elevatorMaxBlockPassCount = 4;
		public static int bucketsPerTank = 16;
		public static boolean enableGraves = false;
		public static int ghostSpawnProbability = 0;
		public static boolean tryHookPlayerRenderer = true;
		public static double trophyDropChance = 0.001;
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

		prop = configFile.getBlock("block", "blockTankId", Config.blockTankId, "The id of the tank block");
		Config.blockTankId = prop.getInt();
		
		prop = configFile.getBlock("block", "blockTrophyId", Config.blockTrophyId, "The id of the trophy block");
		Config.blockTrophyId = prop.getInt();

		prop = configFile.getItem("item", "itemHangGliderId", Config.itemHangGliderId, "The id of the hang glider");
		Config.itemHangGliderId = prop.getInt();

		prop = configFile.getItem("item", "itemGenericId", Config.itemGenericId, "The id of the generic item");
		Config.itemGenericId = prop.getInt();

		prop = configFile.get("dropblock", "searchDistance", Config.elevatorTravelDistance, "The range of the drop block");
		Config.elevatorTravelDistance = prop.getInt();

		prop = configFile.get("dropblock", "mustFaceDirection", Config.elevatorBlockMustFaceDirection, "Must the user face the direction they want to travel?");
		Config.elevatorBlockMustFaceDirection = prop.getBoolean(Config.elevatorBlockMustFaceDirection);

		prop = configFile.get("dropblock", "maxPassThrough", Config.elevatorMaxBlockPassCount, "The maximum amount of blocks the elevator can pass through before the teleport fails. -1 disables this");
		Config.elevatorMaxBlockPassCount = prop.getInt();

		if (Config.elevatorMaxBlockPassCount < -1) {
			Config.elevatorMaxBlockPassCount = -1;
		}
		prop.set(Config.elevatorMaxBlockPassCount);

		prop = configFile.get("dropblock", "ignoreHalfBlocks", Config.elevatorIgnoreHalfBlocks, "The elevator will ignore half blocks when counting the blocks it can pass through");
		Config.elevatorIgnoreHalfBlocks = prop.getBoolean(Config.elevatorIgnoreHalfBlocks);

		prop = configFile.get("grave", "ghostProbability", Config.ghostSpawnProbability, "Probabily that a ghost will spawn from breaking a grave, from 0 to 100.");
		Config.ghostSpawnProbability = prop.getInt();

		if (Config.ghostSpawnProbability > 100) Config.ghostSpawnProbability = 100;
		else if (Config.ghostSpawnProbability < 0) Config.ghostSpawnProbability = 0;

		prop.set(Config.ghostSpawnProbability);

		prop = configFile.get("grave", "enableGraves", Config.enableGraves, "Enable graves on player death");
		Config.enableGraves = prop.getBoolean(Config.enableGraves);

		prop = configFile.get("tanks", "bucketsPerTank", Config.bucketsPerTank, "The amount of buckets each tank can hold");
		Config.bucketsPerTank = prop.getInt();

		prop = configFile.get("trophy", "trophyDropChance", Config.trophyDropChance, "The chance (from 0 to 1) of a trophy drop. for example, 0.001 for 1/1000");
		Config.trophyDropChance = prop.getDouble(Config.trophyDropChance);

		prop = configFile.get("hacks", "tryHookPlayerRenderer", Config.tryHookPlayerRenderer, "Allow OpenBlocks to hook the player renderer to apply special effects");
		Config.tryHookPlayerRenderer = prop.getBoolean(Config.tryHookPlayerRenderer);

		configFile.save();

	}

	@Mod.Init
	public void init(FMLInitializationEvent evt) {
		proxy.init();
		proxy.registerRenderInformation();

	}

	public static void onSetBlock() {
		// System.out.println("Set block!");
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
