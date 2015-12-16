package openblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
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
import openmods.integration.Integration;
import openmods.liquids.BucketFillHandler;
import openmods.network.event.NetworkEventManager;
import openmods.network.rpc.RpcCallDispatcher;
import openmods.utils.EnchantmentUtils;

import com.google.common.base.Objects;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = OpenBlocks.MODID, name = OpenBlocks.NAME, version = OpenBlocks.VERSION, dependencies = OpenBlocks.DEPENDENCIES, guiFactory = "openblocks.client.ModGuiFactory")
public class OpenBlocks {
	public static final String MODID = "OpenBlocks";
	public static final String NAME = "OpenBlocks";
	public static final String VERSION = "$VERSION$";
	public static final String PROXY_SERVER = "openblocks.common.ServerProxy";
	public static final String PROXY_CLIENT = "openblocks.client.ClientProxy";
	public static final String DEPENDENCIES = "required-after:OpenMods@[$LIB-VERSION$,$NEXT-LIB-VERSION$)";

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

	@Instance(MODID)
	public static OpenBlocks instance;

	@SidedProxy(clientSide = OpenBlocks.PROXY_CLIENT, serverSide = OpenBlocks.PROXY_SERVER)
	public static IOpenBlocksProxy proxy;

	public static class Blocks implements BlockInstances {
		@RegisterBlock(name = "ladder")
		public static BlockLadder ladder;

		@RegisterBlock(name = "guide", tileEntity = TileEntityGuide.class, itemBlock = ItemGuide.class)
		public static BlockGuide guide;

		@RegisterBlock(name = "builder_guide", tileEntity = TileEntityBuilderGuide.class, itemBlock = ItemGuide.class)
		public static BlockBuilderGuide builderGuide;

		@RegisterBlock(name = "elevator", itemBlock = ItemElevator.class)
		public static BlockElevator elevator;

		@RegisterBlock(name = "elevator_rotating", tileEntity = TileEntityElevatorRotating.class, itemBlock = ItemElevator.class)
		public static BlockElevatorRotating elevatorRotating;

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

		@RegisterBlock(name = "ropeladder")
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

		@RegisterBlock(name = "scaffolding")
		public static BlockScaffolding scaffolding;
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

		@RegisterItem(name = "epicEraser", unlocalizedName = "epic_eraser")
		public static ItemEpicEraser epicEraser;

		@RegisterItem(name = "wrench")
		public static ItemWrench wrench;
	}

	public static class Fluids {
		/**
		 * Instance of fluid that is added to fluid registry.
		 * It may not be used as default fluid, so don't compare directly with other fluids.
		 * FluidStacks created with this fluid should always be valid.
		 */
		public static final Fluid xpJuice = new Fluid("xpjuice").setLuminosity(10).setDensity(800).setViscosity(1500).setUnlocalizedName("OpenBlocks.xpjuice");
	}

	public static class Enchantments {
		public static Enchantment explosive;
		public static Enchantment lastStand;
		public static Enchantment flimFlam;
	}

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

	public static int renderIdFull;

	public static int renderIdFlat;

	public static final Achievement brickAchievement = new Achievement("openblocks.oops", "openblocks.droppedBrick", 13, 13, net.minecraft.init.Items.brick, null).registerStat();

	public static final Achievement stackAchievement = new Achievement("openblocks.tma2", "openblocks.stackOverflow", -13, 13, net.minecraft.init.Items.nether_star, null).registerStat();

	public static final StatBase brickStat = new StatBasic("openblocks.dropped",
			new ChatComponentTranslation("stat.openblocks.bricksDropped"),
			StatBase.simpleStatType).registerStat();

	private final ModStartupHelper startupHelper = new ModStartupHelper("openblocks") {

		@Override
		protected void populateConfig(Configuration config) {
			ConfigProcessing.processAnnotations("OpenBlocks", config, Config.class);
		}

		@Override
		protected void setupItemFactory(FactoryRegistry<Item> itemFactory) {
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
		}

	};

	@EventHandler
	public void construct(FMLConstructionEvent evt) {
		ApiProvider.setupApi();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		// needed first, to properly initialize delegates
		FluidRegistry.registerFluid(Fluids.xpJuice);

		startupHelper.registerBlocksHolder(OpenBlocks.Blocks.class);
		startupHelper.registerItemsHolder(OpenBlocks.Items.class);

		startupHelper.preInit(evt.getSuggestedConfigurationFile());

		NetworkEventManager.INSTANCE
				.startRegistration()
				.register(MapDataManager.MapDataRequestEvent.class)
				.register(MapDataManager.MapDataResponseEvent.class)
				.register(MapDataManager.MapUpdatesEvent.class)
				.register(ElevatorActionEvent.class)
				.register(PlayerActionEvent.class)
				.register(EntityMiniMe.OwnerChangeEvent.class);

		RpcCallDispatcher.INSTANCE.startRegistration()
				.registerInterface(IRotatable.class)
				.registerInterface(IStencilCrafter.class)
				.registerInterface(IColorChanger.class)
				.registerInterface(ILevelChanger.class)
				.registerInterface(ITriggerable.class)
				.registerInterface(IGuideAnimationTrigger.class);

		Config.register();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, OpenMods.proxy.wrapHandler(new OpenBlocksGuiHandler()));

		MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());

		if (OpenBlocks.Items.cursor != null) {
			MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
		}

		EntityRegistry.registerModEntity(EntityLuggage.class, "Luggage", ENTITY_LUGGAGE_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityXPOrbNoFly.class, "XPOrbNoFly", ENTITY_XP_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityHangGlider.class, "Hang Glider", ENTITY_HANGGLIDER_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityMagnet.class, "Magnet", ENTITY_MAGNET_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityMountedBlock.class, "MountedBlock", ENTITY_BLOCK_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityMagnet.PlayerBound.class, "Player-Magnet", ENTITY_MAGNET_PLAYER_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityCartographer.class, "Cartographer", ENTITY_CARTOGRAPHER_ID, OpenBlocks.instance, 64, 8, true);
		EntityRegistry.registerModEntity(EntityItemProjectile.class, "EntityItemProjectile", ENTITY_CANON_ITEM_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(EntityGoldenEye.class, "GoldenEye", ENTITY_GOLDEN_EYE_ID, OpenBlocks.instance, 64, 8, true);
		EntityRegistry.registerModEntity(EntityMiniMe.class, "MiniMe", ENTITY_MINIME_ID, OpenBlocks.instance, 64, 1, true);

		MagnetWhitelists.instance.initTesters();

		MinecraftForge.EVENT_BUS.register(MapDataManager.instance);

		Integration.addModule(new ModuleAdapters());
		Integration.addModule(new ModuleTurtles());

		if (!Config.soSerious) {
			MinecraftForge.EVENT_BUS.register(new BrickManager());
		}

		if (OpenBlocks.Blocks.elevator != null || OpenBlocks.Blocks.elevatorRotating != null) {
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

		if (Items.luggage != null) {
			MinecraftForge.EVENT_BUS.register(new LuggageDropHandler());
		}

		if (Blocks.elevator != null) {
			MinecraftForge.EVENT_BUS.register(new ElevatorActionHandler());
		}

		if (Blocks.tank != null) {
			BucketFillHandler.instance.addToWhitelist(TileEntityTank.class);
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
		registerOreDictionary();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {
		proxy.postInit();

		if (Enchantments.flimFlam != null) {
			FlimFlamRegistry.instance.registerFlimFlam("inventory-shuffle", -50, 100, new InventoryShuffleFlimFlam()).markSafe();
			FlimFlamRegistry.instance.registerFlimFlam("useless-tool", -125, 50, new UselessToolFlimFlam()).markSafe();
			FlimFlamRegistry.instance.registerFlimFlam("bane", -125, 100, new BaneFlimFlam()).markSafe();
			FlimFlamRegistry.instance.registerFlimFlam("epic-lore", -10, 100, new LoreFlimFlam()).markSafe();
			FlimFlamRegistry.instance.registerFlimFlam("living-rename", -10, 100, new RenameFlimFlam()).markSafe();
			FlimFlamRegistry.instance.registerFlimFlam("squid", -75, 50, new SquidFilmFlam()).markSafe();
			FlimFlamRegistry.instance.registerFlimFlam("sheep-dye", -5, 50, new SheepDyeFlimFlam()).markSafe();
			FlimFlamRegistry.instance.registerFlimFlam("invisible-mobs", -25, 10, new InvisibleMobsFlimFlam()).markSafe();
			FlimFlamRegistry.instance.registerFlimFlam("sound", -5, 150, new SoundFlimFlam()).markSilent().markSafe();

			FlimFlamRegistry.instance.registerFlimFlam("snowballs", -50, 50, new SnowballsFlimFlam());
			FlimFlamRegistry.instance.registerFlimFlam("teleport", -100, 30, new TeleportFlimFlam());
			FlimFlamRegistry.instance.registerFlimFlam("mount", -150, 25, new MountFlimFlam());
			FlimFlamRegistry.instance.registerFlimFlam("encase", -50, 50, new EncaseFlimFlam()).setRange(Integer.MIN_VALUE, -300);
			FlimFlamRegistry.instance.registerFlimFlam("creepers", -60, 50, new DummyCreepersFlimFlam());
			FlimFlamRegistry.instance.registerFlimFlam("disarm", -50, 50, new ItemDropFlimFlam());
			FlimFlamRegistry.instance.registerFlimFlam("effect", -75, 75, new EffectFlimFlam());
			FlimFlamRegistry.instance.registerFlimFlam("skyblock", -100, 150, new SkyblockFlimFlam()).setRange(Integer.MIN_VALUE, -400);

			FlimFlamRegistry.BLACKLIST.init();
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
		startupHelper.handleRenames(event);
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

	private static void registerOreDictionary() {
		OreDictionary.registerOre("craftingTableWood", new ItemStack(net.minecraft.init.Blocks.crafting_table));
		OreDictionary.registerOre("chestWood", new ItemStack(net.minecraft.init.Blocks.chest));
	}
}
