package openblocks;

import java.io.File;
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
import net.minecraftforge.fluids.*;
import openblocks.api.FlimFlamRegistry;
import openblocks.client.radio.RadioManager;
import openblocks.common.*;
import openblocks.common.block.*;
import openblocks.common.entity.*;
import openblocks.common.item.*;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.common.tileentity.*;
import openblocks.enchantments.flimflams.*;
import openblocks.events.EventTypes;
import openblocks.integration.ModuleComputerCraft;
import openblocks.integration.ModuleOpenPeripheral;
import openblocks.rubbish.BrickManager;
import openblocks.utils.ChangelogBuilder;
import openmods.Mods;
import openmods.OpenMods;
import openmods.config.ConfigProcessing;
import openmods.config.RegisterBlock;
import openmods.config.RegisterItem;
import openmods.entity.EntityBlock;
import openmods.item.ItemGeneric;
import openmods.utils.EnchantmentUtils;
import openmods.utils.ReflectionHelper;

import org.apache.commons.lang3.ObjectUtils;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDENCIES)
@NetworkMod(serverSideRequired = true, clientSideRequired = true)
public class OpenBlocks {

	private static final int ENTITY_HANGGLIDER_ID = 701;
	private static final int ENTITY_LUGGAGE_ID = 702;
	private static final int ENTITY_MAGNET_ID = 703;
	private static final int ENTITY_BLOCK_ID = 704;
	private static final int ENTITY_CARTOGRAPHER_ID = 705;
	private static final int ENTITY_CANON_ITEM_ID = 706;
	private static final int ENTITY_GOLDEN_EYE_ID = 707;
	private static final int ENTITY_MAGNET_PLAYER_ID = 708;

	@Instance(value = ModInfo.ID)
	public static OpenBlocks instance;

	@SidedProxy(clientSide = ModInfo.PROXY_CLIENT, serverSide = ModInfo.PROXY_SERVER)
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

		@RegisterBlock(name = "paintcan", tileEntity = TileEntityPaintCan.class, itemBlock = ItemPaintCan.class)
		public static BlockPaintCan paintCan;

		@RegisterBlock(name = "canvasglass", tileEntity = TileEntityCanvas.class)
		public static BlockCanvasGlass canvasGlass;

		@RegisterBlock(name = "projector", tileEntity = TileEntityProjector.class)
		public static BlockProjector projector;

		@RegisterBlock(name = "drawingtable", tileEntity = TileEntityDrawingTable.class)
		public static BlockDrawingTable drawingTable;

		@RegisterBlock(name = "radio", tileEntity = TileEntityRadio.class)
		public static BlockRadio radio;

		@RegisterBlock(name = "sky", tileEntity = TileEntitySky.class, itemBlock = ItemSkyBlock.class)
		public static BlockSky sky;
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

		@RegisterItem(name = "genericUnstackable")
		public static ItemOBGenericUnstackable genericUnstackable;

		@RegisterItem(name = "cursor")
		public static ItemCursor cursor;

		@RegisterItem(name = "tunedCrystal", unlocalizedName = "tuned_crystal")
		public static ItemTunedCrystal tunedCrystal;

		@RegisterItem(name = "infoBook", unlocalizedName = "info_book")
		public static ItemInfoBook infoBook;

		@RegisterItem(name = "wallpaper")
		public static ItemWallpaper wallpaper;
	}

	public static class ClassReferences {
		public static Class<?> flansmodsEntityBullet;
	}

	public static class Fluids {
		public static Fluid XPJuice;
		public static Fluid openBlocksXPJuice;
	}

	public static class Enchantments {
		public static Enchantment explosive;
		public static Enchantment lastStand;
		public static Enchantment flimFlam;
	}

	public static FluidStack XP_FLUID = null;

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
			if (Enchantments.explosive != null) EnchantmentUtils.addAllBooks(Enchantments.explosive, result);
			if (Enchantments.lastStand != null) EnchantmentUtils.addAllBooks(Enchantments.lastStand, result);
			if (Enchantments.flimFlam != null) EnchantmentUtils.addAllBooks(Enchantments.flimFlam, result);
		}

	};

	public static int renderId;

	public static final Achievement brickAchievement = new Achievement(70997, "openblocks.droppedBrick", 13, 13, Item.brick, null).registerAchievement();

	public static final StatBase brickStat = (new StatBasic(70998, "stat.openblocks.bricksDropped")).registerStat();

	public static ItemStack changeLog;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		EventTypes.registerTypes();
		final File configFile = evt.getSuggestedConfigurationFile();
		Configuration config = new Configuration(configFile);
		ConfigProcessing.processAnnotations(configFile, "OpenBlocks", config, Config.class);

		if (config.hasChanged()) config.save();
		Config.register();

		NetworkRegistry.instance().registerGuiHandler(instance, OpenMods.proxy.wrapHandler(new OpenBlocksGuiHandler()));

		if (Config.blockGraveId > 0) {
			MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
		}

		if (Config.itemCursorId > 0) {
			MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
		}

		if (Config.itemLuggageId > 0) {
			EntityRegistry.registerModEntity(EntityLuggage.class, "Luggage", ENTITY_LUGGAGE_ID, OpenBlocks.instance, 64, 1, true);
		}

		EntityRegistry.registerModEntity(EntityHangGlider.class, "Hang Glider", ENTITY_HANGGLIDER_ID, OpenBlocks.instance, 64, 1, true);

		if (Config.itemCraneId > 0) {
			EntityRegistry.registerModEntity(EntityMagnet.class, "Magnet", ENTITY_MAGNET_ID, OpenBlocks.instance, 64, 1, true);
			EntityRegistry.registerModEntity(EntityBlock.class, "Block", ENTITY_BLOCK_ID, OpenBlocks.instance, 64, 1, true);
			EntityRegistry.registerModEntity(EntityMagnet.PlayerBound.class, "Player-Magnet", ENTITY_MAGNET_PLAYER_ID, OpenBlocks.instance, 64, 1, true);
		}

		if (Config.itemCartographerId > 0) {
			EntityRegistry.registerModEntity(EntityCartographer.class, "Cartographer", ENTITY_CARTOGRAPHER_ID, OpenBlocks.instance, 64, 8, true);
		}

		EntityRegistry.registerModEntity(EntityItemProjectile.class, "EntityItemProjectile", ENTITY_CANON_ITEM_ID, OpenBlocks.instance, 64, 1, true);

		if (Config.itemGoldenEyeId > 0) {
			EntityRegistry.registerModEntity(EntityGoldenEye.class, "GoldenEye", ENTITY_GOLDEN_EYE_ID, OpenBlocks.instance, 64, 8, true);
			MinecraftForge.EVENT_BUS.register(StructureRegistry.instance);
		}

		Fluids.openBlocksXPJuice = new Fluid("xpjuice").setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("OpenBlocks.xpjuice");
		FluidRegistry.registerFluid(Fluids.openBlocksXPJuice);
		Fluids.XPJuice = FluidRegistry.getFluid("xpjuice");
		XP_FLUID = new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1);

		if (Items.filledBucket != null) {
			FluidContainerRegistry.registerFluidContainer(Fluids.XPJuice, MetasBucket.xpbucket.newItemStack(), FluidContainerRegistry.EMPTY_BUCKET);
		}

		MagnetWhitelists.instance.initTesters();

		MinecraftForge.EVENT_BUS.register(MapDataManager.instance);

		if (Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.registerAddons();
		if (Loader.isModLoaded(Mods.OPENPERIPHERALCORE)) ModuleOpenPeripheral.registerAdapters();

		if (!Config.soSerious) {
			MinecraftForge.EVENT_BUS.register(new BrickManager());
		}

		if (Config.blockElevatorId > 0) {
			MinecraftForge.EVENT_BUS.register(ElevatorBlockRules.instance);
		}

		if (Config.radioVillagerId > 0) {
			VillagerRegistry.instance().registerVillagerId(Config.radioVillagerId);
			VillagerRegistry.instance().registerVillageTradeHandler(Config.radioVillagerId, RadioManager.instance);
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
		changeLog = ChangelogBuilder.createChangeLog();
		if (Loader.isModLoaded(Mods.FLANSMOD)) {
			ClassReferences.flansmodsEntityBullet = ReflectionHelper.getClass("co.uk.flansmods.common.guns.EntityBullet");
		}
		if (Enchantments.flimFlam != null) {
			FlimFlamRegistry.registerAttackFlimFlam(new TeleportFlimFlam());
			FlimFlamRegistry.registerAttackFlimFlam(new InventoryShuffleFlimFlam());
			FlimFlamRegistry.registerAttackFlimFlam(new UselessToolFlimFlam());
			FlimFlamRegistry.registerAttackFlimFlam(new BaneFlimFlam());
			FlimFlamRegistry.registerAttackFlimFlam(new LoreFlimFlam());
			FlimFlamRegistry.registerAttackFlimFlam(new RenameFlimFlam());
			FlimFlamRegistry.registerAttackFlimFlam(new SquidFilmFlam());
			FlimFlamRegistry.registerAttackFlimFlam(new SheepDyeFlimFlam());
		}
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
