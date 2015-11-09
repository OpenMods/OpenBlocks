package openblocks;

import java.util.Arrays;
import java.util.List;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.*;
import openblocks.OpenBlocks.Enchantments;
import openblocks.common.Stencil;
import openblocks.common.TrophyHandler;
import openblocks.common.item.*;
import openblocks.common.recipe.*;
import openblocks.enchantments.*;
import openmods.config.properties.ConfigProperty;
import openmods.config.properties.OnLineModifiable;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.RegistryDelegate;

public class Config {

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "searchDistance", comment = "The range of the drop block")
	public static int elevatorTravelDistance = 20;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "ignoreAllBlocks", comment = "Disable limit of blocks between elevators (equivalent to maxPassThrough = infinity)")
	public static boolean elevatorIgnoreBlocks = false;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "ignoreHalfBlocks", comment = "The elevator will ignore half blocks when counting the blocks it can pass through")
	public static boolean elevatorIgnoreHalfBlocks = false;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "maxPassThrough", comment = "The maximum amount of blocks the elevator can pass through before the teleport fails")
	public static int elevatorMaxBlockPassCount = 4;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "centerOnBlock", comment = "Should elevator move player to center of block after teleporting	")
	public static boolean elevatorCenter = false;

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "specialBlockRules", comment = "Defines blocks that are handled specially by elevators. Entries are in form <modId>:<blockName>:<action> or id:<blockId>:<action>. Possible actions: abort (elevator can't pass block), increment (counts for elevatorMaxBlockPassCount limit) and ignore")
	public static String[] elevatorRules = new String[0];

	@OnLineModifiable
	@ConfigProperty(category = "dropblock", name = "elevatorXpDrainRatio", comment = "XP consumed by elevator (total amount = ratio * distance)")
	public static float elevatorXpDrainRatio = 0;

	@ConfigProperty(category = "tanks", name = "bucketsPerTank", comment = "The amount of buckets each tank can hold")
	public static int bucketsPerTank = 16;

	@OnLineModifiable
	@ConfigProperty(category = "tanks", name = "tankTicks", comment = "Should tanks try to balance liquid amounts with neighbours")
	public static boolean shouldTanksUpdate = true;

	@OnLineModifiable
	@ConfigProperty(category = "tanks", name = "displayAllFluids", comment = "Should filled tanks be searchable with creative menu")
	public static boolean displayAllFilledTanks = true;

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

	@OnLineModifiable
	@ConfigProperty(category = "crane", name = "turtleMagnetRange", comment = "Range of magnet CC peripheral")
	public static double turtleMagnetRange = 32;

	@OnLineModifiable
	@ConfigProperty(category = "crane", name = "turtleMagnetDeactivateRange", comment = "Maximal distance from turtle to magnet when deactivating")
	public static double turtleMagnetRangeDeactivate = 3;

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
	@ConfigProperty(category = "tomfoolery", name = "flimFlamBlacklist", comment = "Blacklist/Whitelist for effects used by flim-flam enchantment")
	public static String[] flimFlamList = new String[0];

	@OnLineModifiable
	@ConfigProperty(category = "tomfoolery", name = "reverseBlacklist", comment = "If true, flim-flam blacklist will become whitelist")
	public static boolean flimFlamWhitelist = false;

	@OnLineModifiable
	@ConfigProperty(category = "tomfoolery", name = "safeOnly", comment = "Allow only flimflams that don't cause death (or at least very rarely)")
	public static boolean safeFlimFlams = false;

	@OnLineModifiable
	@ConfigProperty(category = "debug", name = "goldenEyeDebug", comment = "Show structures found by golden eye")
	public static boolean eyeDebug = false;

	@OnLineModifiable
	@ConfigProperty(category = "debug", name = "gravesDebug", comment = "Dump extra amount of data, every time grave is created")
	public static boolean debugGraves = false;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "destructiveGraves", comment = "Try to overwrite blocks with graves if no suitable place is found on first try")
	public static boolean destructiveGraves = false;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "spawnRange", comment = "Size of cube searched for spaces suitable for grave spawning")
	public static int graveSpawnRange = 10;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "storeContents", comment = "Store contents of spawned graves into separate NBT files (can later be restored with ob_inventory command)")
	public static boolean backupGraves = true;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "spawnSkeletons", comment = "Should grave randomly spawn skeletons")
	public static boolean spawnSkeletons = true;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "skeletonSpawnRate", comment = "Spawn rate, range: 0..1, default: about 1 per 50s")
	public static double skeletonSpawnRate = 0.002;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "specialActionFrequency", comment = "Frequency of special action on grave digging, 0..1")
	public static double graveSpecialAction = 0.03;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "canSpawnBase", comment = "Can grave spawn single block of dirt when it has no block under?")
	public static boolean graveBase = true;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "voidFix", comment = "Should grave try to spawn when player died by falling into void? (false -> legacy behaviour)")
	public static boolean voidGraves = true;

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
	@ConfigProperty(category = "additional", name = "dumpDeadPlayersInventories", comment = "Should player inventories be stored after death (can be later restored with ob_inventory command)")
	public static boolean dumpStiffsStuff = true;

	@OnLineModifiable
	@ConfigProperty(category = "cartographer", name = "blockBlacklist", comment = "List of blocks that should be invisible to cartographer. Example: id:3,  OpenBlocks:openblocks_radio (case sensitive)")
	public static String[] mapBlacklist = new String[] {
			"Natura:Cloud"
	};

	@OnLineModifiable
	@ConfigProperty(category = "cartographer", name = "reportInvalidRequest", comment = "Should invalid height map request be always reported")
	public static boolean alwaysReportInvalidMapRequests = false;

	@ConfigProperty(category = "radio", name = "radioVillagerId", comment = "Music merchant id (-1 to disable)")
	public static int radioVillagerId = 6156;

	@OnLineModifiable
	@ConfigProperty(category = "radio", name = "radioVillagerSellsRecords", comment = "Should tuned crystals sell records too")
	public static boolean radioVillagerRecords = true;

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

	@OnLineModifiable
	@ConfigProperty(category = "loot", name = "donationStation")
	public static boolean donationStationLoot = false;

	@OnLineModifiable
	@ConfigProperty(category = "loot", name = "sonicGlasses")
	public static boolean sonicGlassesLoot = false;

	@OnLineModifiable
	@ConfigProperty(category = "loot", name = "technicolorGlasses")
	public static boolean technicolorGlassesLoot = true;

	@OnLineModifiable
	@ConfigProperty(category = "loot", name = "stencil")
	public static boolean stencilLoot = false;

	@OnLineModifiable
	@ConfigProperty(category = "loot", name = "paintBrush")
	public static boolean paintBrushLoot = false;

	@OnLineModifiable
	@ConfigProperty(category = "features", name = "infoBook", comment = "Should every player get info book on first login")
	public static boolean spamInfoBook = true;

	@ConfigProperty(category = "features", name = "xpToLiquidRatio", comment = "Storage in mB needed to store single XP point")
	public static int xpToLiquidRatio = 20;

	@OnLineModifiable
	@ConfigProperty(category = "guide", name = "redstoneSensitivity", comment = "How builder guide should react to redstone. 0 - not sensitive, 1 - powered == on, -1 - inverted")
	public static int guideRedstone = 1;

	@OnLineModifiable
	@ConfigProperty(category = "guide", name = "renderDistanceSq", comment = "Square of guide maximum render distance")
	public static double guideRenderRangeSq = 256 * 256;

	@OnLineModifiable
	@ConfigProperty(category = "scaffolding", name = "despawnRate", comment = "The rate at which scaffolding should break. 0 - fastest")
	public static int scaffoldingDespawnRate = 4;

	@OnLineModifiable
	@ConfigProperty(category = "egg", name = "pickBlocks", comment = "Can golden egg pick blocks while hatching (may lead to glitches)")
	public static boolean eggCanPickBlocks = true;

	@OnLineModifiable
	@ConfigProperty(category = "magnet", name = "pickEntities", comment = "Can crane magnet pick entities?")
	public static boolean canMagnetPickEntities = true;

	@OnLineModifiable
	@ConfigProperty(category = "magnet", name = "pickBlocks", comment = "Can crane magnet pick block?")
	public static boolean canMagnetPickBlocks = true;

	@OnLineModifiable
	@ConfigProperty(category = "ladder", name = "infiniteMode", comment = "If true, ladders will behave in old way: single item will place ladder all the way down, but it will not drop when broken")
	public static boolean infiniteLadder = false;

	public static void register() {
		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		// There is no fail checking here because if the Generic item fails,
		// then I doubt anyone wants this to be silent.
		// Too many items would suffer from this. - NC
		MetasGeneric.registerItems();
		OpenBlocks.Items.generic.initRecipes();

		MetasGenericUnstackable.registerItems();
		OpenBlocks.Items.genericUnstackable.initRecipes();

		if (OpenBlocks.Blocks.ladder != null) {
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Blocks.ladder, Blocks.ladder, Blocks.trapdoor));
		}

		if (OpenBlocks.Blocks.guide != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.guide, "grg", "gtg", "grg", 'g', "blockGlass", 't', Blocks.torch, 'r', "dustRedstone"));
		}

		if (OpenBlocks.Blocks.builderGuide != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.builderGuide, "grg", "ete", "grg", 'g', "blockGlass", 't', Blocks.torch, 'r', "dustRedstone", 'e', Items.ender_pearl));
		}

		if (OpenBlocks.Blocks.elevator != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.elevator, "www", "wew", "www", 'w', Blocks.wool, 'e', Items.ender_pearl));
		}

		if (OpenBlocks.Blocks.elevatorRotating != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.elevatorRotating, "wiw", "wew", "wiw", 'w', Blocks.wool, 'e', Items.ender_pearl, 'i', "ingotIron"));

			if (OpenBlocks.Blocks.elevator != null) {
				recipeList.add(new ShapelessOreRecipe(OpenBlocks.Blocks.elevatorRotating, OpenBlocks.Blocks.elevator, "ingotIron", "ingotIron"));
			}
		}

		if (OpenBlocks.Blocks.target != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.target, "www", "www", "s s", 'w', Blocks.wool, 's', "stickWood"));
		}

		if (OpenBlocks.Blocks.flag != null) {
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.flag, 3), "scc", "sc ", "s  ", 'c', Blocks.carpet, 's', "stickWood"));
		}
		if (OpenBlocks.Blocks.tank != null) {
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.tank, 2), "ogo", "ggg", "ogo", 'g', "paneGlass", 'o', Blocks.obsidian));
		}
		if (OpenBlocks.Blocks.trophy != null) {
			MinecraftForge.EVENT_BUS.register(new TrophyHandler());
		}
		if (OpenBlocks.Blocks.bearTrap != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.bearTrap, "fif", "fif", "fif", 'f', Blocks.iron_bars, 'i', "ingotIron"));
		}

		if (OpenBlocks.Blocks.sprinkler != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.sprinkler, "ifi", "iri", "ifi", 'i', "ingotIron", 'r', Blocks.redstone_torch, 'f', Blocks.iron_bars));
		}

		if (OpenBlocks.Blocks.cannon != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.cannon, " d ", " f ", "iri", 'd', Blocks.dispenser, 'f', Blocks.iron_bars, 'i', "ingotIron", 'r', "blockRedstone"));
		}

		if (OpenBlocks.Blocks.vacuumHopper != null) {
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Blocks.vacuumHopper, Blocks.hopper, Blocks.obsidian, Items.ender_eye));
		}

		if (OpenBlocks.Blocks.sponge != null) {
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Blocks.sponge, Blocks.wool, "slimeball"));
		}

		if (OpenBlocks.Blocks.bigButton != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.bigButton, "bb", "bb", 'b', Blocks.stone_button));
		}

		if (OpenBlocks.Blocks.imaginary != null) {
			{
				ItemStack pencil = ItemImaginary.setupValues(null, new ItemStack(OpenBlocks.Blocks.imaginary, 1, ItemImaginary.DAMAGE_PENCIL));
				recipeList.add(new ShapelessOreRecipe(pencil, Items.coal, "stickWood", Items.ender_eye, "slimeball"));
			}

			for (ColorMeta color : ColorUtils.getAllColors()) {
				ItemStack crayon = ItemImaginary.setupValues(color.rgb, new ItemStack(OpenBlocks.Blocks.imaginary, 1, ItemImaginary.DAMAGE_CRAYON));
				recipeList.add(new ShapelessOreRecipe(crayon, color.oreName, Items.paper, Items.ender_eye, "slimeball"));
			}

			recipeList.add(new CrayonMixingRecipe());
			RecipeSorter.register("openblocks:crayon_mix", CrayonMixingRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		}

		if (OpenBlocks.Blocks.fan != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.fan, "f", "i", "s", 'f', Blocks.iron_bars, 'i', "ingotIron", 's', Blocks.stone_slab));
		}

		if (OpenBlocks.Blocks.xpBottler != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.xpBottler, "iii", "ibi", "iii", 'i', "ingotIron", 'b', Items.glass_bottle));
		}

		if (OpenBlocks.Blocks.villageHighlighter != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.villageHighlighter, "www", "wew", "ccc", 'w', "plankWood", 'e', "gemEmerald", 'c', "cobblestone"));
		}

		if (OpenBlocks.Blocks.path != null) {
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.path, 2), "stone", "cobblestone"));
		}

		if (OpenBlocks.Blocks.autoAnvil != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.autoAnvil, "iii", "iai", "rrr", 'i', "ingotIron", 'a', Blocks.anvil, 'r', "dustRedstone"));
		}

		if (OpenBlocks.Blocks.autoEnchantmentTable != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.autoEnchantmentTable, "iii", "iei", "rrr", 'i', "ingotIron", 'e', Blocks.enchanting_table, 'r', "dustRedstone"));
		}

		if (OpenBlocks.Blocks.xpDrain != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.xpDrain, "iii", "iii", "iii", 'i', Blocks.iron_bars));
		}
		if (OpenBlocks.Blocks.blockBreaker != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.blockBreaker, "icc", "src", "icc", 'i', "ingotIron", 'c', "cobblestone", 'r', "dustRedstone", 's', Items.diamond_pickaxe));
		}

		if (OpenBlocks.Blocks.blockPlacer != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.blockPlacer, "icc", "src", "icc", 'i', "ingotIron", 'c', "cobblestone", 'r', "dustRedstone", 's', Blocks.piston));
		}

		if (OpenBlocks.Blocks.itemDropper != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.itemDropper, "icc", "src", "icc", 'i', "ingotIron", 'c', "cobblestone", 'r', "dustRedstone", 's', Blocks.hopper));
		}

		if (OpenBlocks.Blocks.ropeLadder != null) {
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.ropeLadder, 8), "sts", "sts", "sts", 't', "stickWood", 's', Items.string));
		}

		if (OpenBlocks.Blocks.donationStation != null) {
			WeightedRandomChestContent drop = new WeightedRandomChestContent(new ItemStack(OpenBlocks.Blocks.donationStation), 1, 1, 2);

			if (donationStationLoot) {
				ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
				ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			}
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.donationStation, "ppp", "pcp", "ppp", 'p', Items.porkchop, 'c', "chestWood"));
		}

		if (OpenBlocks.Blocks.paintMixer != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.paintMixer, "ooo", "i i", "iii", 'o', Blocks.obsidian, 'i', "ingotIron"));
		}

		if (OpenBlocks.Blocks.canvas != null) {
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.canvas, 9), "ppp", "pfp", "ppp", 'p', Items.paper, 'f', Blocks.fence));
		}

		if (OpenBlocks.Blocks.projector != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.projector, "grl", "iri", "srs", 's', Blocks.stone_slab, 'r', "dustRedstone", 'g', "dustGlowstone", 'i', "ingotIron", 'l', "gemLapis"));
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.projector, "lrg", "iri", "srs", 's', Blocks.stone_slab, 'r', "dustRedstone", 'g', "dustGlowstone", 'i', "ingotIron", 'l', "gemLapis"));
		}

		if (OpenBlocks.Blocks.goldenEgg != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.goldenEgg, "ggg", "geg", "ggg", 'g', "ingotGold", 'e', Items.egg));
		}

		if (OpenBlocks.Blocks.sky != null) {
			final ItemStack normal6 = new ItemStack(OpenBlocks.Blocks.sky, 6, 0);
			final ItemStack normal = new ItemStack(OpenBlocks.Blocks.sky, 1, 1);
			final ItemStack inverted = new ItemStack(OpenBlocks.Blocks.sky, 1, 0);
			recipeList.add(new ShapedOreRecipe(normal6, "geg", "gsg", "geg", 'g', "blockGlassColorless", 'e', Items.ender_eye, 's', Blocks.end_stone));
			recipeList.add(new ShapelessOreRecipe(inverted, normal, Blocks.redstone_torch));
			recipeList.add(new ShapelessOreRecipe(normal, inverted, Blocks.redstone_torch));
		}

		if (OpenBlocks.Blocks.drawingTable != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.drawingTable, "sks", "pcp", "ppp", 'p', "plankWood", 'c', "craftingTableWood", 's', MetasGeneric.unpreparedStencil.newItemStack(), 'k', MetasGeneric.sketchingPencil.newItemStack()));
		}

		if (OpenBlocks.Blocks.xpShower != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.xpShower, "iii", "  o", 'i', "ingotIron", 'o', Blocks.obsidian));
		}

		if (OpenBlocks.Blocks.scaffolding != null) {
			final ItemStack result = new ItemStack(OpenBlocks.Blocks.scaffolding, 8);
			recipeList.add(new ShapedOreRecipe(result, "sss", "s s", "sss", 's', "stickWood"));
			final RegistryDelegate<Item> itemDelegate = result.getItem().delegate;
			GameRegistry.registerFuelHandler(new IFuelHandler() {
				@Override
				public int getBurnTime(ItemStack fuel) {
					return fuel.getItem() == itemDelegate.get()? 100 : 0;
				}
			});
		}

		if (OpenBlocks.Items.hangGlider != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.hangGlider, "wsw", 'w', MetasGeneric.gliderWing.newItemStack(), 's', "stickWood"));
		}

		if (OpenBlocks.Items.luggage != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.luggage, "sds", "scs", "sss", 's', "stickWood", 'd', "gemDiamond", 'c', "chestWood"));
		}

		if (OpenBlocks.Items.sonicGlasses != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.sonicGlasses, "ihi", "oso", "   ", 's', "stickWood", 'h', Items.iron_helmet, 'o', Blocks.obsidian, 'i', "ingotIron"));
			ItemStack stack = new ItemStack(OpenBlocks.Items.sonicGlasses);

			if (sonicGlassesLoot) {
				WeightedRandomChestContent drop = new WeightedRandomChestContent(stack, 1, 1, 2);
				ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
			}
		}

		if (OpenBlocks.Blocks.imaginary != null) {
			if (OpenBlocks.Items.pencilGlasses != null) {
				ItemStack block = new ItemStack(OpenBlocks.Blocks.imaginary, 1, ItemImaginary.DAMAGE_PENCIL);
				ItemImaginary.setupValues(null, block);
				recipeList.add(new ShapelessOreRecipe(OpenBlocks.Items.pencilGlasses, block, Items.paper));
			}

			if (OpenBlocks.Items.crayonGlasses != null) {
				recipeList.add(new CrayonGlassesRecipe());
				// must be after pencil
				RecipeSorter.register("openblocks:crayon_glasses", CrayonGlassesRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
			}

			if (technicolorGlassesLoot && OpenBlocks.Items.technicolorGlasses != null) {
				WeightedRandomChestContent drop = new WeightedRandomChestContent(new ItemStack(OpenBlocks.Items.technicolorGlasses), 1, 1, 2);
				ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
				ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			}
		}

		if (OpenBlocks.Items.craneControl != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.craneControl, "ili", "grg", "iri", 'i', "ingotIron", 'g', "nuggetGold", 'l', "dustGlowstone", 'r', "dustRedstone"));
		}

		if (OpenBlocks.Items.craneBackpack != null) {
			ItemStack line = MetasGeneric.line.newItemStack();
			ItemStack beam = MetasGeneric.beam.newItemStack();
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Items.craneBackpack, MetasGeneric.craneEngine.newItemStack(), MetasGeneric.craneMagnet.newItemStack(), beam, beam, line, line, line, Items.leather));
		}

		if (OpenBlocks.Items.slimalyzer != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.slimalyzer, "igi", "isi", "iri", 'i', "ingotIron", 'g', "paneGlass", 's', "slimeball", 'r', "dustRedstone"));
		}

		if (OpenBlocks.Items.sleepingBag != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.sleepingBag, "cc ", "www", "ccw", 'c', Blocks.carpet, 'w', Blocks.wool));
		}

		if (OpenBlocks.Items.paintBrush != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.paintBrush, "w  ", " s ", "  s", 'w', Blocks.wool, 's', "stickWood"));

			final ItemStack template = new ItemStack(OpenBlocks.Items.paintBrush, 1, OreDictionary.WILDCARD_VALUE);
			for (ColorMeta color : ColorUtils.getAllColors()) {
				ItemStack brush = ItemPaintBrush.createStackWithColor(color.rgb);
				recipeList.add(new ShapelessOreRecipe(brush, template, color.oreName));
			}

			if (paintBrushLoot) {
				for (int color : new int[] { 0xFF0000, 0x00FF00, 0x0000FF }) {
					ItemStack stack = ItemPaintBrush.createStackWithColor(color);
					WeightedRandomChestContent drop = new WeightedRandomChestContent(stack, 1, 1, 2);
					ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
					ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(drop);
					ChestGenHooks.getInfo(ChestGenHooks.BONUS_CHEST).addItem(drop);
					ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
				}
			}
		}

		if (OpenBlocks.Items.stencil != null) {
			if (stencilLoot) {
				for (Stencil stencil : Stencil.values()) {
					WeightedRandomChestContent drop = new WeightedRandomChestContent(new ItemStack(OpenBlocks.Items.stencil, 1, stencil.ordinal()), 1, 1, 2);
					ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
					ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
				}
			}
		}

		if (OpenBlocks.Items.squeegee != null) {
			if (OpenBlocks.Blocks.sponge != null) {
				recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.squeegee, "sss", " w ", " w ", 's', OpenBlocks.Blocks.sponge, 'w', "stickWood"));
			} else {
				recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.squeegee, "sss", " w ", " w ", 's', "slimeball", 'w', "stickWood"));
			}
		}

		if (OpenBlocks.Items.emptyMap != null) {
			if (OpenBlocks.Items.heightMap != null) {
				recipeList.add(new MapCloneRecipe());
				RecipeSorter.register("openblocks:map_clone", MapCloneRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
			}

			recipeList.add(new MapResizeRecipe());
			RecipeSorter.register("openblocks:map_resize", MapResizeRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");

			ItemStack memory = MetasGeneric.mapMemory.newItemStack(2);
			ItemStack cpu = MetasGeneric.mapController.newItemStack(1);
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.emptyMap.createMap(0),
					" m ", "mcm", " m ", 'm', memory, 'c', cpu));
		}

		if (OpenBlocks.Items.cartographer != null) {
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Items.cartographer, MetasGeneric.assistantBase.newItemStack(), Items.ender_eye));
		}

		if (OpenBlocks.Items.goldenEye != null) {
			recipeList.add(new GoldenEyeRechargeRecipe());
			RecipeSorter.register("openblocks:golden_eye_recharge", GoldenEyeRechargeRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.goldenEye, 1, ItemGoldenEye.MAX_DAMAGE), "ggg", "geg", "ggg", 'g', "nuggetGold", 'e', Items.ender_eye));
		}

		if (OpenBlocks.Items.tastyClay != null) {
			final ItemStack cocoa = ColorMeta.BROWN.createStack(Items.dye, 1);
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Items.tastyClay, 2), Items.clay_ball, Items.milk_bucket, cocoa));
		}

		if (OpenBlocks.Items.cursor != null) {
			final ItemStack whiteWool = ColorMeta.WHITE.createStack(Blocks.wool, 1);
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.cursor, "w  ", "www", "www", 'w', whiteWool));
		}

		if (OpenBlocks.Items.infoBook != null) {
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Items.infoBook), Items.clay_ball, Items.book));
		}

		if (OpenBlocks.Items.devNull != null) {
			MinecraftForge.EVENT_BUS.register(OpenBlocks.Items.devNull);
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Items.devNull), "cobblestone", Items.apple));
		}

		if (OpenBlocks.Items.spongeonastick != null) {
			if (OpenBlocks.Blocks.sponge != null) {
				recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.spongeonastick, " s ", " w ", " w ", 's', OpenBlocks.Blocks.sponge, 'w', "stickWood"));
			}
		}

		if (OpenBlocks.Items.pedometer != null) {
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.pedometer, "www", "rcr", "www", 'w', "plankWood", 'r', "dustRedstone", 'c', Items.clock));
		}

		if (OpenBlocks.Items.filledBucket != null) {
			MetasBucket.registerItems();
			MetasBucket.xpbucket.registerAsBucketFor(OpenBlocks.Fluids.xpJuice);
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
				ItemStack result = Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(Enchantments.flimFlam, i + 1));
				Object recipe[] = new Object[emeraldCount + 1];
				recipe[0] = Items.book;
				Arrays.fill(recipe, 1, recipe.length, "gemEmerald");
				recipeList.add(new ShapelessOreRecipe(result, recipe));
			}

		}
	}
}
