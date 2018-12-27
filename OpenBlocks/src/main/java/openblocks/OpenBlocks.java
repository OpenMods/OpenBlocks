package openblocks;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatBasic;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import openblocks.advancements.Criterions;
import openblocks.common.CanvasReplaceBlacklist;
import openblocks.common.CommandInventory;
import openblocks.common.DonationUrlManager;
import openblocks.common.ElevatorActionHandler;
import openblocks.common.ElevatorBlockRules;
import openblocks.common.EntityEventHandler;
import openblocks.common.FluidXpUtils;
import openblocks.common.GameRuleManager;
import openblocks.common.GuideActionHandler;
import openblocks.common.LootHandler;
import openblocks.common.LuggageDropHandler;
import openblocks.common.MagnetWhitelists;
import openblocks.common.MapDataManager;
import openblocks.common.PedometerHandler;
import openblocks.common.PlayerDeathHandler;
import openblocks.common.PlayerInventoryStore;
import openblocks.common.RadioVillagerTrades;
import openblocks.common.ServerTickHandler;
import openblocks.common.block.BlockAutoAnvil;
import openblocks.common.block.BlockAutoEnchantmentTable;
import openblocks.common.block.BlockBearTrap;
import openblocks.common.block.BlockBigButton;
import openblocks.common.block.BlockBigButtonWood;
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
import openblocks.common.entity.EntityGlyph;
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
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemEpicEraser;
import openblocks.common.item.ItemFlagBlock;
import openblocks.common.item.ItemGlyph;
import openblocks.common.item.ItemGoldenEye;
import openblocks.common.item.ItemGuide;
import openblocks.common.item.ItemHangGlider;
import openblocks.common.item.ItemHeightMap;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginationGlasses;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.common.item.ItemInfoBook;
import openblocks.common.item.ItemInvertedSkyBlock;
import openblocks.common.item.ItemLuggage;
import openblocks.common.item.ItemMiracleMagnet;
import openblocks.common.item.ItemPaintBrush;
import openblocks.common.item.ItemPaintCan;
import openblocks.common.item.ItemPedometer;
import openblocks.common.item.ItemPointer;
import openblocks.common.item.ItemSleepingBag;
import openblocks.common.item.ItemSlimalyzer;
import openblocks.common.item.ItemSonicGlasses;
import openblocks.common.item.ItemSpongeOnAStick;
import openblocks.common.item.ItemSqueegee;
import openblocks.common.item.ItemStencil;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.item.ItemTastyClay;
import openblocks.common.item.ItemTrophyBlock;
import openblocks.common.item.ItemUnpreparedStencil;
import openblocks.common.item.ItemWrench;
import openblocks.common.item.ItemXpBucket;
import openblocks.common.sync.SyncableBlockLayers;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoEnchantmentTable;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.common.tileentity.TileEntityBigButton;
import openblocks.common.tileentity.TileEntityBigButtonWood;
import openblocks.common.tileentity.TileEntityBlockBreaker;
import openblocks.common.tileentity.TileEntityBlockPlacer;
import openblocks.common.tileentity.TileEntityBuilderGuide;
import openblocks.common.tileentity.TileEntityCannon;
import openblocks.common.tileentity.TileEntityCanvas;
import openblocks.common.tileentity.TileEntityDonationStation;
import openblocks.common.tileentity.TileEntityDrawingTable;
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
import openblocks.rpc.IColorChanger;
import openblocks.rpc.IGuideAnimationTrigger;
import openblocks.rpc.IItemDropper;
import openblocks.rpc.ILevelChanger;
import openblocks.rpc.IRotatable;
import openblocks.rpc.IStencilCrafter;
import openblocks.rpc.ITriggerable;
import openblocks.rubbish.BrickManager;
import openblocks.rubbish.CommandFlimFlam;
import openblocks.rubbish.CommandLuck;
import openmods.Log;
import openmods.OpenMods;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.config.BlockInstances;
import openmods.config.ItemInstances;
import openmods.config.game.AbstractFeatureManager;
import openmods.config.game.ConfigurableFeatureManager;
import openmods.config.game.FactoryRegistry;
import openmods.config.game.GameRegistryObjectsProvider;
import openmods.config.game.ModStartupHelper;
import openmods.config.game.RegisterBlock;
import openmods.config.game.RegisterBlock.RegisterTileEntity;
import openmods.config.game.RegisterItem;
import openmods.config.properties.ConfigProcessing;
import openmods.entity.EntityBlock;
import openmods.liquids.ContainerBucketFillHandler;
import openmods.liquids.SingleFluidBucketFillHandler;
import openmods.network.event.NetworkEventEntry;
import openmods.network.event.NetworkEventManager;
import openmods.network.rpc.MethodEntry;
import openmods.network.rpc.RpcCallDispatcher;
import openmods.sync.SyncableObjectType;
import openmods.sync.SyncableObjectTypeRegistry;
import openmods.utils.EnchantmentUtils;

@Mod(modid = OpenBlocks.MODID, name = OpenBlocks.NAME, version = OpenBlocks.VERSION, dependencies = OpenBlocks.DEPENDENCIES, guiFactory = OpenBlocks.GUI_FACTORY, updateJSON = OpenBlocks.UPDATE_JSON, certificateFingerprint = OpenMods.CERTIFICATE_FINGERPRINT)
public class OpenBlocks {

	public static final String MODID = "openblocks";
	public static final String NAME = "OpenBlocks";
	public static final String VERSION = "$VERSION$";
	public static final String PROXY_SERVER = "openblocks.common.ServerProxy";
	public static final String PROXY_CLIENT = "openblocks.client.ClientProxy";
	public static final String DEPENDENCIES = "required:forge@[14.23.4.2705,);required-after:openmods@[$LIB-VERSION$,$NEXT-LIB-VERSION$)";
	public static final String GUI_FACTORY = "openblocks.client.ModGuiFactory";
	public static final String UPDATE_JSON = "http://openmods.info/versions/openblocks.json"; // HTTP, for wider support

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
	private static final int ENTITY_GLYPH_ID = 711;

	@Instance(MODID)
	public static OpenBlocks instance;

	@SidedProxy(clientSide = OpenBlocks.PROXY_CLIENT, serverSide = OpenBlocks.PROXY_SERVER)
	public static IOpenBlocksProxy proxy;

	private final ApiSetup apiSetup = new ApiSetup();

	public static class Blocks implements BlockInstances {
		@RegisterBlock(type = BlockLadder.class, id = "ladder")
		public static Block ladder;

		@RegisterBlock(type = BlockGuide.class, id = "guide", tileEntity = TileEntityGuide.class, itemBlock = ItemGuide.class)
		public static Block guide;

		@RegisterBlock(type = BlockBuilderGuide.class, id = "builder_guide", tileEntity = TileEntityBuilderGuide.class, itemBlock = ItemGuide.class)
		public static Block builderGuide;

		@RegisterBlock(type = BlockElevator.class, id = "white_elevator")
		public static Block whiteElevator;

		@RegisterBlock(type = BlockElevator.class, id = "orange_elevator")
		public static Block orangeElevator;

		@RegisterBlock(type = BlockElevator.class, id = "magenta_elevator")
		public static Block magentaElevator;

		@RegisterBlock(type = BlockElevator.class, id = "light_blue_elevator")
		public static Block lightBlueElevator;

		@RegisterBlock(type = BlockElevator.class, id = "yellow_elevator")
		public static Block yellowElevator;

		@RegisterBlock(type = BlockElevator.class, id = "lime_elevator")
		public static Block limeElevator;

		@RegisterBlock(type = BlockElevator.class, id = "pink_elevator")
		public static Block pinkElevator;

		@RegisterBlock(type = BlockElevator.class, id = "gray_elevator")
		public static Block grayElevator;

		@RegisterBlock(type = BlockElevator.class, id = "light_gray_elevator")
		public static Block lightGrayElevator;

		@RegisterBlock(type = BlockElevator.class, id = "cyan_elevator")
		public static Block cyanElevator;

		@RegisterBlock(type = BlockElevator.class, id = "purple_elevator")
		public static Block purpleElevator;

		@RegisterBlock(type = BlockElevator.class, id = "blue_elevator")
		public static Block blueElevator;

		@RegisterBlock(type = BlockElevator.class, id = "brown_elevator")
		public static Block brownElevator;

		@RegisterBlock(type = BlockElevator.class, id = "green_elevator")
		public static Block greenElevator;

		@RegisterBlock(type = BlockElevator.class, id = "red_elevator")
		public static Block redElevator;

		@RegisterBlock(type = BlockElevator.class, id = "black_elevator")
		public static Block blackElevator;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "white_elevator_rotating")
		public static Block whiteElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "orange_elevator_rotating")
		public static Block orangeElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "magenta_elevator_rotating")
		public static Block magentaElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "light_blue_elevator_rotating")
		public static Block lightBlueElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "yellow_elevator_rotating")
		public static Block yellowElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "lime_elevator_rotating")
		public static Block limeElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "pink_elevator_rotating")
		public static Block pinkElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "gray_elevator_rotating")
		public static Block grayElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "light_gray_elevator_rotating")
		public static Block lightGrayElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "cyan_elevator_rotating")
		public static Block cyanElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "purple_elevator_rotating")
		public static Block purpleElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "blue_elevator_rotating")
		public static Block blueElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "brown_elevator_rotating")
		public static Block brownElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "green_elevator_rotating")
		public static Block greenElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "red_elevator_rotating")
		public static Block redElevatorRotating;

		@RegisterBlock(type = BlockElevatorRotating.class, id = "black_elevator_rotating")
		public static Block blackElevatorRotating;

		@RegisterBlock(type = BlockHeal.class, id = "heal", tileEntity = TileEntityHealBlock.class)
		public static Block heal;

		@RegisterBlock(type = BlockTarget.class, id = "target", tileEntity = TileEntityTarget.class)
		public static Block target;

		@RegisterBlock(type = BlockGrave.class, id = "grave", tileEntity = TileEntityGrave.class)
		public static Block grave;

		@RegisterBlock(type = BlockFlag.class, id = "flag", tileEntity = TileEntityFlag.class, itemBlock = ItemFlagBlock.class)
		public static Block flag;

		@RegisterBlock(type = BlockTank.class, id = "tank", tileEntity = TileEntityTank.class, itemBlock = ItemTankBlock.class)
		public static Block tank;

		@RegisterBlock(type = BlockTrophy.class, id = "trophy", tileEntity = TileEntityTrophy.class, itemBlock = ItemTrophyBlock.class)
		public static Block trophy;

		@RegisterBlock(type = BlockBearTrap.class, id = "beartrap", tileEntity = TileEntityBearTrap.class)
		public static Block bearTrap;

		@RegisterBlock(type = BlockSprinkler.class, id = "sprinkler", tileEntity = TileEntitySprinkler.class)
		public static Block sprinkler;

		@RegisterBlock(type = BlockCannon.class, id = "cannon", tileEntity = TileEntityCannon.class)
		public static OpenBlock cannon;

		@RegisterBlock(type = BlockVacuumHopper.class, id = "vacuum_hopper", tileEntity = TileEntityVacuumHopper.class, legacyIds = "vacuumhopper")
		public static Block vacuumHopper;

		@RegisterBlock(type = BlockSponge.class, id = "sponge")
		public static Block sponge;

		@RegisterBlock(type = BlockBigButton.class, id = "big_button", tileEntity = TileEntityBigButton.class, legacyIds = "bigbutton")
		public static Block bigButton;

		@RegisterBlock(type = BlockBigButtonWood.class, id = "big_button_wood", tileEntity = TileEntityBigButtonWood.class)
		public static Block bigButtonWood;

		@RegisterBlock(id = "imaginary", tileEntity = TileEntityImaginary.class, itemBlock = ItemImaginary.class, customItemModels = ItemImaginary.ModelProvider.class, registerDefaultItemModel = false)
		public static BlockImaginary imaginary;

		@RegisterBlock(type = BlockFan.class, id = "fan", tileEntity = TileEntityFan.class)
		public static Block fan;

		@RegisterBlock(type = BlockXPBottler.class, id = "xp_bottler", tileEntity = TileEntityXPBottler.class, legacyIds = "xpbottler")
		public static Block xpBottler;

		@RegisterBlock(type = BlockVillageHighlighter.class, id = "village_highlighter", tileEntity = TileEntityVillageHighlighter.class)
		public static Block villageHighlighter;

		@RegisterBlock(type = BlockPath.class, id = "path")
		public static Block path;

		@RegisterBlock(type = BlockAutoAnvil.class, id = "auto_anvil", tileEntity = TileEntityAutoAnvil.class, legacyIds = "autoanvil")
		public static Block autoAnvil;

		@RegisterBlock(type = BlockAutoEnchantmentTable.class, id = "auto_enchantment_table", tileEntity = TileEntityAutoEnchantmentTable.class, legacyIds = "autoenchantmenttable")
		public static Block autoEnchantmentTable;

		@RegisterBlock(type = BlockXPDrain.class, id = "xp_drain", tileEntity = TileEntityXPDrain.class, legacyIds = "xpdrain")
		public static Block xpDrain;

		@RegisterBlock(type = BlockBlockBreaker.class, id = "block_breaker", tileEntity = TileEntityBlockBreaker.class, legacyIds = "blockbreaker")
		public static Block blockBreaker;

		@RegisterBlock(type = BlockBlockPlacer.class, id = "block_placer", tileEntity = TileEntityBlockPlacer.class, legacyIds = "blockPlacer")
		public static Block blockPlacer;

		@RegisterBlock(type = BlockItemDropper.class, id = "item_dropper", tileEntity = TileEntityItemDropper.class, legacyIds = "itemDropper")
		public static Block itemDropper;

		@RegisterBlock(type = BlockRopeLadder.class, id = "rope_ladder", legacyIds = "ropeladder")
		public static Block ropeLadder;

		@RegisterBlock(type = BlockDonationStation.class, id = "donation_station", tileEntity = TileEntityDonationStation.class, legacyIds = "donationStation")
		public static Block donationStation;

		@RegisterBlock(type = BlockPaintMixer.class, id = "paint_mixer", tileEntity = TileEntityPaintMixer.class, legacyIds = "paintmixer")
		public static Block paintMixer;

		@RegisterBlock(type = BlockCanvas.class, id = "canvas", tileEntities = @RegisterTileEntity(name = "canvas", cls = TileEntityCanvas.class, main = true))
		public static Block canvas;

		@RegisterBlock(type = BlockPaintCan.class, id = "paint_can", tileEntity = TileEntityPaintCan.class, itemBlock = ItemPaintCan.class, legacyIds = "paintcan")
		public static Block paintCan;

		@RegisterBlock(type = BlockCanvasGlass.class, id = "canvas_glass", tileEntities = @RegisterTileEntity(name = "canvas", cls = TileEntityCanvas.class, main = true), legacyIds = "canvasglass")
		public static Block canvasGlass;

		@RegisterBlock(type = BlockProjector.class, id = "projector", tileEntity = TileEntityProjector.class)
		public static Block projector;

		@RegisterBlock(type = BlockDrawingTable.class, id = "drawing_table", tileEntity = TileEntityDrawingTable.class, legacyIds = "drawingtable")
		public static Block drawingTable;

		@RegisterBlock(type = BlockSky.class, id = "sky", tileEntities = @RegisterTileEntity(name = "sky", cls = TileEntitySky.class, main = true), unlocalizedName = "sky.normal")
		public static Block sky;

		@RegisterBlock(type = BlockSky.Inverted.class, id = "inverted_sky", tileEntities = @RegisterTileEntity(name = "sky", cls = TileEntitySky.class, main = true), itemBlock = ItemInvertedSkyBlock.class, unlocalizedName = "sky.normal")
		public static Block invertedSky;

		@RegisterBlock(type = BlockXPShower.class, id = "xp_shower", tileEntity = TileEntityXPShower.class, legacyIds = "xpshower")
		public static Block xpShower;

		@RegisterBlock(type = BlockGoldenEgg.class, id = "golden_egg", tileEntity = TileEntityGoldenEgg.class, legacyIds = "goldenegg")
		public static Block goldenEgg;

		@RegisterBlock(type = BlockScaffolding.class, id = "scaffolding", itemBlock = BlockScaffolding.Item.class)
		public static Block scaffolding;
	}

	public static class Items implements ItemInstances {

		@RegisterItem(type = ItemHangGlider.class, id = "hang_glider", legacyIds = "hangglider")
		public static Item hangGlider;

		@RegisterItem(id = "glider_wing")
		public static Item gliderWing;

		@RegisterItem(id = "beam")
		public static Item beam;

		@RegisterItem(id = "crane_engine")
		public static Item craneEngine;

		@RegisterItem(id = "crane_magnet")
		public static Item craneMagnet;

		@RegisterItem(id = "line")
		public static Item line;

		@RegisterItem(id = "map_controller")
		public static Item mapController;

		@RegisterItem(id = "map_memory")
		public static Item mapMemory;

		@RegisterItem(id = "assistant_base")
		public static Item assistantBase;

		@RegisterItem(type = ItemUnpreparedStencil.class, id = "unprepared_stencil")
		public static Item unpreparedStencil;

		@RegisterItem(id = "sketching_pencil")
		public static Item sketchingPencil;

		@RegisterItem(type = ItemMiracleMagnet.class, id = "miracle_magnet")
		public static Item miracleMagnet;

		@RegisterItem(type = ItemLuggage.class, id = "luggage")
		public static Item luggage;

		@RegisterItem(type = ItemSonicGlasses.class, id = "sonic_glasses", legacyIds = "sonicglasses")
		public static Item sonicGlasses;

		@RegisterItem(type = ItemImaginationGlasses.class, id = "pencil_glasses", unlocalizedName = "glasses.pencil", legacyIds = "pencilGlasses")
		public static Item pencilGlasses;

		@RegisterItem(type = ItemCrayonGlasses.class, id = "crayon_glasses", unlocalizedName = "glasses.crayon", legacyIds = "crayonGlasses")
		public static Item crayonGlasses;

		@RegisterItem(type = ItemImaginationGlasses.class, id = "technicolor_glasses", unlocalizedName = "glasses.technicolor", legacyIds = "technicolorGlasses")
		public static Item technicolorGlasses;

		@RegisterItem(type = ItemImaginationGlasses.class, id = "serious_glasses", unlocalizedName = "glasses.admin", legacyIds = "seriousGlasses")
		public static Item seriousGlasses;

		@RegisterItem(type = ItemCraneControl.class, id = "crane_control", legacyIds = "craneControl")
		public static Item craneControl;

		@RegisterItem(type = ItemCraneBackpack.class, id = "crane_backpack", legacyIds = "craneBackpack")
		public static Item craneBackpack;

		@RegisterItem(type = ItemSlimalyzer.class, id = "slimalyzer")
		public static Item slimalyzer;

		@RegisterItem(type = ItemXpBucket.class, id = "xp_bucket", legacyIds = "filledbucket")
		public static Item xpBucket;

		@RegisterItem(type = ItemSleepingBag.class, id = "sleeping_bag", legacyIds = "sleepingBag")
		public static Item sleepingBag;

		@RegisterItem(type = ItemPaintBrush.class, id = "paintbrush", legacyIds = "paintBrush")
		public static Item paintBrush;

		@RegisterItem(type = ItemStencil.class, id = "stencil", registerDefaultModel = false)
		public static Item stencil;

		@RegisterItem(type = ItemSqueegee.class, id = "squeegee")
		public static Item squeegee;

		@RegisterItem(type = ItemHeightMap.class, id = "height_map", legacyIds = "heightMap")
		public static Item heightMap;

		@RegisterItem(type = ItemEmptyMap.class, id = "empty_map", legacyIds = "emptyMap")
		public static Item emptyMap;

		@RegisterItem(type = ItemCartographer.class, id = "cartographer")
		public static Item cartographer;

		@RegisterItem(type = ItemTastyClay.class, id = "tasty_clay", legacyIds = "tastyClay")
		public static Item tastyClay;

		@RegisterItem(type = ItemGoldenEye.class, id = "golden_eye", legacyIds = "goldenEye")
		public static Item goldenEye;

		@RegisterItem(type = ItemPointer.class, id = "pointer")
		public static Item pointer;

		@RegisterItem(type = ItemCursor.class, id = "cursor")
		public static Item cursor;

		@RegisterItem(type = ItemInfoBook.class, id = "info_book", legacyIds = "infoBook")
		public static Item infoBook;

		@RegisterItem(type = ItemDevNull.class, id = "dev_null", legacyIds = "devnull")
		public static Item devNull;

		@RegisterItem(type = ItemSpongeOnAStick.class, id = "sponge_on_a_stick", legacyIds = "spongeonastick")
		public static Item spongeonastick;

		@RegisterItem(type = ItemPedometer.class, id = "pedometer")
		public static Item pedometer;

		@RegisterItem(type = ItemEpicEraser.class, id = "epic_eraser", legacyIds = "epicEraser")
		public static Item epicEraser;

		@RegisterItem(type = ItemWrench.class, id = "wrench")
		public static Item wrench;

		@RegisterItem(type = ItemGlyph.class, id = "glyph")
		public static Item glyph;
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
		public static final Fluid xpJuice = new Fluid("xpjuice", location("blocks/xp_juice_still"), location("blocks/xp_juice_flowing"))
				.setLuminosity(10)
				.setDensity(800)
				.setViscosity(1500)
				.setUnlocalizedName("openblocks.xp_juice")
				.setEmptySound(SoundEvents.ENTITY_PLAYER_LEVELUP)
				.setFillSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);
	}

	@ObjectHolder(MODID)
	@EventBusSubscriber
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

		@ObjectHolder("minecraft:entity.painting.place")
		public static final SoundEvent ENTITY_GLYPH_PLACE = null;

		@ObjectHolder("minecraft:entity.painting.break")
		public static final SoundEvent ENTITY_GLYPH_BREAK = null;

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

		@SubscribeEvent
		public static void registerSounds(RegistryEvent.Register<SoundEvent> evt) {
			final IForgeRegistry<SoundEvent> registry = evt.getRegistry();
			registerSound(registry, "elevator.activate");
			registerSound(registry, "grave.rob");
			registerSound(registry, "crayon.place");
			registerSound(registry, "luggage.walk");
			registerSound(registry, "luggage.eat.food");
			registerSound(registry, "luggage.eat.item");
			registerSound(registry, "pedometer.use");
			registerSound(registry, "slimalyzer.signal");
			registerSound(registry, "squeegee.use");
			registerSound(registry, "best.feature.ever.fart");
			registerSound(registry, "annoying.mosquito");
			registerSound(registry, "annoying.alarmclock");
			registerSound(registry, "annoying.vibrate");
			registerSound(registry, "beartrap.open");
			registerSound(registry, "beartrap.close");
			registerSound(registry, "cannon.activate");
			registerSound(registry, "target.open");
			registerSound(registry, "target.close");
			registerSound(registry, "bottler.signal");
		}

		private static void registerSound(IForgeRegistry<SoundEvent> registry, String id) {
			final ResourceLocation resourceLocation = location(id);
			registry.register(new SoundEvent(resourceLocation).setRegistryName(resourceLocation));
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

	private static CreativeTabs createTabOpenBlocks() {
		return new CreativeTabs("tabOpenBlocks") {
			@Override
			public ItemStack getTabIconItem() {
				if (OpenBlocks.Blocks.flag != null) {
					return new ItemStack(OpenBlocks.Blocks.flag, 1, BlockFlag.DEFAULT_COLOR.vanillaBlockId);
				} else {
					return new ItemStack(net.minecraft.init.Blocks.SPONGE);
				}
			}

			@Override
			@SideOnly(Side.CLIENT)
			public void displayAllRelevantItems(NonNullList<ItemStack> result) {
				super.displayAllRelevantItems(result);
				if (Enchantments.explosive != null) EnchantmentUtils.addAllBooks(Enchantments.explosive, result);
				if (Enchantments.lastStand != null) EnchantmentUtils.addAllBooks(Enchantments.lastStand, result);
				if (Enchantments.flimFlam != null) EnchantmentUtils.addAllBooks(Enchantments.flimFlam, result);
			}
		};
	}

	public static final StatBase brickStat = new StatBasic("openblocks.dropped", new TextComponentTranslation("stat.openblocks.bricksDropped"), StatBase.simpleStatType).registerStat();

	private final ModStartupHelper startupHelper = new ModStartupHelper("openblocks") {

		@Override
		protected void populateConfig(Configuration config) {
			ConfigProcessing.processAnnotations(MODID, config, Config.class);
		}

		@Override
		protected void setupConfigPre(GameRegistryObjectsProvider gameConfig) {
			gameConfig.setCreativeTab(OpenBlocks::createTabOpenBlocks);
			gameConfig.addModIdToRemap("OpenBlocks");
		}

		@Override
		protected void registerCustomFeatures(ConfigurableFeatureManager features) {
			features.addCustomRule(AbstractFeatureManager.CATEGORY_ITEMS, "miracle_magnet", value -> value && Config.enableCraneTurtles && Loader.isModLoaded(openmods.Mods.OPENPERIPHERALCORE));
		};

		@Override
		protected void setupBlockFactory(FactoryRegistry<Block> blockFactory) {
			for (final ColorMeta color : ColorMeta.getAllColors()) {
				blockFactory.registerFactory(color.id + "_elevator", () -> new BlockElevator(color));
				blockFactory.registerFactory(color.id + "_elevator_rotating", () -> new BlockElevatorRotating(color));
			}
		}

		@Override
		protected void setupItemFactory(FactoryRegistry<Item> itemFactory) {
			itemFactory.registerFactory("pencil_glasses", () -> new ItemImaginationGlasses(ItemImaginationGlasses.Type.PENCIL));
			itemFactory.registerFactory("technicolor_glasses", () -> new ItemImaginationGlasses(ItemImaginationGlasses.Type.TECHNICOLOR));
			itemFactory.registerFactory("serious_glasses", () -> new ItemImaginationGlasses(ItemImaginationGlasses.Type.BASTARD));
		}

	};

	@EventHandler
	public void construct(FMLConstructionEvent evt) {
		apiSetup.injectProvider();
	}

	@EventBusSubscriber
	public static class RegistryEntries {

		@SubscribeEvent
		public static void registerSyncTypes(RegistryEvent.Register<SyncableObjectType> type) {
			SyncableObjectTypeRegistry.startRegistration(type.getRegistry())
					.register(MapJobs.class)
					.register(SyncableBlockLayers.class);
		}

		@SubscribeEvent
		public static void registerMethodTypes(RegistryEvent.Register<MethodEntry> evt) {
			RpcCallDispatcher.startMethodRegistration(evt.getRegistry())
					.registerInterface(IRotatable.class)
					.registerInterface(IStencilCrafter.class)
					.registerInterface(IColorChanger.class)
					.registerInterface(ILevelChanger.class)
					.registerInterface(ITriggerable.class)
					.registerInterface(IGuideAnimationTrigger.class)
					.registerInterface(IItemDropper.class);
		}

		@SubscribeEvent
		public static void registerNetworkEvents(RegistryEvent.Register<NetworkEventEntry> evt) {
			NetworkEventManager.startRegistration(evt.getRegistry())
					.register(MapDataManager.MapDataRequestEvent.class)
					.register(MapDataManager.MapDataResponseEvent.class)
					.register(MapDataManager.MapUpdatesEvent.class)
					.register(ElevatorActionEvent.class)
					.register(PlayerActionEvent.class)
					.register(GuideActionEvent.class)
					.register(EntityMiniMe.OwnerChangeEvent.class);
		}
	}

	private static final String decorateEntityName(String name) {
		return "openblocks." + name;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		// needed first, to properly initialize delegates
		FluidRegistry.registerFluid(Fluids.xpJuice);

		if (Config.registerUniversalXpBucket) {
			FluidRegistry.addBucketForFluid(Fluids.xpJuice);
		}

		Criterions.init();

		startupHelper.registerBlocksHolder(OpenBlocks.Blocks.class);
		startupHelper.registerItemsHolder(OpenBlocks.Items.class);

		startupHelper.preInit(evt.getSuggestedConfigurationFile());

		Config.register();

		apiSetup.setupApis();
		apiSetup.installHolderAccess(evt.getAsmData());

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, OpenMods.proxy.wrapHandler(new OpenBlocksGuiHandler()));

		MinecraftForge.EVENT_BUS.register(new PlayerDeathHandler());

		if (OpenBlocks.Items.cursor != null) {
			// TODO maybe figure way to validate GUIs over distance?
			// was: GuiOpenHandler: handler for PlayerOpenContainerEvent
		}

		EntityRegistry.registerModEntity(location("luggage"), EntityLuggage.class, decorateEntityName("luggage"), ENTITY_LUGGAGE_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(location("xp_orb_no_fly"), EntityXPOrbNoFly.class, decorateEntityName("xp_orb_no_fly"), ENTITY_XP_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(location("hang_glider"), EntityHangGlider.class, decorateEntityName("hang_glider"), ENTITY_HANGGLIDER_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(location("magnet"), EntityMagnet.class, decorateEntityName("magnet"), ENTITY_MAGNET_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(location("mounted_block"), EntityMountedBlock.class, decorateEntityName("mounted_block"), ENTITY_BLOCK_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(location("player_magnet"), EntityMagnet.PlayerBound.class, decorateEntityName("player_magnet"), ENTITY_MAGNET_PLAYER_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(location("cartographer"), EntityCartographer.class, decorateEntityName("cartographer"), ENTITY_CARTOGRAPHER_ID, OpenBlocks.instance, 64, 8, true);
		EntityRegistry.registerModEntity(location("item_projectile"), EntityItemProjectile.class, decorateEntityName("item_projectile"), ENTITY_CANON_ITEM_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(location("golden_eye"), EntityGoldenEye.class, decorateEntityName("golden_eye"), ENTITY_GOLDEN_EYE_ID, OpenBlocks.instance, 64, 8, true);
		EntityRegistry.registerModEntity(location("mini_me"), EntityMiniMe.class, decorateEntityName("mini_me"), ENTITY_MINIME_ID, OpenBlocks.instance, 64, 1, true);
		EntityRegistry.registerModEntity(location("glyph"), EntityGlyph.class, decorateEntityName("glyph"), ENTITY_GLYPH_ID, OpenBlocks.instance, 160, Integer.MAX_VALUE, false);

		final DataFixer dataFixer = FMLCommonHandler.instance().getDataFixer();
		EntityLiving.registerFixesMob(dataFixer, EntityMiniMe.class);
		EntityLuggage.registerFixes(dataFixer);
		ItemLuggage.registerFixes(dataFixer);
		EntityBlock.registerFixes(dataFixer, EntityMountedBlock.class);
		EntityCartographer.registerFixes(dataFixer);
		ItemCartographer.registerFixes(dataFixer);
		EntityItemProjectile.registerFixes(dataFixer);
		EntityGoldenEye.registerFixes(dataFixer);
		ItemDevNull.registerFixes(dataFixer);

		MagnetWhitelists.instance.initTesters();

		MinecraftForge.EVENT_BUS.register(MapDataManager.instance);

		// Integration.addModule(new ModuleAdapters());
		// Integration.addModule(new ModuleTurtles());

		if (!Config.soSerious) {
			BrickManager.registerCapability();
			MinecraftForge.EVENT_BUS.register(new BrickManager());
		}

		MinecraftForge.EVENT_BUS.register(ElevatorBlockRules.instance);
		MinecraftForge.EVENT_BUS.register(new ElevatorActionHandler());

		if (Config.radioVillagerEnabled) {
			RadioVillagerTrades.registerUselessVillager();
		}

		if (Items.luggage != null) {
			MinecraftForge.EVENT_BUS.register(new LuggageDropHandler());
		}

		if (Blocks.guide != null || Blocks.builderGuide != null) {
			MinecraftForge.EVENT_BUS.register(new GuideActionHandler());
		}

		if (Items.xpBucket != null) {
			if (Config.xpBucketDirectFill) MinecraftForge.EVENT_BUS.register(new SingleFluidBucketFillHandler(new ItemStack(Items.xpBucket)));
		}

		if (Items.pedometer != null) {
			PedometerHandler.registerCapability();
		}

		if (Items.sleepingBag != null) MinecraftForge.EVENT_BUS.register(new ItemSleepingBag.IsSleepingHandler());

		MinecraftForge.EVENT_BUS.register(CanvasReplaceBlacklist.instance);

		MinecraftForge.EVENT_BUS.register(PlayerInventoryStore.instance);

		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

		MinecraftForge.EVENT_BUS.register(new GameRuleManager());

		LootHandler.register();

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		FluidXpUtils.initializeFromConfig();

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

		if (Blocks.tank != null) {
			if (Config.allowBucketDrain) {
				final ContainerBucketFillHandler tankFillHandler = new TileEntityTank.BucketFillHandler();

				for (ItemStack bucket : filledBuckets)
					tankFillHandler.addFilledBucket(bucket);

				filledBuckets.clear();

				for (String bucketId : Config.bucketItems) {
					final ResourceLocation bucketLocation = new ResourceLocation(bucketId);
					final Item item = Item.REGISTRY.getObject(bucketLocation);
					if (item == null) {
						Log.warn("Failed to find bucket item %s", item);
					} else {
						tankFillHandler.addFilledBucket(new ItemStack(item));
					}
				}

				MinecraftForge.EVENT_BUS.register(tankFillHandler);
			}
		}
	}

	private final List<ItemStack> filledBuckets = Lists.newArrayList();

	@EventHandler
	public void processMessage(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage m : event.getMessages()) {
			if (m.isStringMessage() && "donateUrl".equalsIgnoreCase(m.key)) {
				DonationUrlManager.instance().addUrl(m.getSender(), m.getStringValue());
			}

			if (m.isItemStackMessage() && "bucket".equalsIgnoreCase(m.key)) {
				filledBuckets.add(m.getItemStackValue());
			}
		}
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
