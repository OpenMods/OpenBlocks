package openblocks;

import openmods.config.properties.ConfigProperty;
import openmods.config.properties.OnLineModifiable;

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
	@ConfigProperty(category = "dropblock", name = "overrides", comment = "Use to configure blocks as elevators. Examples: 'minecraft:wool' - configure any wool as white elevator, 'minecraft:wool#color=light_blue;yellow' - configure lightblue wool as yellow elevator")
	public static String[] elevatorOverrides = new String[0];

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
	@ConfigProperty(category = "tanks", name = "fluidDifferenceUpdateThreshold", comment = "Minimal difference in fluid level between neigbors required for tank update (can be used for performance finetuning")
	public static int tankFluidUpdateThreshold = 0;

	@ConfigProperty(category = "tanks", name = "allowBucketDrain", comment = "Can buckets be filled directly from tank? (works only for vanilla fluids, universal bucket and ones registered in 'bucketItems')")
	public static boolean allowBucketDrain = true;

	@ConfigProperty(category = "tanks", name = "bucketItems", comment = "List of additional custom buckets than can be filled directly from tanks")
	public static String[] bucketItems = new String[] { "openblocks:xp_bucket" };

	@OnLineModifiable
	@ConfigProperty(category = "trophy", name = "trophyDropChance", comment = "Legacy value. For actual configuration, see 'trophyDropChanceFormula'")
	public static double trophyDropChance = 0.001;

	@OnLineModifiable
	@ConfigProperty(category = "trophy", name = "trophyDropChanceFormula", comment = "Formula for calculating trophy drop chance. Trophy drops when result is positive.")
	public static String trophyDropChanceFormula = "let([bias=rand()/4, selection=rand()], (looting + bias) * chance - selection)";

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

	@ConfigProperty(category = "sprinkler", name = "internalTankCapacity", comment = "Capacity (in mB) of internal tank.")
	public static int sprinklerInternalTank = 50;

	@OnLineModifiable
	@ConfigProperty(category = "sprinkler", name = "bonemealConsumeRate", comment = "Consume rate of bonemeal (ticks/item).")
	public static int sprinklerBonemealConsumeRate = 600;

	@OnLineModifiable
	@ConfigProperty(category = "sprinkler", name = "waterConsumeRate", comment = "Consume rate of sprinkler (ticks/mB).")
	public static int sprinklerWaterConsumeRate = 20;

	@OnLineModifiable
	@ConfigProperty(category = "glasses", name = "opacity", comment = "0.0 - no visible change to world, 1.0 - world fully obscured")
	public static double sonicGlassesOpacity = 0.7;

	@OnLineModifiable
	@ConfigProperty(category = "glasses", name = "useTexture", comment = "Use texture for obscuring world")
	public static boolean sonicGlassesUseTexture = true;

	@OnLineModifiable
	@ConfigProperty(category = "imaginary", name = "fadingSpeed", comment = "Speed of imaginary blocks fading/appearing")
	public static float imaginaryFadingSpeed = 0.0075f;

	@OnLineModifiable
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

	@ConfigProperty(category = "crane", name = "magnetBlockWhitelist", comment = "List of resource location names of blocks that can be picked by magnet")
	public static String[] magnetBlockWhitelist = {};

	@ConfigProperty(category = "crane", name = "magnetEntityWhitelist", comment = "List of resource location names of entities that can be picked by magnet")
	public static String[] magnetEntityWhitelist = {};

	@ConfigProperty(category = "crane", name = "magnetTileEntityWhitelist", comment = "List of resource location names of tile entities that can be picked by magnet")
	public static String[] magnetTileEntityWhitelist = {};

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
	@ConfigProperty(category = "tomfoolery", name = "sillyLoreDisplay", comment = "0 - lore hidden, 1 - visible only with pressed ALT, 2 - always visible")
	public static int loreDisplay = 1;

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
	@ConfigProperty(category = "graves", name = "requiresGraveInInv", comment = "Require gravestone to be in a player's inventory (it is consumed)")
	public static boolean requiresGraveInInv = false;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "specialActionFrequency", comment = "Frequency of special action on grave digging, 0..1")
	public static double graveSpecialAction = 0.03;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "canSpawnBase", comment = "Can grave spawn single block of dirt when it has no block under?")
	public static boolean graveBase = true;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "minimalPosY", comment = "Minimal height where grave should be spawned (default value selected to prevent spawning in bedrock)")
	public static int minGraveY = 6;

	@OnLineModifiable
	@ConfigProperty(category = "graves", name = "maximalPosY", comment = "Maximal height where grave should be spawned (default value selected to prevent spawning in bedrock)")
	public static int maxGraveY = 255 - 6;

	@ConfigProperty(category = "features", name = "explosiveEnchantment", comment = "Is 'Explosive' enchantment enabled")
	public static boolean explosiveEnchantmentEnabled = true;

	@ConfigProperty(category = "features", name = "lastStandEnchantment", comment = "Is 'Last Stand' enchantment enabled")
	public static boolean lastStandEnchantmentEnabled = true;

	@ConfigProperty(category = "features", name = "flimFlamEnchantment", comment = "Is  'Flim-flam' enchantment enabled")
	public static boolean flimFlamEnchantmentEnabled = true;

	@ConfigProperty(category = "features", name = "explosiveEnchantGrief", comment = "Explosive enchant can break blocks at level 3")
	public static boolean explosiveEnchantGrief = true;

	@OnLineModifiable
	@ConfigProperty(category = "features", name = "lastStandFormula", comment = "Formula for XP cost (variables: hp,dmg,ench,xp). Note: calculation only triggers when hp - dmg < 1.")
	public static String lastStandEnchantmentFormula = "max(1, 50*(1-(hp-dmg))/ench)";

	// 64 blocks, since containers usually have 64 blocks usability range (IInventory.isUseableByPlayer)
	@ConfigProperty(category = "cursor", name = "cursorMaxDistance", comment = "Maximum distance cursor can reach (warning: increasing may cause crashes)")
	public static int cursorDistanceLimit = 64;

	@OnLineModifiable
	@ConfigProperty(category = "additional", name = "disableMobNames", comment = "List any mob names (like 'minecraft:bat') you want disabled on the server")
	public static String[] disableMobNames = new String[0];

	@OnLineModifiable
	@ConfigProperty(category = "additional", name = "dumpDeadPlayersInventories", comment = "Should player inventories be stored after death (can be later restored with ob_inventory command)")
	public static boolean dumpStiffsStuff = true;

	@OnLineModifiable
	@ConfigProperty(category = "cartographer", name = "blockBlacklist", comment = "List of blocks that should be invisible to cartographer. Example: id:3,  OpenBlocks:openblocks_radio (case sensitive)")
	public static String[] mapBlacklist = new String[] {};

	@OnLineModifiable
	@ConfigProperty(category = "cartographer", name = "reportInvalidRequest", comment = "Should invalid height map request be always reported")
	public static boolean alwaysReportInvalidMapRequests = false;

	@ConfigProperty(category = "radio", name = "radioVillagerEnabled", comment = "Should add radio villager profession")
	public static boolean radioVillagerEnabled = true;

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
	@ConfigProperty(category = "sponge", name = "blockUpdate", comment = "Should sponge block update neighbours after liquid removal?")
	public static boolean spongeBlockUpdate = false;

	@OnLineModifiable
	@ConfigProperty(category = "sponge", name = "stickBlockUpdate", comment = "Should sponge-on-a-stick update neighbours after liquid removal?")
	public static boolean spongeStickBlockUpdate = false;

	@ConfigProperty(category = "projector", name = "lightUpWhenWorking", comment = "Projector will light up whenever it is displaying a map")
	public static boolean litWhenDisplayingMap = true;

	@ConfigProperty(category = "projector", name = "renderHolographicCone", comment = "Projector will render a holographic cone whenever active")
	public static boolean renderHoloCone = true;

	@ConfigProperty(category = "projector", name = "brightness", comment = "The projector's cone will use the specified brightness value to render.\n"
			+ "Value must be between 0 and 255 inclusive. To use the default world brightness set -1 as the value.\n"
			+ "Keep in mind that default brightness means that the cone will render as light blue during the day and dark blue during the night.")
	public static int coneBrightness = -1;

	@ConfigProperty(category = "projector", name = "lightLevel", comment = "Level of light emitted by the active projector. Defaults to 10. Must be at maximum 15 and positive")
	public static int projectorLightLevelValue = 10;

	@OnLineModifiable
	@ConfigProperty(category = "loot", name = "technicolorGlasses")
	public static boolean technicolorGlassesLoot = true;

	@OnLineModifiable
	@ConfigProperty(category = "features", name = "infoBook", comment = "Should every player get info book on first login")
	public static boolean spamInfoBook = true;

	@ConfigProperty(category = "features", name = "xpToLiquidRatio", comment = "Storage in mB needed to store single XP point")
	public static int xpToLiquidRatio = 20;

	@ConfigProperty(category = "features", name = "additionalXpFluids", comment = "Other fluids accepted instead liquid XP")
	public static String[] additionalXpFluids = { "experience:20" };

	@OnLineModifiable
	@ConfigProperty(category = "guide", name = "redstoneSensitivity", comment = "How builder guide should react to redstone. 0 - not sensitive, 1 - powered == on, -1 - inverted")
	public static int guideRedstone = 1;

	@OnLineModifiable
	@ConfigProperty(category = "guide", name = "renderDistanceSq", comment = "Square of guide maximum render distance")
	public static double guideRenderRangeSq = 256 * 256;

	@ConfigProperty(category = "guide", name = "useAdvancedRenderer", comment = "Try to use advanced OpenGL for performance improvement")
	public static boolean useAdvancedRenderer = true;

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

	@OnLineModifiable
	@ConfigProperty(category = "devnull", name = "sneakClickToGui", comment = "If true, /dev/null will require sneaking in addition to clicking air to open gui")
	public static boolean devNullSneakGui = true;

	@OnLineModifiable
	@ConfigProperty(category = "devnull", name = "countColor", comment = "Color of contained stack size in GUI")
	public static int devNullCountColor = 0x00FFFF00;

	@OnLineModifiable
	@ConfigProperty(category = "hangglider", name = "enableThermal", comment = "Enable a whole new level of hanggliding experience through thermal lift. See keybindings for acoustic vario controls")
	public static boolean hanggliderEnableThermal = true;

	@OnLineModifiable
	@ConfigProperty(category = "itemdropper", name = "maxItemDropSpeed", comment = "Maximum speed that can be set in item dropper GUI")
	public static double maxItemDropSpeed = 4;

	@OnLineModifiable
	@ConfigProperty(category = "canvas", name = "canvasTexturePoolSize", comment = "Controls number of textures allocated for canvas. Increase if canvas blocks stop rendering properly (get empty sides). To re-apply, reload textures")
	public static int canvasPoolSize = 256;

	@OnLineModifiable
	@ConfigProperty(category = "canvas", name = "paintbrushReplacesBlocks", comment = "If true, paintbrush will replace suitable blocks with canvas. Otherwise, it will only try to paint blocks")
	public static boolean paintbrushReplacesBlocks = true;

	@OnLineModifiable
	@ConfigProperty(category = "canvas", name = "replaceBlacklist", comment = "List of block ids that should not be replaceable by canvas (by using brush, stencil, etc)")
	public static String[] canvasBlacklist = new String[0];

	@OnLineModifiable
	@ConfigProperty(category = "skyblock", name = "renderingEnabled", comment = "Enables skyblock rendering. Disable when there are graphic glitches or performance problems. Requires resource reload after change.")
	public static boolean renderSkyBlocks = true;

	@ConfigProperty(category = "skyblock", name = "optifineOverride", comment = "Forces skyblock rendering even when Optifine is enabled (warning: skyblocks may be incompatible with shaders!)")
	public static boolean skyBlocksOptifineOverride = false;

	@OnLineModifiable
	@ConfigProperty(category = "breaker", name = "actionLimit", comment = "Maximum number of actions that can be performed by block breaker in single tick")
	public static int blockBreakerActionLimit = 16;

	@OnLineModifiable
	@ConfigProperty(category = "placer", name = "actionLimit", comment = "Maximum number of actions that can be performed by block placer in single tick")
	public static int blockPlacerActionLimit = 16;

	@ConfigProperty(category = "xpBucket", name = "directFill", comment = "Can bucket be filled with liquid XP directly from any source?")
	public static boolean xpBucketDirectFill = true;

	@ConfigProperty(category = "xpBucket", name = "universalBucketSupport", comment = "Should liquid XP be registered for universal bucket support (does not enable universal bucket)")
	public static boolean registerUniversalXpBucket = true;

	@OnLineModifiable
	@ConfigProperty(category = "xpBucket", name = "showInCreativeGui", comment = "Should XP bucket be shown in inventory (if this is set, while universal bucket is enabled and registerUniversalXpBucket is set, creative menu will contain two buckets containing liquid XP)")
	public static boolean showXpBucketInCreative = true;

	@OnLineModifiable
	@ConfigProperty(category = "glyphs", name = "showInCreativeSearch", comment = "Should glyphs be added to creative search GUI")
	public static boolean showGlypsInSearch = false;
}
