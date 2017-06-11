package openblocks;

import com.google.common.base.Objects;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import openblocks.common.CommandInventory;
import openblocks.common.DonationUrlManager;
import openblocks.common.ElevatorActionHandler;
import openblocks.common.ElevatorBlockRules;
import openblocks.common.EntityEventHandler;
import openblocks.common.GameRuleManager;
import openblocks.common.GuideActionHandler;
import openblocks.common.LuggageDropHandler;
import openblocks.common.MagnetWhitelists;
import openblocks.common.MapDataManager;
import openblocks.common.PedometerHandler;
import openblocks.common.PlayerDeathHandler;
import openblocks.common.PlayerInventoryStore;
import openblocks.common.ServerTickHandler;
import openblocks.common.block.BlockAutoAnvil;
import openblocks.common.block.BlockAutoEnchantmentTable;
import openblocks.common.block.BlockBearTrap;
import openblocks.common.block.BlockBigButton;
import openblocks.common.block.BlockBlockBreaker;
import openblocks.common.block.BlockBlockPlacer;
import openblocks.common.block.BlockBuilderGuide;
import openblocks.common.block.BlockCannon;
import openblocks.common.block.BlockCanvas;
import openblocks.common.block.BlockCanvasGlass;
import openblocks.common.block.BlockDonationStation;
import openblocks.common.block.BlockDrawingTable;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockElevatorRotating;
import openblocks.common.block.BlockFan;
import openblocks.common.block.BlockFlag;
import openblocks.common.block.BlockGoldenEgg;
import openblocks.common.block.BlockGrave;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockHeal;
import openblocks.common.block.BlockImaginary;
import openblocks.common.block.BlockItemDropper;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockPaintCan;
import openblocks.common.block.BlockPaintMixer;
import openblocks.common.block.BlockPath;
import openblocks.common.block.BlockProjector;
import openblocks.common.block.BlockRopeLadder;
import openblocks.common.block.BlockScaffolding;
import openblocks.common.block.BlockSky;
import openblocks.common.block.BlockSponge;
import openblocks.common.block.BlockSprinkler;
import openblocks.common.block.BlockTank;
import openblocks.common.block.BlockTarget;
import openblocks.common.block.BlockTrophy;
import openblocks.common.block.BlockVacuumHopper;
import openblocks.common.block.BlockVillageHighlighter;
import openblocks.common.block.BlockXPBottler;
import openblocks.common.block.BlockXPDrain;
import openblocks.common.block.BlockXPShower;
import openblocks.common.entity.EntityCartographer;
import openblocks.common.entity.EntityCartographer.MapJobs;
import openblocks.common.entity.EntityGoldenEye;
import openblocks.common.entity.EntityHangGlider;
import openblocks.common.entity.EntityItemProjectile;
import openblocks.common.entity.EntityLuggage;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.entity.EntityMiniMe;
import openblocks.common.entity.EntityMountedBlock;
import openblocks.common.entity.EntityXPOrbNoFly;
import openblocks.common.item.ItemCartographer;
import openblocks.common.item.ItemCraneBackpack;
import openblocks.common.item.ItemCraneControl;
import openblocks.common.item.ItemCursor;
import openblocks.common.item.ItemDevNull;
import openblocks.common.item.ItemElevator;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemEpicEraser;
import openblocks.common.item.ItemFlagBlock;
import openblocks.common.item.ItemGoldenEye;
import openblocks.common.item.ItemGuide;
import openblocks.common.item.ItemHangGlider;
import openblocks.common.item.ItemHeightMap;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginationGlasses;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.common.item.ItemInfoBook;
import openblocks.common.item.ItemLuggage;
import openblocks.common.item.ItemOBGeneric;
import openblocks.common.item.ItemOBGenericUnstackable;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemPaintCan;
import openblocks.common.item.ItemPedometer;
import openblocks.common.item.ItemSkyBlock;
import openblocks.common.item.ItemSleepingBag;
import openblocks.common.item.ItemSlimalyzer;
import openblocks.common.item.ItemSonicGlasses;
import openblocks.common.item.ItemSpongeOnAStick;
import openblocks.common.item.ItemSqueegee;
import openblocks.common.item.ItemStencil;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.item.ItemTastyClay;
import openblocks.common.item.ItemTrophyBlock;
import openblocks.common.item.ItemWrench;
import openblocks.common.item.ItemXpBucket;
import openblocks.common.sync.SyncableBlockLayers;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.common.tileentity.TileEntityBigButton;
import openblocks.common.tileentity.TileEntityBlockBreaker;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import openblocks.common.tileentity.TileEntityBuilderGuide;
import openblocks.common.tileentity.TileEntityCannon;
import openblocks.common.tileentity.TileEntityCanvas;
import openblocks.common.tileentity.TileEntityDonationStation;
import openblocks.common.tileentity.TileEntityDrawingTable;
import openblocks.common.tileentity.TileEntityElevatorRotating;
import openblocks.common.tileentity.TileEntityFan;
import openblocks.common.tileentity.TileEntityFlag;
import openblocks.common.tileentity.TileEntityGoldenEgg;
import openblocks.common.tileentity.TileEntityGrave;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityHealBlock;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityItemDropper;
import openblocks.common.tileentity.TileEntityPaintCan;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openblocks.common.tileentity.TileEntityProjector;
import openblocks.common.tileentity.TileEntitySky;
import openblocks.common.tileentity.TileEntitySprinkler;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.common.tileentity.TileEntityTrophy;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openblocks.common.tileentity.TileEntityXPBottler;
import openblocks.common.tileentity.TileEntityXPDrain;
import openblocks.common.tileentity.TileEntityXPShower;
import openblocks.enchantments.flimflams.BaneFlimFlam;
import openblocks.enchantments.flimflams.DummyCreepersFlimFlam;
import openblocks.enchantments.flimflams.EffectFlimFlam;
import openblocks.enchantments.flimflams.EncaseFlimFlam;
import openblocks.enchantments.flimflams.FlimFlamRegistry;
import openblocks.enchantments.flimflams.InventoryShuffleFlimFlam;
import openblocks.enchantments.flimflams.InvisibleMobsFlimFlam;
import openblocks.enchantments.flimflams.ItemDropFlimFlam;
import openblocks.enchantments.flimflams.LoreFlimFlam;
import openblocks.enchantments.flimflams.MountFlimFlam;
import openblocks.enchantments.flimflams.RenameFlimFlam;
import openblocks.enchantments.flimflams.SheepDyeFlimFlam;
import openblocks.enchantments.flimflams.SkyblockFlimFlam;
import openblocks.enchantments.flimflams.SnowballsFlimFlam;
import openblocks.enchantments.flimflams.SoundFlimFlam;
import openblocks.enchantments.flimflams.SquidFilmFlam;
import openblocks.enchantments.flimflams.TeleportFlimFlam;
import openblocks.enchantments.flimflams.UselessToolFlimFlam;
import openblocks.events.ElevatorActionEvent;
import openblocks.events.GuideActionEvent;
import openblocks.events.PlayerActionEvent;
import openblocks.integration.ModuleAdapters;
import openblocks.integration.ModuleTurtles;
import openblocks.rpc.IColorChanger;
import openblocks.rpc.IGuideAnimationTrigger;
import openblocks.rpc.ILevelChanger;
import openblocks.rpc.IRotatable;
import openblocks.rpc.IStencilCrafter;
import openblocks.rpc.ITriggerable;
import openblocks.rubbish.BrickManager;
import openblocks.rubbish.CommandFlimFlam;
import openblocks.rubbish.CommandLuck;
import openmods.Mods;
import openmods.OpenMods;
import openmods.colors.ColoredModelProvider;
import openmods.config.BlockInstances;
import openmods.config.ItemInstances;
import openmods.config.game.FactoryRegistry;
import openmods.config.game.GameRegistryObjectsProvider;
import openmods.config.game.ModStartupHelper;
import openmods.config.game.RegisterBlock;
import openmods.config.game.RegisterItem;
import openmods.config.properties.ConfigProcessing;
import openmods.integration.Integration;
import openmods.liquids.BucketFillHandler;
import openmods.network.event.NetworkEventManager;
import openmods.network.rpc.RpcCallDispatcher;
import openmods.sync.SyncableObjectTypeRegistry;
import openmods.utils.EnchantmentUtils;

@Mod(modid = OpenBlocks.MODID, name = OpenBlocks.NAME, version = OpenBlocks.VERSION, dependencies = OpenBlocks.DEPENDENCIES, guiFactory = "openblocks.client.ModGuiFactory")
public class OpenBlocks {
	public static final String MODID = "openblocks";
	public static final String NAME = "OpenBlocks";
	public static final String VERSION = "$VERSION$";
	public static final String PROXY_SERVER = "openblocks.common.ServerProxy";
	public static final String PROXY_CLIENT = "openblocks.client.ClientProxy";
	public static final String DEPENDENCIES = "required-after:openmods@[$LIB-VERSION$,$NEXT-LIB-VERSION$)";

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

	private final ApiSetup apiSetup = new ApiSetup();

	public static class Blocks implements BlockInstances {
		@RegisterBlock(id = "ladder")
		public static BlockLadder ladder;

		@RegisterBlock(id = "guide", tileEntity = TileEntityGuide.class, itemBlock = ItemGuide.class)
		public static BlockGuide guide;

		@RegisterBlock(id = "builder_guide", tileEntity = TileEntityBuilderGuide.class, itemBlock = ItemGuide.class)
		public static BlockBuilderGuide builderGuide;

		@RegisterBlock(id = "elevator", itemBlock = ItemElevator.class, registerDefaultItemModel = false, customItemModels = ColoredModelProvider.class)
		public static BlockElevator elevator;

		@RegisterBlock(id = "elevator_rotating", tileEntity = TileEntityElevatorRotating.class, itemBlock = ItemElevator.class, registerDefaultItemModel = false, customItemModels = ColoredModelProvider.class)
		public static BlockElevatorRotating elevatorRotating;

		@RegisterBlock(id = "heal", tileEntity = TileEntityHealBlock.class)
		public static BlockHeal heal;

		@RegisterBlock(id = "target", tileEntity = TileEntityTarget.class)
		public static BlockTarget target;

		@RegisterBlock(id = "grave", tileEntity = TileEntityGrave.class)
		public static BlockGrave grave;

		@RegisterBlock(id = "flag", tileEntity = TileEntityFlag.class, itemBlock = ItemFlagBlock.class)
		public static BlockFlag flag;

		@RegisterBlock(id = "tank", tileEntity = TileEntityTank.class, itemBlock = ItemTankBlock.class)
		public static BlockTank tank;

		@RegisterBlock(id = "trophy", tileEntity = TileEntityTrophy.class, itemBlock = ItemTrophyBlock.class)
		public static BlockTrophy trophy;

		@RegisterBlock(id = "beartrap", tileEntity = TileEntityBearTrap.class)
		public static BlockBearTrap bearTrap;

		@RegisterBlock(id = "sprinkler", tileEntity = TileEntitySprinkler.class)
		public static BlockSprinkler sprinkler;

		@RegisterBlock(id = "cannon", tileEntity = TileEntityCannon.class)
		public static BlockCannon cannon;

		@RegisterBlock(id = "vacuumhopper", tileEntity = TileEntityVacuumHopper.class)
		public static BlockVacuumHopper vacuumHopper;

		@RegisterBlock(id = "sponge")
		public static BlockSponge sponge;

		@RegisterBlock(id = "bigbutton", tileEntity = TileEntityBigButton.class)
		public static BlockBigButton bigButton;

		@RegisterBlock(id = "imaginary", tileEntity = TileEntityImaginary.class, itemBlock = ItemImaginary.class, customItemModels = ItemImaginary.ModelProvider.class, registerDefaultItemModel = false)
		public static BlockImaginary imaginary;

		@RegisterBlock(id = "fan", tileEntity = TileEntityFan.class)
		public static BlockFan fan;

		@RegisterBlock(id = "xpbottler", tileEntity = TileEntityXPBottler.class)
		public static BlockXPBottler xpBottler;

		@RegisterBlock(id = "village_highlighter", tileEntity = TileEntityVillageHighlighter.class)
		public static BlockVillageHighlighter villageHighlighter;

		@RegisterBlock(id = "path")
		public static BlockPath path;

		@RegisterBlock(id = "autoanvil", tileEntity = TileEntityAutoAnvil.class)
		public static BlockAutoAnvil autoAnvil;

		@RegisterBlock(id = "autoenchantmenttable", tileEntity = TileEntityAutoEnchantmentTable.class)
		public static BlockAutoEnchantmentTable autoEnchantmentTable;

		@RegisterBlock(id = "xpdrain", tileEntity = TileEntityXPDrain.class)
		public static BlockXPDrain xpDrain;

		@RegisterBlock(id = "blockbreaker", tileEntity = TileEntityBlockBreaker.class)
		public static BlockBlockBreaker blockBreaker;

		@RegisterBlock(id = "blockPlacer", tileEntity = TileEntityBlockPlacer.class)
		public static BlockBlockPlacer blockPlacer;

		@RegisterBlock(id = "itemDropper", tileEntity = TileEntityItemDropper.class)
		public static BlockItemDropper itemDropper;

		@RegisterBlock(id = "ropeladder")
		public static BlockRopeLadder ropeLadder;

		@RegisterBlock(id = "donationStation", tileEntity = TileEntityDonationStation.class)
		public static BlockDonationStation donationStation;

		@RegisterBlock(id = "paintmixer", tileEntity = TileEntityPaintMixer.class)
		public static BlockPaintMixer paintMixer;

		@RegisterBlock(id = "canvas", tileEntity = TileEntityCanvas.class)
		public static BlockCanvas canvas;

		@RegisterBlock(id = "paintcan", tileEntity = TileEntityPaintCan.class, itemBlock = ItemPaintCan.class, registerDefaultItemModel = true)
		public static BlockPaintCan paintCan;

		@RegisterBlock(id = "canvasglass", tileEntity = TileEntityCanvas.class)
		public static BlockCanvasGlass canvasGlass;

		@RegisterBlock(id = "projector", tileEntity = TileEntityProjector.class)
		public static BlockProjector projector;

		@RegisterBlock(id = "drawingtable", tileEntity = TileEntityDrawingTable.class)
		public static BlockDrawingTable drawingTable;

		@RegisterBlock(id = "sky", tileEntity = TileEntitySky.class, itemBlock = ItemSkyBlock.class, unlocalizedName = "sky.normal")
		public static BlockSky sky;

		@RegisterBlock(id = "xpshower", tileEntity = TileEntityXPShower.class)
		public static BlockXPShower xpShower;

		@RegisterBlock(id = "goldenegg", tileEntity = TileEntityGoldenEgg.class)
		public static BlockGoldenEgg goldenEgg;

		@RegisterBlock(id = "scaffolding")
		public static BlockScaffolding scaffolding;
	}

	public static class Items implements ItemInstances {

		@RegisterItem(id = "hangglider")
		public static ItemHangGlider hangGlider;

		@RegisterItem(id = "generic", isConfigurable = false, registerDefaultModel = false)
		public static ItemOBGeneric generic;

		@RegisterItem(id = "luggage")
		public static ItemLuggage luggage;

		@RegisterItem(id = "sonicglasses")
		public static ItemSonicGlasses sonicGlasses;

		@RegisterItem(id = "pencilGlasses", unlocalizedName = "glasses.pencil")
		public static ItemImaginationGlasses pencilGlasses;

		@RegisterItem(id = "crayonGlasses", unlocalizedName = "glasses.crayon")
		public static ItemCrayonGlasses crayonGlasses;

		@RegisterItem(id = "technicolorGlasses", unlocalizedName = "glasses.technicolor")
		public static ItemImaginationGlasses technicolorGlasses;

		@RegisterItem(id = "seriousGlasses", unlocalizedName = "glasses.admin")
		public static ItemImaginationGlasses seriousGlasses;

		@RegisterItem(id = "craneControl", unlocalizedName = "crane_control")
		public static ItemCraneControl craneControl;

		@RegisterItem(id = "craneBackpack", unlocalizedName = "crane_backpack")
		public static ItemCraneBackpack craneBackpack;

		@RegisterItem(id = "slimalyzer")
		public static ItemSlimalyzer slimalyzer;

		@RegisterItem(id = "xpbucket", legacyIds = { "filledbucket" })
		public static ItemXpBucket xpBucket;

		@RegisterItem(id = "sleepingBag", unlocalizedName = "sleepingbag")
		public static ItemSleepingBag sleepingBag;

		@RegisterItem(id = "paintBrush", unlocalizedName = "paintbrush")
		public static ItemPaintBrush paintBrush;

		@RegisterItem(id = "stencil", registerDefaultModel = false)
		public static ItemStencil stencil;

		@RegisterItem(id = "squeegee")
		public static ItemSqueegee squeegee;

		@RegisterItem(id = "heightMap", unlocalizedName = "height_map")
		public static ItemHeightMap heightMap;

		@RegisterItem(id = "emptyMap", unlocalizedName = "empty_map")
		public static ItemEmptyMap emptyMap;

		@RegisterItem(id = "cartographer")
		public static ItemCartographer cartographer;

		@RegisterItem(id = "tastyClay", unlocalizedName = "tasty_clay")
		public static ItemTastyClay tastyClay;

		@RegisterItem(id = "goldenEye", unlocalizedName = "golden_eye")
		public static ItemGoldenEye goldenEye;

		@RegisterItem(id = "genericUnstackable", isConfigurable = false, registerDefaultModel = false)
		public static ItemOBGenericUnstackable genericUnstackable;

		@RegisterItem(id = "cursor")
		public static ItemCursor cursor;

		@RegisterItem(id = "infoBook", unlocalizedName = "info_book")
		public static ItemInfoBook infoBook;

		@RegisterItem(id = "devnull")
		public static ItemDevNull devNull;

		@RegisterItem(id = "spongeonastick")
		public static ItemSpongeOnAStick spongeonastick;

		@RegisterItem(id = "pedometer")
		public static ItemPedometer pedometer;

		@RegisterItem(id = "epicEraser", unlocalizedName = "epic_eraser")
		public static ItemEpicEraser epicEraser;

		@RegisterItem(id = "wrench")
		public static ItemWrench wrench;
	}

	public static ResourceLocation location(String path) {
		return new ResourceLocation("openblocks", path);
	}

	public static class Fluids {
		/**
		 * Instance of fluid that is added to fluid registry.
		 * It may not be used as default fluid, so don't compare directly with other fluids.
		 * FluidStacks created with this fluid should always be valid.
		 */
		public static final Fluid xpJuice = new Fluid("xpjuice", location("blocks/xpjuicestill"), location("blocks/xpjuiceflowing"))
				.setLuminosity(10)
				.setDensity(800)
				.setViscosity(1500)
				.setUnlocalizedName("OpenBlocks.xpjuice")
				.setEmptySound(SoundEvents.ENTITY_PLAYER_LEVELUP)
				.setFillSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
	}

	@ObjectHolder(MODID)
	public static class Sounds {
		// TODO subtitles where sensible
		// TODO remove categories from sounds.json
		@ObjectHolder("elevator.activate")
		public static final SoundEvent BLOCK_ELEVATOR_ACTIVATE = null;

		@ObjectHolder("grave.rob")
		public static final SoundEvent BLOCK_GRAVE_ROB = null;

		@ObjectHolder("beartrap.open")
		public static final SoundEvent BLOCK_BEARTRAP_OPEN = null;

		@ObjectHolder("beartrap.close")
		public static final SoundEvent BLOCK_BEARTRAP_CLOSE = null;

		@ObjectHolder("cannon.activate")
		public static final SoundEvent BLOCK_CANNON_ACTIVATE = null;

		@ObjectHolder("target.open")
		public static final SoundEvent BLOCK_TARGET_OPEN = null;

		@ObjectHolder("target.close")
		public static final SoundEvent BLOCK_TARGET_CLOSE = null;

		@ObjectHolder("bottler.signal")
		public static final SoundEvent BLOCK_XPBOTTLER_DONE = null;

		@ObjectHolder("crayon.place")
		public static final SoundEvent ITEM_CRAYON_PLACE = null;

		@ObjectHolder("luggage.walk")
		public static final SoundEvent ENTITY_LUGGAGE_WALK = null;

		@ObjectHolder("luggage.eat.food")
		public static final SoundEvent ENTITY_LUGGAGE_EAT_FOOD = null;

		@ObjectHolder("luggage.eat.item")
		public static final SoundEvent ENTITY_LUGGAGE_EAT_ITEM = null;

		@ObjectHolder("pedometer.use")
		public static final SoundEvent ITEM_PEDOMETER_USE = null;

		@ObjectHolder("slimalyzer.signal")
		public static final SoundEvent ITEM_SLIMALYZER_PING = null;

		@ObjectHolder("squeegee.use")
		public static final SoundEvent ITEM_SQUEEGEE_ACTION = null;

		@ObjectHolder("best.feature.ever.fart")
		public static final SoundEvent PLAYER_WHOOPS = null;

		@ObjectHolder("annoying.mosquito")
		public static final SoundEvent MISC_MOSQUITO = null;

		@ObjectHolder("annoying.alarmclock")
		public static final SoundEvent MISC_ALARM_CLOCK = null;

		@ObjectHolder("annoying.vibrate")
		public static final SoundEvent MISC_VIBRATE = null;

		public static void registerSounds() {
			registerSound("elevator.activate");
			registerSound("grave.rob");
			registerSound("crayon.place");
			registerSound("luggage.walk");
			registerSound("luggage.eat.food");
			registerSound("luggage.eat.item");
			registerSound("pedometer.use");
			registerSound("slimalyzer.signal");
			registerSound("squeegee.use");
			registerSound("best.feature.ever.fart");
			registerSound("annoying.mosquito");
			registerSound("annoying.alarmclock");
			registerSound("annoying.vibrate");
			registerSound("beartrap.open");
			registerSound("beartrap.close");
			registerSound("cannon.activate");
			registerSound("target.open");
			registerSound("target.close");
			registerSound("bottler.signal");
		}

		private static void registerSound(String id) {
			final ResourceLocation resourceLocation = location(id);
			GameRegistry.register(new SoundEvent(resourceLocation).setRegistryName(resourceLocation));
		}
	}

	@ObjectHolder(MODID)
	public static class Enchantments {
		@ObjectHolder("explosive")
		public static final Enchantment explosive = null;

		@ObjectHolder("last_stand")
		public static final Enchantment lastStand = null;

		@ObjectHolder("flim_flam")
		public static final Enchantment flimFlam = null;
	}

	public static CreativeTabs tabOpenBlocks = new CreativeTabs("tabOpenBlocks") {
		@Override
		public Item getTabIconItem() {
			Block block = Objects.firstNonNull(OpenBlocks.Blocks.flag, net.minecraft.init.Blocks.SPONGE);
			return Item.getItemFromBlock(block);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void displayAllRelevantItems(List<ItemStack> result) {
			super.displayAllRelevantItems(result);
			if (Enchantments.explosive != null) EnchantmentUtils.addAllBooks(Enchantments.explosive, result);
			if (Enchantments.lastStand != null) EnchantmentUtils.addAllBooks(Enchantments.lastStand, result);
			if (Enchantments.flimFlam != null) EnchantmentUtils.addAllBooks(Enchantments.flimFlam, result);
		}

	};

	public static final Achievement brickAchievement = new Achievement("openblocks.oops", "openblocks.droppedBrick", 13, 13, net.minecraft.init.Items.BRICK, null).registerStat();

	public static final Achievement stackAchievement = new Achievement("openblocks.tma2", "openblocks.stackOverflow", -13, 13, net.minecraft.init.Items.NETHER_STAR, null).registerStat();

	public static final StatBase brickStat = new StatBasic("openblocks.dropped",
			new TextComponentTranslation("stat.openblocks.bricksDropped"),
			StatBase.simpleStatType).registerStat();

	private final ModStartupHelper startupHelper = new ModStartupHelper("openblocks") {

		@Override
		protected void populateConfig(Configuration config) {
			ConfigProcessing.processAnnotations(MODID, config, Config.class);
		}

		@Override
		protected void setupConfigPre(GameRegistryObjectsProvider gameConfig) {
			gameConfig.setCreativeTab(tabOpenBlocks);
			gameConfig.addModIdToRemap("OpenBlocks");
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
		apiSetup.injectProvider();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		// needed first, to properly initialize delegates
		FluidRegistry.registerFluid(Fluids.xpJuice);

		startupHelper.registerBlocksHolder(OpenBlocks.Blocks.class);
		startupHelper.registerItemsHolder(OpenBlocks.Items.class);

		startupHelper.preInit(evt.getSuggestedConfigurationFile());

		SyncableObjectTypeRegistry.register(MapJobs.class);
		SyncableObjectTypeRegistry.register(SyncableBlockLayers.class);

		NetworkEventManager.INSTANCE
				.register(MapDataManager.MapDataRequestEvent.class)
				.register(MapDataManager.MapDataResponseEvent.class)
				.register(MapDataManager.MapUpdatesEvent.class)
				.register(ElevatorActionEvent.class)
				.register(PlayerActionEvent.class)
				.register(GuideActionEvent.class)
				.register(EntityMiniMe.OwnerChangeEvent.class);

		RpcCallDispatcher.INSTANCE
				.registerInterface(IRotatable.class)
				.registerInterface(IStencilCrafter.class)
				.registerInterface(IColorChanger.class)
				.registerInterface(ILevelChanger.class)
				.registerInterface(ITriggerable.class)
				.registerInterface(IGuideAnimationTrigger.class);

		Config.register();

		Sounds.registerSounds();

		apiSetup.setupApis();
		apiSetup.installHolderAccess(evt.getAsmData());

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, OpenMods.proxy.wrapHandler(new OpenBlocksGuiHandler()));

		MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());

		if (OpenBlocks.Items.cursor != null) {
			// TODO maybe figure way to validate GUIs over distance?
			// was: GuiOpenHandler: handler for PlayerOpenContainerEvent
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
			BrickManager.registerCapability();
			MinecraftForge.EVENT_BUS.register(new BrickManager());
		}

		if (OpenBlocks.Blocks.elevator != null || OpenBlocks.Blocks.elevatorRotating != null) {
			MinecraftForge.EVENT_BUS.register(ElevatorBlockRules.instance);
			MinecraftForge.EVENT_BUS.register(new ElevatorActionHandler());
		}

		if (Config.radioVillagerId > 0) {
			registerUselessVillager();
		}

		{
			String luggageName = EntityList.getEntityStringFromClass(EntityLuggage.class);
			// TODO 1.10 verify if it still works
			FMLInterModComms.sendMessage(Mods.MFR, "registerAutoSpawnerBlacklist", luggageName);
		}

		if (Items.luggage != null) {
			MinecraftForge.EVENT_BUS.register(new LuggageDropHandler());
		}

		if (Blocks.guide != null || Blocks.builderGuide != null) {
			MinecraftForge.EVENT_BUS.register(new GuideActionHandler());
		}

		if (Blocks.tank != null) {
			BucketFillHandler.instance.addToWhitelist(TileEntityTank.class);
		}

		if (Items.pedometer != null) {
			PedometerHandler.registerCapability();
		}

		if (Items.sleepingBag != null)
			MinecraftForge.EVENT_BUS.register(new ItemSleepingBag.IsSleepingHandler());

		MinecraftForge.EVENT_BUS.register(PlayerInventoryStore.instance);

		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

		MinecraftForge.EVENT_BUS.register(new GameRuleManager());

		proxy.preInit();
	}

	@SuppressWarnings({ "unused" })
	protected void registerUselessVillager() {
		final VillagerProfession prof = GameRegistry.register(new VillagerProfession(
				"openblocks:radio",
				"openblocks:textures/models/king-ish.png",
				"openblocks:textures/models/king-ish.png")); // TODO: zombie texture?

		new VillagerCareer(prof, "basic"); // TODO re-add trades
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		MinecraftForge.EVENT_BUS.register(new ServerTickHandler());
		proxy.init();
		proxy.registerRenderInformation();
		registerOreDictionary();

		startupHelper.init();

		if (Blocks.imaginary != null) {
			// delayed to init, since sounds are not loaded in pre-init
			Blocks.imaginary.setSoundType();
		}
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
		OreDictionary.registerOre("craftingTableWood", new ItemStack(net.minecraft.init.Blocks.CRAFTING_TABLE));
		OreDictionary.registerOre("chestWood", new ItemStack(net.minecraft.init.Blocks.CHEST));
	}
}
