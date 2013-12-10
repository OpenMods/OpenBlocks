package openblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.*;
import openblocks.common.block.*;
import openblocks.common.entity.*;
import openblocks.common.item.*;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.common.tileentity.*;
import openblocks.events.EventTypes;
import openblocks.integration.ModuleComputerCraft;
import openblocks.integration.ModuleOpenPeripheral;
import openblocks.rubbish.BrickManager;
import openmods.Mods;
import openmods.config.RegisterBlock;
import openmods.config.RegisterItem;
import openmods.entity.EntityBlock;
import openmods.item.ItemGeneric;
import openmods.utils.EnchantmentUtils;

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
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "OpenBlocks", name = "OpenBlocks", version = "@VERSION@", dependencies = "required-after:OpenMods;after:OpenPeripheral")
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenBlocks {

	@Instance(value = "OpenBlocks")
	public static OpenBlocks instance;

	@SidedProxy(clientSide = "openblocks.client.ClientProxy", serverSide = "openblocks.common.ServerProxy")
	public static IOpenBlocksProxy proxy;

	public static class Blocks {
		@RegisterBlock(name = "ladder")
		public static BlockLadder ladder;

		@RegisterBlock(name = "guide", tileEntity = TileEntityGuide.class)
		public static BlockGuide guide;

		@RegisterBlock(name = "elevator", tileEntity = TileEntityElevator.class)
		public static BlockElevator elevator;

		@RegisterBlock(name = "heal", tileEntity = TileEntityHealBlock.class)
		public static BlockHeal heal;

		@RegisterBlock(name = "target", tileEntity = TileEntityTarget.class)
		public static BlockTarget target;

		@RegisterBlock(name = "grave", tileEntity = TileEntityGrave.class)
		public static BlockGrave grave;

		@RegisterBlock(name = "flag", tileEntity = TileEntityFlag.class, itemBlock = ItemFlagBlock.class)
		public static BlockFlag flag;

		@RegisterBlock(name = "tank", tileEntity = TileEntityTank.class, itemBlock = ItemTankBlock.class)
		public static BlockTank tank;

		@RegisterBlock(name = "trophy", tileEntity = TileEntityTrophy.class, itemBlock = ItemTrophyBlock.class)
		public static BlockTrophy trophy;

		@RegisterBlock(name = "beartrap", tileEntity = TileEntityBearTrap.class)
		public static BlockBearTrap bearTrap;

		@RegisterBlock(name = "sprinkler", tileEntity = TileEntitySprinkler.class)
		public static BlockSprinkler sprinkler;

		@RegisterBlock(name = "cannon", tileEntity = TileEntityCannon.class)
		public static BlockCannon cannon;

		@RegisterBlock(name = "vacuumhopper", tileEntity = TileEntityVacuumHopper.class)
		public static BlockVacuumHopper vacuumHopper;

		@RegisterBlock(name = "sponge", tileEntity = TileEntitySponge.class)
		public static BlockSponge sponge;

		@RegisterBlock(name = "bigbutton", tileEntity = TileEntityBigButton.class)
		public static BlockBigButton bigButton;

		@RegisterBlock(name = "imaginary", tileEntity = TileEntityImaginary.class, itemBlock = ItemImaginary.class)
		public static BlockImaginary imaginary;

		@RegisterBlock(name = "fan", tileEntity = TileEntityFan.class)
		public static BlockFan fan;

		@RegisterBlock(name = "xpbottler", tileEntity = TileEntityXPBottler.class)
		public static BlockXPBottler xpBottler;

		@RegisterBlock(name = "village_highlighter", tileEntity = TileEntityVillageHighlighter.class)
		public static BlockVillageHighlighter villageHighlighter;

		@RegisterBlock(name = "path")
		public static BlockPath path;

		@RegisterBlock(name = "autoanvil", tileEntity = TileEntityAutoAnvil.class)
		public static BlockAutoAnvil autoAnvil;

		@RegisterBlock(name = "autoenchantmenttable", tileEntity = TileEntityAutoEnchantmentTable.class)
		public static BlockAutoEnchantmentTable autoEnchantmentTable;

		@RegisterBlock(name = "xpdrain", tileEntity = TileEntityXPDrain.class)
		public static BlockXPDrain xpDrain;

		@RegisterBlock(name = "blockbreaker", tileEntity = TileEntityBlockBreaker.class)
		public static BlockBlockBreaker blockBreaker;

		@RegisterBlock(name = "blockPlacer", tileEntity = TileEntityBlockPlacer.class)
		public static BlockBlockPlacer blockPlacer;

		@RegisterBlock(name = "itemDropper", tileEntity = TileEntityItemDropper.class)
		public static BlockItemDropper itemDropper;

		@RegisterBlock(name = "ropeladder", tileEntity = TileEntityRopeLadder.class)
		public static BlockRopeLadder ropeLadder;

		@RegisterBlock(name = "donationStation", tileEntity = TileEntityDonationStation.class)
		public static BlockDonationStation donationStation;

		@RegisterBlock(name = "paintmixer", tileEntity = TileEntityPaintMixer.class)
		public static BlockPaintMixer paintMixer;

		@RegisterBlock(name = "canvas", tileEntity = TileEntityCanvas.class)
		public static BlockCanvas canvas;

		@RegisterBlock(name = "oreCrusher", tileEntity = TileEntityOreCrusher.class)
		public static BlockMachineOreCrusher machineOreCrusher;

		@RegisterBlock(name = "paintcan", tileEntity = TileEntityPaintCan.class)
		public static BlockPaintCan paintCan;

		@RegisterBlock(name = "canvasglass", tileEntity = TileEntityCanvas.class)
		public static BlockCanvasGlass canvasGlass;

		@RegisterBlock(name = "projector", tileEntity = TileEntityProjector.class)
		public static BlockProjector projector;

		@RegisterBlock(name = "drawingtable", tileEntity = TileEntityDrawingTable.class)
		public static BlockDrawingTable drawingTable;
	}

	public static class Items {
		// uhh, unlocalized names are messy...
		@RegisterItem(name = "hangglider")
		public static ItemHangGlider hangGlider;

		@RegisterItem(name = "generic")
		public static ItemGeneric generic;

		@RegisterItem(name = "luggage")
		public static ItemLuggage luggage;

		@RegisterItem(name = "sonicglasses")
		public static ItemSonicGlasses sonicGlasses;

		@RegisterItem(name = "pencilGlasses", unlocalizedName = "glasses.pencil")
		public static ItemImaginationGlasses pencilGlasses;

		@RegisterItem(name = "crayonGlasses", unlocalizedName = "glasses.crayon")
		public static ItemCrayonGlasses crayonGlasses;

		@RegisterItem(name = "technicolorGlasses", unlocalizedName = "glasses.technicolor")
		public static ItemImaginationGlasses technicolorGlasses;

		@RegisterItem(name = "seriousGlasses", unlocalizedName = "glasses.admin")
		public static ItemImaginationGlasses seriousGlasses;

		@RegisterItem(name = "craneControl", unlocalizedName = "crane_control")
		public static ItemCraneControl craneControl;

		@RegisterItem(name = "craneBackpack", unlocalizedName = "crane_backpack")
		public static ItemCraneBackpack craneBackpack;

		@RegisterItem(name = "slimalyzer")
		public static ItemSlimalyzer slimalyzer;

		@RegisterItem(name = "filledbucket")
		public static ItemFilledBucket filledBucket;

		@RegisterItem(name = "sleepingBag", unlocalizedName = "sleepingbag")
		public static ItemSleepingBag sleepingBag;

		@RegisterItem(name = "paintBrush", unlocalizedName = "paintbrush")
		public static ItemPaintBrush paintBrush;

		@RegisterItem(name = "stencil")
		public static ItemStencil stencil;

		@RegisterItem(name = "squeegee")
		public static ItemSqueegee squeegee;

		@RegisterItem(name = "heightMap", unlocalizedName = "height_map")
		public static ItemHeightMap heightMap;

		@RegisterItem(name = "emptyMap", unlocalizedName = "empty_map")
		public static ItemEmptyMap emptyMap;

		@RegisterItem(name = "cartographer")
		public static ItemCartographer cartographer;

		@RegisterItem(name = "tastyClay", unlocalizedName = "tasty_clay")
		public static ItemTastyClay tastyClay;

		@RegisterItem(name = "goldenEye", unlocalizedName = "golden_eye")
		public static ItemGoldenEye goldenEye;
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

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@SideOnly(Side.CLIENT)
		public void displayAllReleventItems(List result) {
			super.displayAllReleventItems(result);
			if (explosiveEnch != null) EnchantmentUtils.addAllBooks(explosiveEnch, result);
		}

	};

	public static int renderId;

	public static final Achievement brickAchievement = new Achievement(70997, "openblocks.droppedBrick", 13, 13, Item.brick, null).registerAchievement();

	public static final StatBase brickStat = (new StatBasic(70998, "stat.openblocks.bricksDropped")).registerStat();

	public static Enchantment explosiveEnch;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		EventTypes.registerTypes();
		Configuration configFile = new Configuration(evt.getSuggestedConfigurationFile());
		Config.readConfig(configFile);
		if (configFile.hasChanged()) configFile.save();
		Config.register();

		NetworkRegistry.instance().registerGuiHandler(instance, proxy.createGuiHandler());
		if (Config.enableGraves) {
			MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
		}

		MinecraftForge.EVENT_BUS.register(new TileEntityEventHandler());

		if (Config.itemLuggageId > 0) {
			EntityRegistry.registerModEntity(EntityLuggage.class, "Luggage", 702, OpenBlocks.instance, 64, 1, true);
		}

		EntityRegistry.registerModEntity(EntityHangGlider.class, "Hang Glider", 701, OpenBlocks.instance, 64, 1, true);

		if (Config.itemCraneId > 0) {
			EntityRegistry.registerModEntity(EntityMagnet.class, "Magnet", 703, OpenBlocks.instance, 64, 1, true);
			EntityRegistry.registerModEntity(EntityBlock.class, "Block", 704, OpenBlocks.instance, 64, 1, true);
		}

		if (Config.itemCartographerId > 0) {
			EntityRegistry.registerModEntity(EntityCartographer.class, "Cartographer", 705, OpenBlocks.instance, 64, 8, true);
		}

		EntityRegistry.registerModEntity(EntityItemProjectile.class, "EntityItemProjectile", 706, OpenBlocks.instance, 64, 1, true);

		if (Config.itemGoldenEyeId > 0) {
			EntityRegistry.registerModEntity(EntityGoldenEye.class, "GoldenEye", 707, OpenBlocks.instance, 64, 8, true);
			MinecraftForge.EVENT_BUS.register(StructureRegistry.instance);
		}

		Fluids.openBlocksXPJuice = new Fluid("xpjuice").setLuminosity(10).setDensity(800).setViscosity(1500);
		FluidRegistry.registerFluid(Fluids.openBlocksXPJuice);
		Fluids.XPJuice = FluidRegistry.getFluid("xpjuice");
		XP_FLUID = new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1);

		FluidContainerRegistry.registerFluidContainer(Fluids.XPJuice, MetasBucket.xpbucket.newItemStack(), FluidContainerRegistry.EMPTY_BUCKET);

		OpenBlocks.Items.generic.initRecipes();

		MagnetWhitelists.instance.initTesters();

		MinecraftForge.EVENT_BUS.register(MapDataManager.instance);

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.registerAddons();
		if (Loader.isModLoaded(Mods.OPENPERIPHERALCORE)) ModuleOpenPeripheral.registerAdapters();

		if (!Config.soSerious) {
			MinecraftForge.EVENT_BUS.register(new BrickManager());
		}

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		TickRegistry.registerTickHandler(new ServerTickHandler(), Side.SERVER);
		proxy.init();
		proxy.registerRenderInformation();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.postInit();
	}

	@Mod.EventHandler
	public void processMessage(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage m : event.getMessages()) {
			if (m.isStringMessage() && m.key.equalsIgnoreCase("donateUrl")) {
				DonationUrlManager.instance().addUrl(m.getSender(), m.getStringValue());
			}
		}
	}

	public static String getModId() {
		return OpenBlocks.class.getAnnotation(Mod.class).modid();
	}
}
