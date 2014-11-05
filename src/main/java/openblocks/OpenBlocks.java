package openblocks;

import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import openblocks.api.FlimFlamRegistry;
import openblocks.common.*;
import openblocks.common.block.*;
import openblocks.common.entity.*;
import openblocks.common.item.*;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.common.tileentity.*;
import openblocks.enchantments.flimflams.*;
import openblocks.events.ElevatorActionEvent;
import openblocks.events.PlayerActionEvent;
import openblocks.integration.ModuleAdapters;
import openblocks.integration.ModuleTurtles;
import openblocks.rpc.*;
import openblocks.rubbish.BrickManager;
import openblocks.rubbish.CommandFlimFlam;
import openblocks.rubbish.CommandLuck;
import openmods.Mods;
import openmods.OpenMods;
import openmods.config.BlockInstances;
import openmods.config.ItemInstances;
import openmods.config.game.*;
import openmods.config.properties.ConfigProcessing;
import openmods.entity.EntityBlock;
import openmods.integration.Integration;
import openmods.network.event.NetworkEventManager;
import openmods.network.rpc.RpcCallDispatcher;
import openmods.utils.EnchantmentUtils;
import openmods.utils.ReflectionHelper;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = OpenBlocks.ID, name = OpenBlocks.NAME, version = OpenBlocks.VERSION, dependencies = OpenBlocks.DEPENDENCIES)
public class OpenBlocks {
	public static final String ID = "OpenBlocks";
	public static final String NAME = "OpenBlocks";
	public static final String VERSION = "@VERSION@";
	public static final String PROXY_SERVER = "openblocks.common.ServerProxy";
	public static final String PROXY_CLIENT = "openblocks.client.ClientProxy";
	public static final String DEPENDENCIES = "required-after:OpenMods@[0.6,];after:OpenPeripheral;after:NotEnoughCodecs";

	private static final int ENTITY_HANGGLIDER_ID = 701;
	private static final int ENTITY_LUGGAGE_ID = 702;
	private static final int ENTITY_MAGNET_ID = 703;
	private static final int ENTITY_BLOCK_ID = 704;
	private static final int ENTITY_CARTOGRAPHER_ID = 705;
	private static final int ENTITY_CANON_ITEM_ID = 706;
	private static final int ENTITY_GOLDEN_EYE_ID = 707;
	private static final int ENTITY_MAGNET_PLAYER_ID = 708;
	private static final int ENTITY_XP_ID = 709;
	private static final int ENTITY_MINIME_ID = 710;

	@Instance(value = OpenBlocks.ID)
	public static OpenBlocks instance;

	@SidedProxy(clientSide = OpenBlocks.PROXY_CLIENT, serverSide = OpenBlocks.PROXY_SERVER)
	public static IOpenBlocksProxy proxy;

	public static class Blocks implements BlockInstances {
		@RegisterBlock(name = "ladder")
		public static BlockLadder ladder;

		@RegisterBlock(name = "guide", tileEntity = TileEntityGuide.class)
		public static BlockGuide guide;

		@RegisterBlock(name = "elevator", tileEntity = TileEntityElevator.class, itemBlock = ItemElevator.class)
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

		@RegisterBlock(name = "sponge")
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

		@RegisterBlock(name = "paintcan", tileEntity = TileEntityPaintCan.class, itemBlock = ItemPaintCan.class)
		public static BlockPaintCan paintCan;

		@RegisterBlock(name = "canvasglass", tileEntity = TileEntityCanvas.class)
		public static BlockCanvasGlass canvasGlass;

		@RegisterBlock(name = "projector", tileEntity = TileEntityProjector.class)
		public static BlockProjector projector;

		@RegisterBlock(name = "drawingtable", tileEntity = TileEntityDrawingTable.class)
		public static BlockDrawingTable drawingTable;

		@RegisterBlock(name = "sky", tileEntity = TileEntitySky.class, itemBlock = ItemSkyBlock.class)
		public static BlockSky sky;

		@RegisterBlock(name = "xpshower", tileEntity = TileEntityXPShower.class)
		public static BlockXPShower xpShower;

		@RegisterBlock(name = "goldenegg", tileEntity = TileEntityGoldenEgg.class)
		public static BlockGoldenEgg goldenEgg;
	}

	public static class Items implements ItemInstances {

		@RegisterItem(name = "hangglider")
		public static ItemHangGlider hangGlider;

		@RegisterItem(name = "generic", isConfigurable = false)
		public static ItemOBGeneric generic;

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

		@RegisterItem(name = "genericUnstackable", isConfigurable = false)
		public static ItemOBGenericUnstackable genericUnstackable;

		@RegisterItem(name = "cursor")
		public static ItemCursor cursor;

		@RegisterItem(name = "infoBook", unlocalizedName = "info_book")
		public static ItemInfoBook infoBook;

		@RegisterItem(name = "devnull")
		public static ItemDevNull devNull;

		@RegisterItem(name = "spongeonastick")
		public static ItemSpongeOnAStick spongeonastick;

		@RegisterItem(name = "pedometer")
		public static ItemPedometer pedometer;
	}

	public static class ClassReferences {
		public static Class<?> flansmodsEntityBullet;
	}

	public static class Fluids {
		public static Fluid xpJuice;
	}

	public static class Enchantments {
		public static Enchantment explosive;
		public static Enchantment lastStand;
		public static Enchantment flimFlam;
	}

	public static FluidStack XP_FLUID = null;

	public static CreativeTabs tabOpenBlocks = new CreativeTabs("tabOpenBlocks") {
		@Override
		public Item getTabIconItem() {
			Block block = Objects.firstNonNull(OpenBlocks.Blocks.flag, net.minecraft.init.Blocks.sponge);
			return Item.getItemFromBlock(block);
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

	public static final Achievement brickAchievement = new Achievement("openblocks.oops", "openblocks.droppedBrick", 13, 13, net.minecraft.init.Items.brick, null).registerStat();

	public static final StatBase brickStat = new StatBasic("openblocks.dropped",
			new ChatComponentTranslation("stat.openblocks.bricksDropped"),
			StatBase.simpleStatType).registerStat();

	private GameConfigProvider gameConfig;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		ConfigurableFeatureManager features = new ConfigurableFeatureManager();
		features.collectFromBlocks(OpenBlocks.Blocks.class);
		features.collectFromItems(OpenBlocks.Items.class);

		final File configFile = evt.getSuggestedConfigurationFile();
		Configuration config = new Configuration(configFile);

		ConfigProcessing.processAnnotations(configFile, "OpenBlocks", config, Config.class);
		features.loadFromConfiguration(config);

		if (config.hasChanged()) config.save();

		gameConfig = new GameConfigProvider("openblocks");
		gameConfig.setFeatures(features);

		final FactoryRegistry<Item> itemFactory = gameConfig.getItemFactory();
		itemFactory.registerFactory("pencilGlasses", new FactoryRegistry.Factory<Item>() {
			@Override
			public Item construct() {
				return new ItemImaginationGlasses(ItemImaginationGlasses.Type.PENCIL);
			}
		});

		itemFactory.registerFactory("technicolorGlasses", new FactoryRegistry.Factory<Item>() {
			@Override
			public Item construct() {
				return new ItemImaginationGlasses(ItemImaginationGlasses.Type.TECHNICOLOR);
			}
		});

		itemFactory.registerFactory("seriousGlasses", new FactoryRegistry.Factory<Item>() {
			@Override
			public Item construct() {
				return new ItemImaginationGlasses(ItemImaginationGlasses.Type.BASTARD);
			}
		});

		gameConfig.registerBlocks(OpenBlocks.Blocks.class);
		gameConfig.registerItems(OpenBlocks.Items.class);

		NetworkEventManager.INSTANCE
				.startRegistration()
				.register(MapDataManager.MapDataRequestEvent.class)
				.register(MapDataManager.MapDataResponseEvent.class)
				.register(MapDataManager.MapUpdatesEvent.class)
				.register(ElevatorActionEvent.class)
				.register(PlayerActionEvent.class);

		RpcCallDispatcher.INSTANCE.startRegistration()
				.registerInterface(IRotatable.class)
				.registerInterface(IStencilCrafter.class)
				.registerInterface(IColorChanger.class)
				.registerInterface(ILevelChanger.class);

		Config.register();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, OpenMods.proxy.wrapHandler(new OpenBlocksGuiHandler()));

		if (OpenBlocks.Blocks.grave != null) {
			MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());
		}

		if (OpenBlocks.Items.cursor != null) {
			MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
		}

		EntityRegistry.registerModEntity(EntityLuggage.class, "Luggage", ENTITY_LUGGAGE_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityXPOrbNoFly.class, "XPOrbNoFly", ENTITY_XP_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityHangGlider.class, "Hang Glider", ENTITY_HANGGLIDER_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityMagnet.class, "Magnet", ENTITY_MAGNET_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityBlock.class, "Block", ENTITY_BLOCK_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityMagnet.PlayerBound.class, "Player-Magnet", ENTITY_MAGNET_PLAYER_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityCartographer.class, "Cartographer", ENTITY_CARTOGRAPHER_ID, OpenBlocks.instance, 64, 8, true);
		EntityRegistry.registerModEntity(EntityItemProjectile.class, "EntityItemProjectile", ENTITY_CANON_ITEM_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityGoldenEye.class, "GoldenEye", ENTITY_GOLDEN_EYE_ID, OpenBlocks.instance, 64, 8, true);
		EntityRegistry.registerModEntity(EntityMiniMe.class, "MiniMe", ENTITY_MINIME_ID, OpenBlocks.instance, 64, 1, true);

		XP_FLUID = new FluidStack(OpenBlocks.Fluids.xpJuice, 1);

		MagnetWhitelists.instance.initTesters();

		MinecraftForge.EVENT_BUS.register(MapDataManager.instance);

		Integration.addModule(new ModuleAdapters());
		Integration.addModule(new ModuleTurtles());

		if (!Config.soSerious) {
			MinecraftForge.EVENT_BUS.register(new BrickManager());
		}

		if (OpenBlocks.Blocks.elevator != null) {
			MinecraftForge.EVENT_BUS.register(ElevatorBlockRules.instance);
		}

		if (Config.radioVillagerId > 0) {
			VillagerRegistry.instance().registerVillagerId(Config.radioVillagerId);
			VillagerRegistry.instance().registerVillageTradeHandler(Config.radioVillagerId, new RadioVillagerTradeManager());
		}

		{
			String luggageName = (String)EntityList.classToStringMapping.get(EntityLuggage.class);
			FMLInterModComms.sendMessage(Mods.MFR, "registerAutoSpawnerBlacklist", luggageName);
		}

		MinecraftForge.EVENT_BUS.register(PlayerInventoryStore.instance);

		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

		MinecraftForge.EVENT_BUS.register(new GameRuleManager());

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		FMLCommonHandler.instance().bus().register(new ServerTickHandler());
		proxy.init();
		proxy.registerRenderInformation();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.postInit();

		if (Loader.isModLoaded(Mods.FLANSMOD)) {
			ClassReferences.flansmodsEntityBullet = ReflectionHelper.getClass("co.uk.flansmods.common.guns.EntityBullet");
		}
		if (Enchantments.flimFlam != null) {
			FlimFlamRegistry.registerFlimFlam("inventory-shuffle", -50, 100, new InventoryShuffleFlimFlam()).markSafe();
			FlimFlamRegistry.registerFlimFlam("useless-tool", -125, 50, new UselessToolFlimFlam()).markSafe();
			FlimFlamRegistry.registerFlimFlam("bane", -125, 100, new BaneFlimFlam()).markSafe();
			FlimFlamRegistry.registerFlimFlam("epic-lore", -10, 100, new LoreFlimFlam()).markSafe();
			FlimFlamRegistry.registerFlimFlam("living-rename", -10, 100, new RenameFlimFlam()).markSafe();
			FlimFlamRegistry.registerFlimFlam("squid", -75, 50, new SquidFilmFlam()).markSafe();
			FlimFlamRegistry.registerFlimFlam("sheep-dye", -5, 50, new SheepDyeFlimFlam()).markSafe();
			FlimFlamRegistry.registerFlimFlam("invisible-mobs", -25, 10, new InvisibleMobsFlimFlam()).markSafe();
			FlimFlamRegistry.registerFlimFlam("sound", -5, 150, new SoundFlimFlam()).markSilent().markSafe();

			FlimFlamRegistry.registerFlimFlam("snowballs", -50, 50, new SnowballsFlimFlam());
			FlimFlamRegistry.registerFlimFlam("teleport", -100, 30, new TeleportFlimFlam());
			FlimFlamRegistry.registerFlimFlam("mount", -150, 25, new MountFlimFlam());
			FlimFlamRegistry.registerFlimFlam("encase", -50, 50, new EncaseFlimFlam()).setRange(Integer.MIN_VALUE, -300);
			FlimFlamRegistry.registerFlimFlam("creepers", -60, 50, new DummyCreepersFlimFlam());
			FlimFlamRegistry.registerFlimFlam("disarm", -50, 50, new ItemDropFlimFlam());
			FlimFlamRegistry.registerFlimFlam("effect", -75, 75, new EffectFlimFlam());
			FlimFlamRegistry.registerFlimFlam("skyblock", -100, 150, new SkyblockFlimFlam()).setRange(Integer.MIN_VALUE, -400);
		}
	}

	@EventHandler
	public void processMessage(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage m : event.getMessages()) {
			if (m.isStringMessage() && "donateUrl".equalsIgnoreCase(m.key)) {
				DonationUrlManager.instance().addUrl(m.getSender(), m.getStringValue());
			}
		}
	}

	@EventHandler
	public void handleRenames(FMLMissingMappingsEvent event) {
		Preconditions.checkNotNull(gameConfig, "What?");
		gameConfig.handleRemaps(event.get());
	}

	@EventHandler
	public void severStart(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new CommandFlimFlam());
		evt.registerServerCommand(new CommandLuck());
		evt.registerServerCommand(new CommandInventory());
	}

	public static String getModId() {
		return OpenBlocks.class.getAnnotation(Mod.class).modid();
	}
}
