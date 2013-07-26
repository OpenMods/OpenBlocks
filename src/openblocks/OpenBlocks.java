package openblocks;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import openblocks.common.CommonProxy;
import openblocks.common.block.BlockDrop;
import openblocks.common.block.BlockGrave;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockHeal;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockLightbox;
import openblocks.common.block.BlockTarget;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "OpenBlocks", name = "OpenBlocks", version = "@VERSION@")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenBlocks {

	@Instance(value = "OpenBlocks")
	public static OpenBlocks instance;

	@SidedProxy(clientSide = "openblocks.client.ClientProxy", serverSide = "openblocks.common.CommonProxy")
	public static CommonProxy proxy;

	public static class Blocks {
		public static BlockLadder ladder;
		public static BlockGuide guide;
		public static BlockDrop drop;
		public static BlockHeal heal;
		public static BlockLightbox lightbox;
		public static BlockTarget target;
		public static BlockGrave grave;
	}

	public static class Config {
		public static int blockLadderId = 800;
		public static int blockGuideId = 801;
		public static int blockDropId = 802;
		public static int blockHealId = 803;
		public static int blockLightboxId = 804;
		public static int blockTargetId = 805;
		public static int blockGraveId = 806;
		public static int dropBlockSearchDistance = 30;
		public static boolean dropBlockMustFaceDirection = false;
	}

	public static enum Gui {
		Lightbox
	}

	public static int renderId;

	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent evt) {

		if (Mods.areInstalled("That", "I", "Dont", "Like")) {
			destroyTheWorld();
		}

		Configuration configFile = new Configuration(
				evt.getSuggestedConfigurationFile());

		/*
		 * getBlock makes this mod anti-block-id-collision-forge-thingy
		 * compliant.. Don't be a redpower :P
		 */
		Property prop = configFile.getBlock("block", "blockLadderId",
				Config.blockLadderId, "The id of the ladder");
		Config.blockLadderId = prop.getInt();

		prop = configFile.getBlock("block", "blockGuideId",
				Config.blockGuideId, "The id of the guide");
		Config.blockGuideId = prop.getInt();

		prop = configFile.getBlock("block", "blockDropId", Config.blockDropId,
				"The id of the drop block");
		Config.blockDropId = prop.getInt();

		prop = configFile.getBlock("block", "blockHealId", Config.blockHealId,
				"The id of the heal block");
		Config.blockHealId = prop.getInt();

		prop = configFile.getBlock("block", "blockLightboxId",
				Config.blockLightboxId, "The id of the lightbox block");
		Config.blockLightboxId = prop.getInt();

		prop = configFile.getBlock("block", "blockTargetId",
				Config.blockTargetId, "The id of the target block");
		Config.blockTargetId = prop.getInt();
		
		prop = configFile.getBlock("block", "blockGraveId", 
				Config.blockGraveId, "The id of the grave block");
		Config.blockGraveId = prop.getInt();
	
		prop = configFile.get("dropblock", "searchDistance", Config.dropBlockSearchDistance, "The range of the drop block");
		Config.dropBlockSearchDistance = prop.getInt();

		prop = configFile.get("dropblock", "mustFaceDirection", Config.dropBlockMustFaceDirection, "Must the user face the direction they want to travel?");
		Config.dropBlockMustFaceDirection = prop.getBoolean(Config.dropBlockMustFaceDirection);

		configFile.save();

	}

	@Mod.Init
	public void init(FMLInitializationEvent evt) {
		proxy.init();
		proxy.registerRenderInformation();

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

}
