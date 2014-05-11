package openblocks;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openblocks.OpenBlocks.Enchantments;
import openblocks.asm.EntityPlayerVisitor;
import openblocks.client.radio.RadioManager;
import openblocks.client.radio.RadioManager.RadioStation;
import openblocks.common.EntityEventHandler;
import openblocks.common.Stencil;
import openblocks.common.TrophyHandler;
import openblocks.common.block.*;
import openblocks.common.item.*;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.common.recipe.*;
import openblocks.enchantments.*;
import openmods.config.*;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;

public class Config {

	@BlockId(description = "The id of the ladder")
	public static int blockLadderId = 2540;

	@BlockId(description = "The id of the guide")
	public static int blockGuideId = 2541;

	@BlockId(description = "The id of the elevator block")
	public static int blockElevatorId = 2542;

	@BlockId(description = "The id of the heal block")
	public static int blockHealId = 2543;

	@BlockId(description = "The id of the target block")
	public static int blockTargetId = 2545;

	@BlockId(description = "The id of the grave block")
	public static int blockGraveId = 2546;

	@BlockId(description = "The id of the flag block")
	public static int blockFlagId = 2547;

	@BlockId(description = "The id of the tank block")
	public static int blockTankId = 2548;

	@BlockId(description = "The id of the trophy block")
	public static int blockTrophyId = 2549;

	@BlockId(description = "The id of the bear trap")
	public static int blockBearTrapId = 2550;

	@BlockId(description = "The id of the sprinkler block")
	public static int blockSprinklerId = 2551;

	@BlockId(description = "The id of the cannon block")
	public static int blockCannonId = 2552;

	@BlockId(description = "The id of the vacuum hopper block")
	public static int blockVacuumHopperId = 2553;

	@BlockId(description = "The id of the sponge block")
	public static int blockSpongeId = 2554;

	@BlockId(description = "The id of the big button block")
	public static int blockBigButton = 2555;

	@BlockId(description = "The id of the imaginary block")
	public static int blockImaginaryId = 2556;

	@BlockId(description = "The id of the xp bottler block")
	public static int blockXPBottlerId = 2557;

	@BlockId(description = "The id of the village highlighter block")
	public static int blockVillageHighlighterId = 2558;

	@BlockId(description = "The id of the path block")
	public static int blockPathId = 2559;

	@BlockId(description = "The id of the auto anvil")
	public static int blockAutoAnvilId = 2560;

	@BlockId(description = "The id of the auto enchantment table")
	public static int blockAutoEnchantmentTableId = 2561;

	@BlockId(description = "The id of the xp drain block")
	public static int blockXPDrainId = 2562;

	@BlockId(description = "The id of the block-breaker block")
	public static int blockBlockBreakerId = 2563;

	@BlockId(description = "The id of the block-placer block")
	public static int blockBlockPlacerId = 2564;

	@BlockId(description = "The id of the item-dropper block")
	public static int blockItemDropperId = 2565;

	@BlockId(description = "The id of the rope ladder block")
	public static int blockRopeLadderId = 2566;

	@BlockId(description = "The id of the donation station block")
	public static int blockDonationStationId = 2567;

	@BlockId(description = "The id of the clay stainer block")
	public static int blockPaintMixer = 2568;

	@BlockId(description = "The id of the canvas block")
	public static int blockCanvasId = 2569;

	@BlockId(description = "The id of the paint tin")
	public static int blockPaintCanId = 2571;

	@BlockId(description = "The id of the glass canvas block")
	public static int blockCanvasGlassId = 2572;

	@BlockId(description = "The id of the map projector block")
	public static int blockProjectorId = 2573;

	@BlockId(description = "The id of the drawing table")
	public static int blockDrawingTable = 2574;

	@BlockId(description = "The id of the golden egg block")
	public static int blockGoldenEggId = 2575;

	@BlockId(description = "The id of the fan block")
	public static int blockFanId = 2576;

	@BlockId(description = "The id of the radio block")
	public static int blockRadioId = 2577;

	@BlockId(description = "The id of the sky block")
	public static int blockSkyId = 2578;

	@BlockId(description = "The id of the xp shower")
	public static int blockXPShowerId = 2579;

	@BlockId(description = "The id of the digital fuse")
	public static int blockDigitalFuseId = 2580;

	@ItemId(description = "The id of the hang glider")
	public static int itemHangGliderId = 14975;

	@ItemId(description = "The id of the generic item")
	public static int itemGenericId = 14976;

	@ItemId(description = "The id of the luggage item")
	public static int itemLuggageId = 14977;

	@ItemId(description = "The id of the sonic glasses item")
	public static int itemSonicGlassesId = 14978;

	@ItemId(description = "The id of the imaginary pencil glasses item")
	public static int itemGlassesPencil = 14979;

	@ItemId(description = "The id of the imaginary crayon glasses item")
	public static int itemGlassesCrayon = 14980;

	@ItemId(description = "The id of the amazing technicolor glasses item")
	public static int itemGlassesTechnicolor = 14981;

	@ItemId(description = "The id of the serious admin glasses item")
	public static int itemGlassesSerious = 14982;

	@ItemId(description = "The id of the crane controller item")
	public static int itemCraneControl = 14983;

	@ItemId(description = "The id of the crane backpack item")
	public static int itemCraneId = 14984;

	@ItemId(description = "The id of slimalyzer item")
	public static int itemSlimalyzerId = 14985;

	@ItemId(description = "The id of the filled bucket")
	public static int itemFilledBucketId = 14986;

	@ItemId(description = "The id of the sleeping bag")
	public static int itemSleepingBagId = 14987;

	@ItemId(description = "The id of the paint brush")
	public static int itemPaintBrushId = 14988;

	@ItemId(description = "The id of the stencil")
	public static int itemStencilId = 14989;

	@ItemId(description = "The id of the Squeegee")
	public static int itemSqueegeeId = 14990;

	@ItemId(description = "The id of the height map item")
	public static int itemHeightMap = 14991;

	@ItemId(description = "The id of the empty height map item")
	public static int itemEmptyMap = 14992;

	@ItemId(description = "The id of the cartographer spawner item")
	public static int itemCartographerId = 14993;

	@ItemId(description = "The id of the tasty clay item!")
	public static int itemTastyClay = 14994;

	@ItemId(description = "The id of the golden eye item")
	public static int itemGoldenEyeId = 14995;

	@ItemId(description = "The id of the unstackable generic item")
	public static int itemGenericUnstackableId = 14996;

	@ItemId(description = "The id of the cursor item")
	public static int itemCursorId = 14997;

	@ItemId(description = "The id of tuned crystal item")
	public static int itemTunedCrystalId = 14998;

	@ItemId(description = "The id of info book")
	public static int itemInfoBookId = 14999;

	@ItemId(description = "The id of wallpaper")
	public static int itemWallpaperId = 15000;

	@ItemId(description = "The id of the dev null item")
	public static int itemDevNullId = 15001;

	@ItemId(description = "The id of sponge on a stick")
	public static int itemSpongeOnAStickId = 15002;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "searchDistance", comment = "The range of the drop block")
	public static int elevatorTravelDistance = 20;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "ignoreHalfBlocks", comment = "The elevator will ignore half blocks when counting the blocks it can pass through")
	public static boolean elevatorIgnoreHalfBlocks = false;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "maxPassThrough", comment = "The maximum amount of blocks the elevator can pass through before the teleport fails. -1 disables this")
	public static int elevatorMaxBlockPassCount = 4;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "specialBlockRules", comment = "Defines blocks that are handled specially by elevators. Entries are in form <modId>:<blockName>:<action> or id:<blockId>:<action>. Possible actions: abort (elevator can't pass block), increment (counts for elevatorMaxBlockPassCount limit) and ignore")
	public static String[] elevatorRules = new String[0];

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "elevatorDrainsXP", comment = "Does the elevator drain player XP when used?")
	public static boolean elevatorDrainsXP = true;

	@ConfigProperty(category = "tanks", name = "bucketsPerTank", comment = "The amount of buckets each tank can hold")
	public static int bucketsPerTank = 16;

	@ConfigProperty(category = "hacks", name = "tryHookPlayerRenderer", comment = "Allow OpenBlocks to hook the player renderer to apply special effects")
	public static boolean tryHookPlayerRenderer = true;

	@OnLineModifiable
	@ConfigProperty(category = "trophy", name = "trophyDropChance", comment = "The chance (from 0 to 1) of a trophy drop. for example, 0.001 for 1/1000")
	public static double trophyDropChance = 0.001;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "irregularBlocksArePassable", comment = "The elevator will try to pass through blocks that have custom collision boxes")
	public static boolean irregularBlocksArePassable = true;

	@OnLineModifiable
	@ConfigProperty(category = "tanks", name = "emitLight", comment = "Tanks will emit light when they contain a liquid that glows (eg. lava)")
	public static boolean tanksEmitLight = true;

	@OnLineModifiable
	@ConfigProperty(category = "sprinkler", name = "fertilizeChance", comment = "1/chance that crops will be fertilized without bonemeal")
	public static int sprinklerFertilizeChance = 500;

	@OnLineModifiable
	@ConfigProperty(category = "sprinkler", name = "bonemealFertilizeChance", comment = "1/chance that crops will be fertilized with bonemeal")
	public static int sprinklerBonemealFertizizeChance = 200;

	@OnLineModifiable
	@ConfigProperty(category = "sprinkler", name = "effectiveRange", comment = "The range in each cardinal direction that crops will be affected.")
	public static int sprinklerEffectiveRange = 4;

	@OnLineModifiable
	@ConfigProperty(category = "glasses", name = "opacity", comment = "0.0 - no visible change to world, 1.0 - world fully obscured")
	public static double sonicGlassesOpacity = 0.95;

	@OnLineModifiable
	@ConfigProperty(category = "glasses", name = "useTexture", comment = "Use texture for obscuring world")
	public static boolean sonicGlassesUseTexture = true;

	@OnLineModifiable
	@ConfigProperty(category = "imaginary", name = "fadingSpeed", comment = "Speed of imaginary blocks fading/appearing")
	public static float imaginaryFadingSpeed = 0.0075f;

	@ConfigProperty(category = "imaginary", name = "numberOfUses", comment = "Number of newly created crayon/pencil uses")
	public static float imaginaryItemUseCount = 10;

	@ConfigProperty(category = "crane", name = "doCraneCollisionCheck", comment = "Enable collision checking of crane arm")
	public static boolean doCraneCollisionCheck = false;

	@OnLineModifiable
	@ConfigProperty(category = "crane", name = "boringMode", comment = "Use shift to control crane direction (otherwise, toggle every time)")
	public static boolean craneShiftControl = true;

	@ConfigProperty(category = "crane", name = "turtleMagnetRange", comment = "Range of magnet CC peripheral")
	public static double turtleMagnetRange = 4;

	@ConfigProperty(category = "crane", name = "addTurtles", comment = "Enable magnet turtles")
	public static boolean enableCraneTurtles = true;

	@ConfigProperty(category = "crane", name = "showTurtles", comment = "Show magnet turtles in creative list")
	public static boolean showCraneTurtles = true;

	@ConfigProperty(category = "hacks", name = "enableExperimentalFeatures", comment = "Enable experimental features that may be buggy or broken entirely")
	public static boolean experimentalFeatures = false;

	@ConfigProperty(category = "tomfoolery", name = "weAreSeriousPeople", comment = "Are you serious too?")
	public static boolean soSerious = true;

	@OnLineModifiable
	@ConfigProperty(category = "tomfoolery", name = "doItWhileTyping", comment = "You know, THAT thing! That you shouldn't do in public!")
	public static boolean fartTypying = false;

	@OnLineModifiable
	@ConfigProperty(category = "tomfoolery", name = "flimFlamBlacklist", comment = "Blacklist for effects used by flim-flam enchantment")
	public static String[] flimFlamBlacklist = new String[0];

	@OnLineModifiable
	@ConfigProperty(category = "tomfoolery", name = "safeOnly", comment = "Allow only flimflams that don't cause death (or at least very rarely)")
	public static boolean safeFlimFlams = false;

	@OnLineModifiable
	@ConfigProperty(category = "debug", name = "goldenEyeDebug", comment = "Show structures found by golden eye")
	public static boolean eyeDebug = false;

	@OnLineModifiable
	@ConfigProperty(category = "debug", name = "enableChangelogBooks", comment = "Enable the changelog books")
	public static boolean enableChangelogBooks = true;

	@OnLineModifiable
	@ConfigProperty(category = "debug", name = "gravesDebug", comment = "Dump extra amount of data, every time grave is created")
	public static boolean debugGraves = false;

	@ConfigProperty(category = "features", name = "explosiveEnchantmentId", comment = "Id of explosive enchantment")
	public static int explosiveEnchantmentId = 211;

	@ConfigProperty(category = "features", name = "lastStandEnchantmentId", comment = "Id of last stand enchantment")
	public static int lastStandEnchantmentId = 212;

	@ConfigProperty(category = "features", name = "flimFlamEnchantmentId", comment = "Id of flim flam enchantment")
	public static int flimFlamEnchantmentId = 213;

	@ConfigProperty(category = "features", name = "explosiveEnchantGrief", comment = "Explosive enchant can break blocks at level 3")
	public static boolean explosiveEnchantGrief = true;

	@ConfigProperty(category = "cursor", name = "cursorMaxDamage", comment = "Amount of damage a cursor can take")
	public static int cursorMaxDamage = 128;

	@OnLineModifiable
	@ConfigProperty(category = "additional", name = "disableMobNames", comment = "List any mob names you want disabled on the server")
	public static String[] disableMobNames = new String[0];

	@OnLineModifiable
	@ConfigProperty(category = "radio", name = "radioStations", comment = "List any radio stations you want")
	public static String[] radioStations = new String[] {
			"http://idobiradio.idobi.com;idobi Radio (idobi.com);Blue",
			"http://192.184.9.79:8006;CINEMIX;Blue",
			"http://radiorivendell.de:80/;Radio Rivendell;Blue",
			"http://205.164.62.15:10052;1.fm Love Classics;Blue",
			"http://theradio.cc:8000/trcc-stream.mp3;TheRadioCC;Red",
			"http://streaming202.radionomy.com:80/abacusfm-vintage-jazz;Vintage Jazz;Red",
			"http://s1.stream-experts.net:8032/;Nature sounds;Red",
			"http://91.121.166.38:7016/;British Comedy;Red",
			"http://50.7.173.162:8010;Audiophile Baroque;Red"
	};

	@ConfigProperty(category = "radio", name = "replaceList", comment = "List of URLs that need to be updated (url, whitespace, replacement")
	public static String[] derpList = new String[] {
			"http://69.46.75.101:80 http://idobiradio.idobi.com"
	};

	@OnLineModifiable
	@ConfigProperty(category = "cartographer", name = "blockBlacklist", comment = "List of blocks that should be invisible to cartographer. Example: id:3,  OpenBlocks:openblocks_radio (case sensitive)")
	public static String[] mapBlacklist = new String[] {
			"Natura:Cloud"
	};

	@ConfigProperty(category = "radio", name = "enableChestLoot", comment = "Add tuned crystals as loot in chests")
	public static boolean radioChests = true;

	@ConfigProperty(category = "radio", name = "radioVillagerId", comment = "Tuned crystals merchant id (-1 to disable)")
	public static int radioVillagerId = 6156;

	@OnLineModifiable
	@ConfigProperty(category = "radio", name = "radioVillagerSellsRecords", comment = "Should tuned crystals sell records too")
	public static boolean radioVillagerRecords = true;

	@OnLineModifiable
	@ConfigProperty(category = "radio", name = "maxSources", comment = "Maximum number of sources playing at one time")
	public static int maxRadioSources = 3;

	@OnLineModifiable
	@ConfigProperty(category = "fan", name = "fanForce", comment = "Maximum force applied every tick to entities nearby (linear decay)")
	public static double fanForce = 0.05;

	@OnLineModifiable
	@ConfigProperty(category = "fan", name = "fanRange", comment = "Range of fan in blocks")
	public static double fanRange = 10;

	@OnLineModifiable
	@ConfigProperty(category = "fan", name = "isRedstoneActivated", comment = "Is fan force controlled by redstone current")
	public static boolean redstoneActivatedFan = true;

	@ConfigProperty(category = "sponge", name = "spongeStickUseCount", comment = "SpongeOnAStick use count")
	public static int spongeMaxDamage = 256;

	@OnLineModifiable
	@ConfigProperty(category = "sponge", name = "spongeRange", comment = "Sponge block range (distance from center)")
	public static int spongeRange = 3;

	@OnLineModifiable
	@ConfigProperty(category = "sponge", name = "spongeRange", comment = "Sponge block range (distance from center)")
	public static int spongeStickRange = 3;

	public static void register() {

		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		// There is no fail checking here because if the Generic item fails,
		// then I doubt anyone wants this to be silent.
		// Too many items would suffer from this. - NC
		OpenBlocks.Items.generic = new ItemOBGeneric();
		MetasGeneric.registerItems();
		OpenBlocks.Items.generic.initRecipes();

		OpenBlocks.Items.genericUnstackable = new ItemOBGenericUnstackable();
		MetasGenericUnstackable.registerItems();
		OpenBlocks.Items.genericUnstackable.initRecipes();

		if (itemFilledBucketId > 0) {
			OpenBlocks.Items.filledBucket = new ItemFilledBucket();
			MetasBucket.registerItems();
		}

		if (ConfigProcessing.canRegisterBlock(blockLadderId)) {
			OpenBlocks.Blocks.ladder = new BlockLadder();
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Blocks.ladder, Block.ladder, Block.trapdoor));
		}
		if (ConfigProcessing.canRegisterBlock(blockGuideId)) {
			OpenBlocks.Blocks.guide = new BlockGuide();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.guide, "ggg", "gtg", "ggg", 'g', Block.glass, 't', Block.torchWood));
		}
		if (ConfigProcessing.canRegisterBlock(blockElevatorId)) {
			OpenBlocks.Blocks.elevator = new BlockElevator();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.elevator, "www", "wew", "www", 'w', Block.cloth, 'e', Item.enderPearl));
		}
		if (ConfigProcessing.canRegisterBlock(blockHealId)) {
			OpenBlocks.Blocks.heal = new BlockHeal();
		}
		if (ConfigProcessing.canRegisterBlock(blockTargetId)) {
			OpenBlocks.Blocks.target = new BlockTarget();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.target, "www", "www", "s s", 'w', Block.cloth, 's', "stickWood"));
		}
		if (ConfigProcessing.canRegisterBlock(blockGraveId)) {
			OpenBlocks.Blocks.grave = new BlockGrave();
		}
		if (ConfigProcessing.canRegisterBlock(blockFlagId)) {
			OpenBlocks.Blocks.flag = new BlockFlag();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.flag, 3), "scc", "sc ", "s  ", 'c', Block.carpet, 's', "stickWood"));
		}
		if (ConfigProcessing.canRegisterBlock(blockTankId)) {
			OpenBlocks.Blocks.tank = new BlockTank();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.tank, 2), "ogo", "ggg", "ogo", 'g', Block.thinGlass, 'o', Block.obsidian));
		}
		if (ConfigProcessing.canRegisterBlock(blockTrophyId)) {
			OpenBlocks.Blocks.trophy = new BlockTrophy();
			MinecraftForge.EVENT_BUS.register(new TrophyHandler());
		}
		if (ConfigProcessing.canRegisterBlock(blockBearTrapId)) {
			OpenBlocks.Blocks.bearTrap = new BlockBearTrap();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.bearTrap, "fif", "fif", "fif", 'f', Block.fenceIron, 'i', Item.ingotIron));
		}

		if (ConfigProcessing.canRegisterBlock(blockSprinklerId)) {
			OpenBlocks.Blocks.sprinkler = new BlockSprinkler();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.sprinkler, "ifi", "iri", "ifi", 'i', Item.ingotIron, 'r', Block.torchRedstoneActive, 'f', Block.fenceIron));
		}

		if (ConfigProcessing.canRegisterBlock(blockCannonId)) {
			OpenBlocks.Blocks.cannon = new BlockCannon();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.cannon, " d ", " f ", "iri", 'd', Block.dispenser, 'f', Block.fenceIron, 'i', Item.ingotIron, 'r', Block.blockRedstone));
		}

		if (ConfigProcessing.canRegisterBlock(blockVacuumHopperId)) {
			OpenBlocks.Blocks.vacuumHopper = new BlockVacuumHopper();
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Blocks.vacuumHopper, Block.hopperBlock, Block.obsidian, Item.enderPearl));
		}

		if (ConfigProcessing.canRegisterBlock(blockSpongeId)) {
			OpenBlocks.Blocks.sponge = new BlockSponge();
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Blocks.sponge, Block.cloth, Item.slimeBall));
		}

		if (ConfigProcessing.canRegisterBlock(blockBigButton)) {
			OpenBlocks.Blocks.bigButton = new BlockBigButton();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.bigButton, "bb", "bb", 'b', Block.stoneButton));
		}

		if (ConfigProcessing.canRegisterBlock(blockImaginaryId)) {
			OpenBlocks.Blocks.imaginary = new BlockImaginary();
			{
				ItemStack pencil = ItemImaginary.setupValues(null, new ItemStack(OpenBlocks.Blocks.imaginary, 1, ItemImaginary.DAMAGE_PENCIL));
				recipeList.add(new ShapelessOreRecipe(pencil, Item.coal, "stickWood", Item.enderPearl, Item.slimeBall));
			}

			for (ColorMeta color : ColorUtils.getAllColors()) {
				ItemStack crayon = ItemImaginary.setupValues(color.rgb, new ItemStack(OpenBlocks.Blocks.imaginary, 1, ItemImaginary.DAMAGE_CRAYON));
				recipeList.add(new ShapelessOreRecipe(crayon, color.oreName, Item.paper, Item.enderPearl, Item.slimeBall));
			}

			recipeList.add(new CrayonMixingRecipe());
		}

		if (ConfigProcessing.canRegisterBlock(blockFanId)) {
			OpenBlocks.Blocks.fan = new BlockFan();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.fan, "f", "i", "s", 'f', Block.fenceIron, 'i', Item.ingotIron, 's', Block.stoneSingleSlab));
		}

		if (ConfigProcessing.canRegisterBlock(blockXPBottlerId)) {
			OpenBlocks.Blocks.xpBottler = new BlockXPBottler();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.xpBottler, "iii", "ibi", "iii", 'i', Item.ingotIron, 'b', Item.glassBottle));
		}

		if (ConfigProcessing.canRegisterBlock(blockVillageHighlighterId)) {
			OpenBlocks.Blocks.villageHighlighter = new BlockVillageHighlighter();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.villageHighlighter, "www", "wew", "ccc", 'w', "plankWood", 'e', Item.emerald, 'c', Block.cobblestone));
		}

		if (ConfigProcessing.canRegisterBlock(blockPathId)) {
			OpenBlocks.Blocks.path = new BlockPath();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.path, 2), "stone", "cobblestone"));
		}

		if (ConfigProcessing.canRegisterBlock(blockAutoAnvilId)) {
			OpenBlocks.Blocks.autoAnvil = new BlockAutoAnvil();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.autoAnvil, "iii", "iai", "rrr", 'i', Item.ingotIron, 'a', Block.anvil, 'r', Item.redstone));
		}

		if (ConfigProcessing.canRegisterBlock(blockAutoEnchantmentTableId)) {
			OpenBlocks.Blocks.autoEnchantmentTable = new BlockAutoEnchantmentTable();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.autoEnchantmentTable, "iii", "iei", "rrr", 'i', Item.ingotIron, 'e', Block.enchantmentTable, 'r', Item.redstone));
		}

		if (ConfigProcessing.canRegisterBlock(blockXPDrainId)) {
			OpenBlocks.Blocks.xpDrain = new BlockXPDrain();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.xpDrain, "iii", "iii", "iii", 'i', Block.fenceIron));
		}
		if (ConfigProcessing.canRegisterBlock(blockBlockBreakerId)) {
			OpenBlocks.Blocks.blockBreaker = new BlockBlockBreaker();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.blockBreaker, "icc", "src", "icc", 'i', Item.ingotIron, 'c', Block.cobblestone, 'r', Item.redstone, 's', Item.pickaxeDiamond));
		}

		if (ConfigProcessing.canRegisterBlock(blockBlockPlacerId)) {
			OpenBlocks.Blocks.blockPlacer = new BlockBlockPlacer();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.blockPlacer, "icc", "src", "icc", 'i', Item.ingotIron, 'c', Block.cobblestone, 'r', Item.redstone, 's', Block.pistonBase));
		}

		if (ConfigProcessing.canRegisterBlock(blockItemDropperId)) {
			OpenBlocks.Blocks.itemDropper = new BlockItemDropper();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.itemDropper, "icc", "src", "icc", 'i', Item.ingotIron, 'c', Block.cobblestone, 'r', Item.redstone, 's', Block.hopperBlock));
		}

		if (ConfigProcessing.canRegisterBlock(blockRopeLadderId)) {
			OpenBlocks.Blocks.ropeLadder = new BlockRopeLadder();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.ropeLadder, "sts", "sts", "sts", 't', "stickWood", 's', Item.silk));
		}

		if (ConfigProcessing.canRegisterBlock(blockDonationStationId)) {
			OpenBlocks.Blocks.donationStation = new BlockDonationStation();
			WeightedRandomChestContent drop = new WeightedRandomChestContent(new ItemStack(OpenBlocks.Blocks.donationStation), 1, 1, 2);
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
			ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.donationStation, "ppp", "pcp", "ppp", 'p', Item.porkRaw, 'c', Block.chest));
		}

		if (ConfigProcessing.canRegisterBlock(blockPaintMixer)) {
			OpenBlocks.Blocks.paintMixer = new BlockPaintMixer();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.paintMixer, "ooo", "i i", "iii", 'o', Block.obsidian, 'i', Item.ingotIron));
		}

		if (ConfigProcessing.canRegisterBlock(blockCanvasId)) {
			OpenBlocks.Blocks.canvas = new BlockCanvas();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.canvas, 9), "ppp", "pfp", "ppp", 'p', Item.paper, 'f', Block.fence));
		}

		if (ConfigProcessing.canRegisterBlock(blockPaintCanId)) {
			OpenBlocks.Blocks.paintCan = new BlockPaintCan();
		}

		if (ConfigProcessing.canRegisterBlock(blockCanvasGlassId)) {
			OpenBlocks.Blocks.canvasGlass = new BlockCanvasGlass();
		}

		if (ConfigProcessing.canRegisterBlock(blockProjectorId)) {
			OpenBlocks.Blocks.projector = new BlockProjector();
			final ItemStack lapis = new ItemStack(Item.dyePowder, 1, ColorUtils.BLUE);
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.projector, "grl", "iri", "srs", 's', Block.stoneSingleSlab, 'r', Item.redstone, 'g', Item.glowstone, 'i', Item.ingotIron, 'l', lapis));
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.projector, "lrg", "iri", "srs", 's', Block.stoneSingleSlab, 'r', Item.redstone, 'g', Item.glowstone, 'i', Item.ingotIron, 'l', lapis));
		}

		if (ConfigProcessing.canRegisterBlock(blockGoldenEggId)) {
			OpenBlocks.Blocks.goldenEgg = new BlockGoldenEgg();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.goldenEgg, "ggg", "geg", "ggg", 'g', Item.ingotGold, 'e', Item.egg));
		}

		if (ConfigProcessing.canRegisterBlock(blockSkyId)) {
			OpenBlocks.Blocks.sky = new BlockSky();
			final ItemStack normal6 = new ItemStack(OpenBlocks.Blocks.sky, 6, 0);
			final ItemStack normal = new ItemStack(OpenBlocks.Blocks.sky, 1, 1);
			final ItemStack inverted = new ItemStack(OpenBlocks.Blocks.sky, 1, 0);
			recipeList.add(new ShapedOreRecipe(normal6, "geg", "gsg", "geg", 'g', Block.glass, 'e', Item.enderPearl, 's', Block.whiteStone));
			recipeList.add(new ShapelessOreRecipe(inverted, normal, Block.torchRedstoneActive));
			recipeList.add(new ShapelessOreRecipe(normal, inverted, Block.torchRedstoneActive));
		}

		if (ConfigProcessing.canRegisterBlock(blockDrawingTable)) {
			OpenBlocks.Blocks.drawingTable = new BlockDrawingTable();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.drawingTable, "sks", "pcp", "ppp", 'p', Block.planks, 'c', Block.workbench, 's', MetasGeneric.unpreparedStencil.newItemStack(), 'k', MetasGeneric.sketchingPencil.newItemStack()));
		}

		if (ConfigProcessing.canRegisterBlock(blockRadioId)) {
			OpenBlocks.Blocks.radio = new BlockRadio();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.radio, "pbp", "prp", "pgp", 'p', "plankWood", 'b', Block.fenceIron, 'r', Item.redstone, 'g', Item.ingotGold));
		}

		if (ConfigProcessing.canRegisterBlock(blockXPShowerId)) {
			OpenBlocks.Blocks.xpShower = new BlockXPShower();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.xpShower, "iii", "  o", 'i', Item.ingotIron, 'o', Block.obsidian));
		}

		if (ConfigProcessing.canRegisterBlock(blockDigitalFuseId)) {
			OpenBlocks.Blocks.digitalFuse = new BlockDigitalFuse();
		}

		if (itemHangGliderId > 0) {
			OpenBlocks.Items.hangGlider = new ItemHangGlider();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.hangGlider, "wsw", 'w', MetasGeneric.gliderWing.newItemStack(), 's', "stickWood"));
		}

		if (itemLuggageId > 0) {
			OpenBlocks.Items.luggage = new ItemLuggage();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.luggage, "sds", "scs", "sss", 's', "stickWood", 'd', Item.diamond, 'c', Block.chest));
		}

		if (itemSonicGlassesId > 0) {
			OpenBlocks.Items.sonicGlasses = new ItemSonicGlasses();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.sonicGlasses, "ihi", "oso", "   ", 's', "stickWood", 'h', Item.helmetIron, 'o', Block.obsidian, 'i', Item.ingotIron));
			ItemStack stack = new ItemStack(OpenBlocks.Items.sonicGlasses);
			WeightedRandomChestContent drop = new WeightedRandomChestContent(stack, 1, 1, 2);
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
		}

		if (OpenBlocks.Blocks.imaginary != null) {
			if (itemGlassesPencil > 0) {
				OpenBlocks.Items.pencilGlasses = new ItemImaginationGlasses(itemGlassesPencil, ItemImaginationGlasses.Type.PENCIL);
				ItemStack block = new ItemStack(OpenBlocks.Blocks.imaginary, 1, ItemImaginary.DAMAGE_PENCIL);
				ItemImaginary.setupValues(null, block);
				recipeList.add(new ShapelessOreRecipe(OpenBlocks.Items.pencilGlasses, block, Item.paper));
			}

			if (itemGlassesCrayon > 0) {
				OpenBlocks.Items.crayonGlasses = new ItemCrayonGlasses(itemGlassesCrayon);
				recipeList.add(new CrayonGlassesRecipe());
			}

			if (itemGlassesTechnicolor > 0) {
				OpenBlocks.Items.technicolorGlasses = new ItemImaginationGlasses(itemGlassesTechnicolor, ItemImaginationGlasses.Type.TECHNICOLOR);
				WeightedRandomChestContent drop = new WeightedRandomChestContent(new ItemStack(OpenBlocks.Items.technicolorGlasses), 1, 1, 2);
				ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
				ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			}

			if (itemGlassesSerious > 0) {
				OpenBlocks.Items.seriousGlasses = new ItemImaginationGlasses(itemGlassesSerious, ItemImaginationGlasses.Type.BASTARD);
			}
		}

		if (itemCraneControl > 0) {
			OpenBlocks.Items.craneControl = new ItemCraneControl();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.craneControl, "ili", "grg", "iri", 'i', Item.ingotIron, 'g', Item.goldNugget, 'l', Item.glowstone, 'r', Item.redstone));
		}

		if (itemCraneId > 0) {
			OpenBlocks.Items.craneBackpack = new ItemCraneBackpack();
			ItemStack line = MetasGeneric.line.newItemStack();
			ItemStack beam = MetasGeneric.beam.newItemStack();
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Items.craneBackpack, MetasGeneric.craneEngine.newItemStack(), MetasGeneric.craneMagnet.newItemStack(), beam, beam, line, line, line, Item.leather));
		}

		if (itemSlimalyzerId > 0) {
			OpenBlocks.Items.slimalyzer = new ItemSlimalyzer();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.slimalyzer, "igi", "isi", "iri", 'i', Item.ingotIron, 'g', Block.thinGlass, 's', Item.slimeBall, 'r', Item.redstone));
		}

		if (itemSleepingBagId > 0 && EntityPlayerVisitor.IsInBedHookSuccess) {
			OpenBlocks.Items.sleepingBag = new ItemSleepingBag();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.sleepingBag, "cc ", "www", "ccw", 'c', Block.carpet, 'w', Block.cloth));
		}

		if (itemPaintBrushId > 0) {
			OpenBlocks.Items.paintBrush = new ItemPaintBrush();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.paintBrush, "w  ", " s ", "  s", 'w', Block.cloth, 's', "stickWood"));
			int[] colors = new int[] { 0xFF0000, 0x00FF00, 0x0000FF };
			for (int color : colors) {
				ItemStack stack = ItemPaintBrush.createStackWithColor(color);
				WeightedRandomChestContent drop = new WeightedRandomChestContent(stack, 1, 1, 2);
				ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
				ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(drop);
				ChestGenHooks.getInfo(ChestGenHooks.BONUS_CHEST).addItem(drop);
				ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			}
		}

		if (itemStencilId > 0) {
			OpenBlocks.Items.stencil = new ItemStencil();
			for (Stencil stencil : Stencil.values()) {
				WeightedRandomChestContent drop = new WeightedRandomChestContent(new ItemStack(OpenBlocks.Items.stencil, 1, stencil.ordinal()), 1, 1, 2);
				ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
				ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			}
		}

		if (itemSqueegeeId > 0) {
			OpenBlocks.Items.squeegee = new ItemSqueegee();
			if (OpenBlocks.Blocks.sponge != null) {
				recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.squeegee, "sss", " w ", " w ", 's', OpenBlocks.Blocks.sponge, 'w', "stickWood"));
			} else {
				recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.squeegee, "sss", " w ", " w ", 's', Item.slimeBall, 'w', "stickWood"));
			}
		}

		if (itemHeightMap > 0) {
			OpenBlocks.Items.heightMap = new ItemHeightMap();
			if (itemEmptyMap > 0) recipeList.add(new MapCloneRecipe());
		}

		if (itemEmptyMap > 0) {
			OpenBlocks.Items.emptyMap = new ItemEmptyMap();
			recipeList.add(new MapResizeRecipe());

			ItemStack memory = MetasGeneric.mapMemory.newItemStack(2);
			ItemStack cpu = MetasGeneric.mapController.newItemStack(1);
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.emptyMap.createMap(0),
					" m ", "mcm", " m ", 'm', memory, 'c', cpu));
		}

		if (itemCartographerId > 0) {
			OpenBlocks.Items.cartographer = new ItemCartographer();
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Items.cartographer, MetasGeneric.assistantBase.newItemStack(), Item.eyeOfEnder));
		}

		if (itemGoldenEyeId > 0) {
			OpenBlocks.Items.goldenEye = new ItemGoldenEye();
			recipeList.add(new GoldenEyeRechargeRecipe());
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.goldenEye, 1, ItemGoldenEye.MAX_DAMAGE), "ggg", "geg", "ggg", 'g', Item.goldNugget, 'e', Item.eyeOfEnder));
		}

		if (itemTastyClay > 0) {
			OpenBlocks.Items.tastyClay = new ItemTastyClay();
			final ItemStack cocoa = new ItemStack(Item.dyePowder, 1, ColorUtils.BROWN);
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Items.tastyClay, 2), Item.clay, Item.bucketMilk, cocoa));
		}

		if (itemCursorId > 0) {
			OpenBlocks.Items.cursor = new ItemCursor();
			final ItemStack whiteWool = ColorUtils.createDyedWool(ColorUtils.WHITE);
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.cursor, "w  ", "www", "www", 'w', whiteWool));
		}

		if (itemTunedCrystalId > 0) {
			OpenBlocks.Items.tunedCrystal = new ItemTunedCrystal();

			for (RadioStation station : RadioManager.instance.getRadioStations()) {
				WeightedRandomChestContent drop = new WeightedRandomChestContent(station.getStack().copy(), 1, 1, 2);
				ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
				ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			}
		}

		if (itemInfoBookId > 0) {
			OpenBlocks.Items.infoBook = new ItemInfoBook();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Items.infoBook), Item.clay, Item.book));
		}

		if (itemDevNullId > 0) {
			OpenBlocks.Items.devNull = new ItemDevNull();
			MinecraftForge.EVENT_BUS.register(OpenBlocks.Items.devNull);
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Items.devNull), Block.cobblestone, Item.appleRed));
		}

		if (itemWallpaperId > 0) {
			// OpenBlocks.Items.wallpaper = new ItemWallpaper();
		}

		if (itemSpongeOnAStickId > 0) {
			OpenBlocks.Items.spongeonastick = new ItemSpongeOnAStick();
			if (OpenBlocks.Blocks.sponge != null) {
				recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.spongeonastick, " s ", " w ", " w ", 's', OpenBlocks.Blocks.sponge, 'w', "stickWood"));
			}
		}

		if (explosiveEnchantmentId > 0) {
			MinecraftForge.EVENT_BUS.register(new ExplosiveEnchantmentsHandler());
			Enchantments.explosive = new EnchantmentExplosive(explosiveEnchantmentId);
		}

		if (lastStandEnchantmentId > 0) {
			MinecraftForge.EVENT_BUS.register(new LastStandEnchantmentsHandler());
			Enchantments.lastStand = new EnchantmentLastStand(lastStandEnchantmentId);
		}

		if (flimFlamEnchantmentId > 0) {
			MinecraftForge.EVENT_BUS.register(new FlimFlamEnchantmentsHandler());
			Enchantments.flimFlam = new EnchantmentFlimFlam(flimFlamEnchantmentId);

			for (int i = 0; i < 4; i++) {
				int emeraldCount = 1 << i;
				ItemStack result = Item.enchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.flimFlam, i + 1));
				Object recipe[] = new Object[emeraldCount + 1];
				recipe[0] = Item.book;
				Arrays.fill(recipe, 1, recipe.length, Item.emerald);
				recipeList.add(new ShapelessOreRecipe(result, recipe));
			}

		}

		final String modId = "openblocks";
		ConfigProcessing.registerItems(OpenBlocks.Items.class, modId);
		ConfigProcessing.registerBlocks(OpenBlocks.Blocks.class, modId);
	}
}
