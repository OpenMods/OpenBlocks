package openblocks;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import openblocks.common.CommonProxy;
import openblocks.common.block.BlockBearTrap;
import openblocks.common.block.BlockBigButton;
import openblocks.common.block.BlockCannon;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockFlag;
import openblocks.common.block.BlockGrave;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockHeal;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockLightbox;
import openblocks.common.block.BlockSponge;
import openblocks.common.block.BlockSprinkler;
import openblocks.common.block.BlockTank;
import openblocks.common.block.BlockTarget;
import openblocks.common.block.BlockTrophy;
import openblocks.common.block.BlockVacuumHopper;
import openblocks.common.item.ItemGeneric;
import openblocks.common.item.ItemHangGlider;
import openblocks.common.item.ItemLuggage;
import openblocks.common.item.ItemSonicGlasses;
import openblocks.network.PacketHandler;
import openblocks.sync.SyncableManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
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
		public static BlockBearTrap bearTrap;
		public static BlockSprinkler sprinkler;
		public static BlockCannon cannon;
		public static BlockVacuumHopper vacuumHopper;
		public static BlockSponge sponge;
		public static BlockBigButton bigButton;
	}

	public static class Items {
		public static ItemHangGlider hangGlider;
		public static ItemGeneric generic;
		public static ItemLuggage luggage;
		public static ItemSonicGlasses sonicGlasses;
	}

	public static class Config {
		public static boolean failIdsQuietly = true;
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
		public static int blockBearTrapId = 2550;
		public static int blockSprinklerId = 2551;
		public static int blockCannonId = 2552;
		public static int blockVacuumHopperId = 2553;
		public static int blockSpongeId = 2554;
		public static int blockBigButton = 2555;
		public static int itemHangGliderId = 14975;
		public static int itemGenericId = 14976;
		public static int itemLuggageId = 14977;
		public static int itemSonicGlassesId = 14978;
		public static int elevatorTravelDistance = 20;
		public static boolean elevatorBlockMustFaceDirection = false;
		public static boolean elevatorIgnoreHalfBlocks = false;
		public static int elevatorMaxBlockPassCount = 4;
		public static int bucketsPerTank = 16;
		public static boolean enableGraves = false;
		public static int ghostSpawnProbability = 0;
		public static boolean tryHookPlayerRenderer = true;
		public static double trophyDropChance = 0.001;
		public static boolean irregularBlocksArePassable = false;
		public static boolean tanksEmitLight = true;
		public static boolean tanksAreTransparent = true;
		public static boolean tanksHaveDynamicTransparency = true;
		public static int sprinklerFertilizeChance = 500;
		public static int sprinklerBonemealFertizizeChance = 200;
		public static int sprinklerEffectiveRange = 4;
		public static double sonicGlassesOpacity = 0.95;
		public static boolean sonicGlassesUseTexture = true;
	}

	public static enum Gui {
		Lightbox, Luggage, Sprinkler, VacuumHopper, BigButton
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

		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());

		Property prop = configFile.get("openblocks", "failIdsQuietly", Config.failIdsQuietly, "If true, OpenBlocks will not throw an error when a block cannot be loaded due to ID conflict.");
		Config.failIdsQuietly = prop.getBoolean(Config.failIdsQuietly);
		/*
		 * getBlock makes this mod anti-block-id-collision-forge-thingy
		 * compliant.. Don't be a redpower :P
		 */
		prop = configFile.getBlock("block", "blockLadderId", Config.blockLadderId, "The id of the ladder");
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

		prop = configFile.getBlock("block", "blockSprinklerId", Config.blockSprinklerId, "The id of the sprinkler block");
		Config.blockSprinklerId = prop.getInt();

		prop = configFile.getBlock("block", "blockTrophyId", Config.blockTrophyId, "The id of the trophy block");
		Config.blockTrophyId = prop.getInt();

		prop = configFile.getItem("item", "itemHangGliderId", Config.itemHangGliderId, "The id of the hang glider");
		Config.itemHangGliderId = prop.getInt();

		prop = configFile.getItem("item", "itemGenericId", Config.itemGenericId, "The id of the generic item");
		Config.itemGenericId = prop.getInt();

		prop = configFile.getItem("item", "itemLuggageId", Config.itemLuggageId, "The id of the luggage item");
		Config.itemLuggageId = prop.getInt();
		
		prop = configFile.getItem("item", "itemSonicGlassesId", Config.itemSonicGlassesId, "The id of the sonic glasses item item");
		Config.itemSonicGlassesId = prop.getInt();

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

		prop = configFile.get("dropblock", "irregularBlocksArePassable", Config.irregularBlocksArePassable, "The elevator will try to pass through blocks that have custom collision boxes");
		Config.irregularBlocksArePassable = prop.getBoolean(Config.irregularBlocksArePassable);

		prop = configFile.get("grave", "ghostProbability", Config.ghostSpawnProbability, "Probabily that a ghost will spawn from breaking a grave, from 0 to 100.");
		Config.ghostSpawnProbability = prop.getInt();

		if (Config.ghostSpawnProbability > 100) Config.ghostSpawnProbability = 100;
		else if (Config.ghostSpawnProbability < 0) Config.ghostSpawnProbability = 0;

		prop.set(Config.ghostSpawnProbability);

		prop = configFile.get("grave", "enableGraves", Config.enableGraves, "Enable graves on player death");
		Config.enableGraves = prop.getBoolean(Config.enableGraves);

		prop = configFile.get("tanks", "bucketsPerTank", Config.bucketsPerTank, "The amount of buckets each tank can hold");
		Config.bucketsPerTank = prop.getInt(Config.bucketsPerTank);

		prop = configFile.get("tanks", "emitLight", Config.tanksEmitLight, "Tanks will emit light when they contain a liquid that glows (eg. lava)");
		Config.tanksEmitLight = prop.getBoolean(Config.tanksEmitLight);

		prop = configFile.get("tanks", "transparent", Config.tanksAreTransparent, "Tanks will pass light");
		Config.tanksAreTransparent = prop.getBoolean(Config.tanksAreTransparent);

		prop = configFile.get("tanks", "dynamicTransparency", Config.tanksHaveDynamicTransparency, "The tank opacity changes with the amount of liquid");
		Config.tanksHaveDynamicTransparency = prop.getBoolean(Config.tanksHaveDynamicTransparency);

		prop = configFile.get("trophy", "trophyDropChance", Config.trophyDropChance, "The chance (from 0 to 1) of a trophy drop. for example, 0.001 for 1/1000");
		Config.trophyDropChance = prop.getDouble(Config.trophyDropChance);
		
		prop = configFile.get("sprinkler", "fertilizeChance", Config.sprinklerFertilizeChance, "1/chance that crops will be fertilized without bonemeal");
		Config.sprinklerFertilizeChance = prop.getInt(Config.sprinklerFertilizeChance);
		
		prop = configFile.get("sprinkler", "bonemealFertilizeChance", Config.sprinklerBonemealFertizizeChance, "1/chance that crops will be fertilized with bonemeal");
		Config.sprinklerBonemealFertizizeChance = prop.getInt(Config.sprinklerBonemealFertizizeChance);
		
		prop = configFile.get("sprinkler", "effectiveRange", Config.sprinklerEffectiveRange, "The range in each cardinal direction that crops will be affected.");
		Config.sprinklerEffectiveRange = prop.getInt(Config.sprinklerEffectiveRange);

		prop = configFile.get("hacks", "tryHookPlayerRenderer", Config.tryHookPlayerRenderer, "Allow OpenBlocks to hook the player renderer to apply special effects");
		Config.tryHookPlayerRenderer = prop.getBoolean(Config.tryHookPlayerRenderer);
		
		prop = configFile.get("glasses", "opacity", Config.sonicGlassesOpacity, "0.0 - no visible change to world, 1.0 - world fully obscured");
		Config.sonicGlassesOpacity = prop.getDouble(Config.sonicGlassesOpacity);

		prop = configFile.get("glasses", "useTexture", Config.sonicGlassesUseTexture, "Use texture for obscuring world");
		Config.sonicGlassesUseTexture = prop.getBoolean(Config.sonicGlassesUseTexture);
		
		configFile.save();
	}

	@Mod.Init
	public void init(FMLInitializationEvent evt) {
		proxy.init();
		proxy.registerRenderInformation();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.postInit();
	}

	public static void onSetBlock() {
		// System.out.println("Set block!");
	}
	
	/* 
	 * TODO: These either need amending or depreciating, maybe move it from CompatibilityUtils to here 
	 * - NC
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
}
