package jadedladder;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import jadedladder.common.CommonProxy;
import jadedladder.common.block.BlockDrop;
import jadedladder.common.block.BlockGuide;
import jadedladder.common.block.BlockHeal;
import jadedladder.common.block.BlockLadder;

@Mod(modid = "JadedLadder", name = "JadedLadder", version = "@VERSION@")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class JadedLadder {

	@Instance(value = "JadedLadder")
	public static JadedLadder instance;

	@SidedProxy(clientSide = "jadedladder.client.ClientProxy", serverSide = "jadedladder.common.CommonProxy")
	public static CommonProxy proxy;

	public static class Blocks {
		public static BlockLadder ladder;
		public static BlockGuide guide;
		public static BlockDrop drop;
		public static BlockHeal heal;
	}

	public static class Config {
		public static int blockLadderId = 800;
		public static int blockGuideId = 801;
		public static int blockDropId = 802;
		public static int blockHealId = 803;
	}

	public static int renderId;
	
	@Mod.PreInit
	public void preInit(FMLPreInitializationEvent evt) {

		if (Mods.areInstalled("That", "I", "Dont", "Like")) {
			destroyTheWorld();
		}

		Configuration configFile = new Configuration(
				evt.getSuggestedConfigurationFile());

		/* getBlock makes this mod anti-block-id-collision-forge-thingy compliant.. Don't be a redpower :P */
		Property prop = configFile.getBlock("block", "blockLadderId",Config.blockLadderId, "The id of the ladder");
		Config.blockLadderId = prop.getInt();
		
		prop = configFile.getBlock("block", "blockGuideId",Config.blockGuideId, "The id of the guide");
		Config.blockGuideId = prop.getInt();
		
		prop = configFile.getBlock("block", "blockDropId",Config.blockDropId, "The id of the drop block");
		Config.blockDropId = prop.getInt();
		
		prop = configFile.getBlock("block", "blockHealId", Config.blockHealId, "The id of the heal block");
		Config.blockHealId = prop.getInt();
		
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
