package openblocks;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.DonationUrlManager;
import openblocks.common.MagnetWhitelists;
import openblocks.common.PlayerDeathHandler;
import openblocks.common.block.*;
import openblocks.common.entity.EntityBlock;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.item.*;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.integration.ModuleComputerCraft;
import openblocks.integration.ModuleOpenPeripheral;
import openblocks.network.PacketHandler;
import openblocks.sync.SyncableManager;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid = "OpenBlocks", name = "OpenBlocks", version = "@VERSION@", dependencies = "after:ComputerCraft;after:OpenPeripheral")
@NetworkMod(serverSideRequired = true, clientSideRequired = true, channels = { PacketHandler.CHANNEL_SYNC, PacketHandler.CHANNEL_EVENTS }, packetHandler = PacketHandler.class)
public class OpenBlocks {

	public static final String CHANNEL = "OpenBlocks";

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
		public static BlockXPBottler xpBottler;
		public static BlockVillageHighlighter villageHighlighter;
		public static BlockPath path;
		public static BlockAutoAnvil autoAnvil;
		public static BlockAutoEnchantmentTable autoEnchantmentTable;
		public static BlockXPDrain xpDrain;
		public static BlockBlockBreaker blockBreaker;
		public static BlockBlockPlacer blockPlacer;
		public static BlockItemDropper itemDropper;
		public static BlockRopeLadder ropeLadder;
		public static BlockDonationStation donationStation;
		public static BlockClayStainer clayStainer;
		public static BlockSpecialStainedClay specialStainedClay;
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
		public static ItemSlimalyzer slimalyzer;
		public static ItemFilledBucket filledBucket;
		public static ItemSleepingBag sleepingBag;
	}

	public static class Fluids {
		public static Fluid XPJuice;
		public static Fluid openBlocksXPJuice;
	}

	public static FluidStack XP_FLUID = null;

	public static enum GuiId {
		luggage
	}

	public static final GuiId[] GUIS = GuiId.values();

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
		if (configFile.hasChanged()) {
			Config.readConfig(configFile);
		}
		configFile.save();
		
		OpenBlocks.syncableManager = new SyncableManager();

		Config.register();

		NetworkRegistry.instance().registerGuiHandler(instance, proxy.createGuiHandler());
		if (Config.enableGraves) {
			MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
		}

		if (Config.itemLuggageId > 0) {
			EntityRegistry.registerModEntity(EntityLuggage.class, "Luggage", 702, OpenBlocks.instance, 64, 1, true);
		}
		EntityRegistry.registerModEntity(EntityHangGlider.class, "Hang Glider", 701, OpenBlocks.instance, 64, 1, true);

		if (Config.itemCraneId > 0) {
			EntityRegistry.registerModEntity(EntityMagnet.class, "Magnet", 703, OpenBlocks.instance, 64, 1, true);
			EntityRegistry.registerModEntity(EntityBlock.class, "Block", 704, OpenBlocks.instance, 64, 1, true);
		}

		Fluids.openBlocksXPJuice = new Fluid("xpjuice").setLuminosity(10).setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(Fluids.openBlocksXPJuice);
		Fluids.XPJuice = FluidRegistry.getFluid("xpjuice");
		XP_FLUID = new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1);

		FluidContainerRegistry.registerFluidContainer(Fluids.XPJuice, ItemFilledBucket.BucketMetas.xpbucket.newItemStack(), FluidContainerRegistry.EMPTY_BUCKET);

		OpenBlocks.Items.generic.initRecipes();

		MagnetWhitelists.instance.initTesters();

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.registerAddons();
		if (Loader.isModLoaded(Mods.OPENPERIPHERAL)) ModuleOpenPeripheral.registerAdapters();

	}

	/**
	 * @param evt
	 */
	@EventHandler
	public void init(FMLInitializationEvent evt) {

		proxy.init();

		proxy.registerRenderInformation();
	}

	/**
	 * @param evt
	 */
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.postInit();
	}

	@Mod.EventHandler
	public void processMessage(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage m : event.getMessages()) {
			if (m.isStringMessage() && m.key.equals("donateUrl")) {
				DonationUrlManager.instance().addUrl(m.getSender(), m.getStringValue());
			}
		}
	}

	public static String getModId() {
		return OpenBlocks.class.getAnnotation(Mod.class).modid();
	}
}
