package openblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openblocks.asm.EntityPlayerVisitor;
import openblocks.common.*;
import openblocks.common.block.*;
import openblocks.common.item.*;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.common.recipe.*;
import openmods.config.BlockId;
import openmods.config.ConfigProcessing;
import openmods.config.ConfigProperty;
import openmods.config.ItemId;
import openmods.utils.ColorUtils;
import openmods.utils.ColorUtils.ColorMeta;

import com.google.common.collect.Lists;

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

	@BlockId(description = "The id of the fan block")
	public static int blockFanId = 2556;

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

	@BlockId(description = "The id of the Ore Crusher")
	public static int blockMachineOreCrusherId = 2570;

	@BlockId(description = "The id of the paint tin")
	public static int blockPaintCanId = 2571;

	@BlockId(description = "The id of the glass canvas block")
	public static int blockCanvasGlassId = 2572;

	@BlockId(description = "The id of the map projector block")
	public static int blockProjectorId = 2573;

	@BlockId(description = "The id of the drawing table")
	public static int blockDrawingTable = 2574;

	//@BlockId(description = "The id of the golden egg block")
	public static int blockGoldenEggId = 2575;

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

	@ItemId(description = "The id of the cursor item")
	public static int itemCursorId = 14997;

	@ItemId(description = "The id of the unstackable generic item")
	public static int itemGenericUnstackableId = 14996;
	
	@ConfigProperty(category = "dropblock", name = "searchDistance", comment = "The range of the drop block")
	public static int elevatorTravelDistance = 20;

	@ConfigProperty(category = "dropblock", name = "mustFaceDirection", comment = "Must the user face the direction they want to travel?")
	public static boolean elevatorBlockMustFaceDirection = false;

	@ConfigProperty(category = "dropblock", name = "ignoreHalfBlocks", comment = "The elevator will ignore half blocks when counting the blocks it can pass through")
	public static boolean elevatorIgnoreHalfBlocks = false;

	@ConfigProperty(category = "dropblock", name = "maxPassThrough", comment = "The maximum amount of blocks the elevator can pass through before the teleport fails. -1 disables this")
	public static int elevatorMaxBlockPassCount = 4;

	@ConfigProperty(category = "tanks", name = "bucketsPerTank", comment = "The amount of buckets each tank can hold")
	public static int bucketsPerTank = 16;

	@ConfigProperty(category = "grave", name = "enableGraves", comment = "Enable graves on player death")
	public static boolean enableGraves = false;

	@ConfigProperty(category = "hacks", name = "tryHookPlayerRenderer", comment = "Allow OpenBlocks to hook the player renderer to apply special effects")
	public static boolean tryHookPlayerRenderer = true;

	@ConfigProperty(category = "trophy", name = "trophyDropChance", comment = "The chance (from 0 to 1) of a trophy drop. for example, 0.001 for 1/1000")
	public static double trophyDropChance = 0.001;

	@ConfigProperty(category = "dropblock", name = "irregularBlocksArePassable", comment = "The elevator will try to pass through blocks that have custom collision boxes")
	public static boolean irregularBlocksArePassable = true;

	@ConfigProperty(category = "tanks", name = "emitLight", comment = "Tanks will emit light when they contain a liquid that glows (eg. lava)")
	public static boolean tanksEmitLight = true;

	@ConfigProperty(category = "sprinkler", name = "fertilizeChance", comment = "1/chance that crops will be fertilized without bonemeal")
	public static int sprinklerFertilizeChance = 500;

	@ConfigProperty(category = "sprinkler", name = "bonemealFertilizeChance", comment = "1/chance that crops will be fertilized with bonemeal")
	public static int sprinklerBonemealFertizizeChance = 200;

	@ConfigProperty(category = "sprinkler", name = "effectiveRange", comment = "The range in each cardinal direction that crops will be affected.")
	public static int sprinklerEffectiveRange = 4;

	@ConfigProperty(category = "glasses", name = "opacity", comment = "0.0 - no visible change to world, 1.0 - world fully obscured")
	public static double sonicGlassesOpacity = 0.95;

	@ConfigProperty(category = "glasses", name = "useTexture", comment = "Use texture for obscuring world")
	public static boolean sonicGlassesUseTexture = true;

	@ConfigProperty(category = "imaginary", name = "fadingSpeed", comment = "Speed of imaginary blocks fading/appearing")
	public static float imaginaryFadingSpeed = 0.0075f;

	@ConfigProperty(category = "imaginary", name = "numberOfUses", comment = "Number of newly created crayon/pencil uses")
	public static float imaginaryItemUseCount = 10;

	@ConfigProperty(category = "crane", name = "doCraneCollisionCheck", comment = "Enable collision checking of crane arm")
	public static boolean doCraneCollisionCheck = false;

	@ConfigProperty(category = "crane", name = "boringMode", comment = "Use shift to control crane direction (otherwise, toggle every time)")
	public static boolean craneShiftControl = true;

	@ConfigProperty(category = "crane", name = "turtleMagnetRange", comment = "Range of magnet CC peripheral")
	public static double turtleMagnetRange = 4;

	@ConfigProperty(category = "crane", name = "addTurtles", comment = "Enable magnet turtles in creative list")
	public static boolean addCraneTurtles = true;

	@ConfigProperty(category = "hacks", name = "enableExperimentalFeatures", comment = "Enable experimental features that may be buggy or broken entirely")
	public static boolean experimentalFeatures = false;

	@ConfigProperty(category = "tomfoolery", name = "weAreSeriousPeople", comment = "Are you serious too?")
	public static boolean soSerious = true;

	@ConfigProperty(category = "tomfoolery", name = "doItWhileTyping", comment = "You know, THAT thing! That you shouldn't do in public!")
	public static boolean fartTypying = false;

	@ConfigProperty(category = "debug", name = "goldenEyeDebug", comment = "Show structures found by golden eye")
	public static boolean eyeDebug = false;

	@ConfigProperty(category = "features", name = "explosiveEnchantmentId", comment = "Id of explosive enchantment")
	public static int explosiveEnchantmentId = 211;
	
	@ConfigProperty(category = "cursor", name = "cursorMaxDamage", comment = "Amount of damage a cursor can take")
	public static int cursorMaxDamage = 10240;
	
	public static List<String> disableMobNames = Lists.newArrayList();

	static void readConfig(Configuration configFile) {
		ConfigProcessing.processAnnotations(configFile, Config.class);
	}

	public static void register() {

		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		// There is no fail checking here because if the Generic item fails,
		// then I doubt anyone wants this to be silent.
		// Too many items would suffer from this. - NC
		OpenBlocks.Items.generic = new ItemOBGeneric();
		MetasGeneric.registerItems();
		if (itemFilledBucketId > 0) {
			OpenBlocks.Items.filledBucket = new ItemFilledBucket();
			MetasBucket.registerItems();
		}
		
		OpenBlocks.Items.genericUnstackable = new ItemOBGenericUnstackable();
		MetasGenericUnstackable.registerItems();
		if (itemFilledBucketId > 0) {
			OpenBlocks.Items.filledBucket = new ItemFilledBucket();
			MetasBucket.registerItems();
		}

		if (ConfigProcessing.canRegisterBlock(blockLadderId)) {
			OpenBlocks.Blocks.ladder = new BlockLadder();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.ladder), new ItemStack(Block.ladder), new ItemStack(Block.trapdoor)));
		}
		if (ConfigProcessing.canRegisterBlock(blockGuideId)) {
			OpenBlocks.Blocks.guide = new BlockGuide();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.guide), new Object[] { "ggg", "gtg", "ggg", 'g', new ItemStack(Block.glass), 't', new ItemStack(Block.torchWood) }));
		}
		if (ConfigProcessing.canRegisterBlock(blockElevatorId)) {
			OpenBlocks.Blocks.elevator = new BlockElevator();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.elevator), new Object[] { "www", "wew", "www", 'w', new ItemStack(Block.cloth, 1, Short.MAX_VALUE), 'e', new ItemStack(Item.enderPearl) }));
		}
		if (ConfigProcessing.canRegisterBlock(blockHealId)) {
			OpenBlocks.Blocks.heal = new BlockHeal();
		}
		if (ConfigProcessing.canRegisterBlock(blockTargetId)) {
			OpenBlocks.Blocks.target = new BlockTarget();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.target), new Object[] { "www", "www", "s s", 'w', new ItemStack(Block.cloth, 1, Short.MAX_VALUE), 's', "stickWood" }));
		}
		if (ConfigProcessing.canRegisterBlock(blockGraveId)) {
			OpenBlocks.Blocks.grave = new BlockGrave();
		}
		if (ConfigProcessing.canRegisterBlock(blockFlagId)) {
			OpenBlocks.Blocks.flag = new BlockFlag();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.flag, 3), new Object[] { "scc", "sc ", "s  ", 'c', new ItemStack(Block.carpet, 1, Short.MAX_VALUE), 's', "stickWood" }));
		}
		if (ConfigProcessing.canRegisterBlock(blockTankId)) {
			OpenBlocks.Blocks.tank = new BlockTank();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.tank, 2), new Object[] { "ogo", "ggg", "ogo", 'g', new ItemStack(Block.thinGlass), 'o', new ItemStack(Block.obsidian) }));
		}
		if (ConfigProcessing.canRegisterBlock(blockTrophyId)) {
			OpenBlocks.Blocks.trophy = new BlockTrophy();
			MinecraftForge.EVENT_BUS.register(new TrophyHandler());
		}
		if (ConfigProcessing.canRegisterBlock(blockBearTrapId)) {
			OpenBlocks.Blocks.bearTrap = new BlockBearTrap();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.bearTrap), new Object[] { "fif", "fif", "fif", 'f', new ItemStack(Block.fenceIron), 'i', new ItemStack(Item.ingotIron) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockSprinklerId)) {
			OpenBlocks.Blocks.sprinkler = new BlockSprinkler();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.sprinkler, 1), new Object[] { "ifi", "iri", "ifi", 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Block.torchRedstoneActive), 'f', new ItemStack(Block.fenceIron) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockCannonId)) {
			OpenBlocks.Blocks.cannon = new BlockCannon();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.cannon), new Object[] { " d ", " f ", "iri", 'd', new ItemStack(Block.dispenser), 'f', new ItemStack(Block.fenceIron), 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Block.blockRedstone) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockVacuumHopperId)) {
			OpenBlocks.Blocks.vacuumHopper = new BlockVacuumHopper();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.vacuumHopper), new ItemStack(Block.hopperBlock), new ItemStack(Block.obsidian), new ItemStack(Item.enderPearl)));
		}

		if (ConfigProcessing.canRegisterBlock(blockSpongeId)) {
			OpenBlocks.Blocks.sponge = new BlockSponge();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.sponge), new ItemStack(Block.cloth, 1, Short.MAX_VALUE), new ItemStack(Item.slimeBall)));
		}

		if (ConfigProcessing.canRegisterBlock(blockBigButton)) {
			OpenBlocks.Blocks.bigButton = new BlockBigButton();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.bigButton), new Object[] { "bb", "bb", 'b', new ItemStack(Block.stoneButton) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockImaginaryId)) {
			OpenBlocks.Blocks.imaginary = new BlockImaginary();
			{
				ItemStack pencil = ItemImaginary.setupValues(null, new ItemStack(OpenBlocks.Blocks.imaginary, 1, 0));
				recipeList.add(new ShapelessOreRecipe(pencil, Item.coal, "stickWood", Item.enderPearl, Item.slimeBall));
			}

			for (ColorMeta color : ColorUtils.getAllColors()) {
				ItemStack crayon = ItemImaginary.setupValues(color.rgb, new ItemStack(OpenBlocks.Blocks.imaginary, 1, 0));
				recipeList.add(new ShapelessOreRecipe(crayon, color.oreName, Item.paper, Item.enderPearl, Item.slimeBall));
			}

			recipeList.add(new CrayonMixingRecipe());
		}

		if (ConfigProcessing.canRegisterBlock(blockFanId)) {
			OpenBlocks.Blocks.fan = new BlockFan();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.fan), new Object[] { "f", "i", "s", 'f', new ItemStack(Block.fenceIron), 'i', new ItemStack(Item.ingotIron), 's', new ItemStack(Block.stoneSingleSlab) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockXPBottlerId)) {
			OpenBlocks.Blocks.xpBottler = new BlockXPBottler();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.xpBottler), new Object[] { "iii", "ibi", "iii", 'i', new ItemStack(Item.ingotIron), 'b', new ItemStack(Item.glassBottle) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockVillageHighlighterId)) {
			OpenBlocks.Blocks.villageHighlighter = new BlockVillageHighlighter();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.villageHighlighter), new Object[] { "www", "wew", "ccc", 'w', "plankWood", 'e', new ItemStack(Item.emerald), 'c', new ItemStack(Block.cobblestone) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockPathId)) {
			OpenBlocks.Blocks.path = new BlockPath();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.path, 2), "stone", "cobblestone"));
		}

		if (ConfigProcessing.canRegisterBlock(blockAutoAnvilId)) {
			OpenBlocks.Blocks.autoAnvil = new BlockAutoAnvil();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.autoAnvil), new Object[] { "iii", "iai", "rrr", 'i', new ItemStack(Item.ingotIron), 'a', new ItemStack(Block.anvil, 1, Short.MAX_VALUE), 'r', new ItemStack(Item.redstone) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockAutoEnchantmentTableId)) {
			OpenBlocks.Blocks.autoEnchantmentTable = new BlockAutoEnchantmentTable();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.autoEnchantmentTable), new Object[] { "iii", "iei", "rrr", 'i', new ItemStack(Item.ingotIron), 'e', new ItemStack(Block.enchantmentTable, 1, Short.MAX_VALUE), 'r', new ItemStack(Item.redstone) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockXPDrainId)) {
			OpenBlocks.Blocks.xpDrain = new BlockXPDrain();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.xpDrain), new Object[] { "iii", "iii", "iii", 'i', new ItemStack(Block.fenceIron) }));
		}
		if (ConfigProcessing.canRegisterBlock(blockBlockBreakerId)) {
			OpenBlocks.Blocks.blockBreaker = new BlockBlockBreaker();
			ItemStack specialItem = new ItemStack(Item.pickaxeDiamond);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.blockBreaker), new Object[] { "icc", "src", "icc", 'i', new ItemStack(Item.ingotIron), 'c', new ItemStack(Block.cobblestone), 'r', new ItemStack(Item.redstone), 's', specialItem }));
		}

		if (ConfigProcessing.canRegisterBlock(blockBlockPlacerId)) {
			OpenBlocks.Blocks.blockPlacer = new BlockBlockPlacer();
			ItemStack specialItem = new ItemStack(Block.pistonBase);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.blockPlacer), new Object[] { "icc", "src", "icc", 'i', new ItemStack(Item.ingotIron), 'c', new ItemStack(Block.cobblestone), 'r', new ItemStack(Item.redstone), 's', specialItem }));
		}

		if (ConfigProcessing.canRegisterBlock(blockItemDropperId)) {
			OpenBlocks.Blocks.itemDropper = new BlockItemDropper();
			ItemStack specialItem = new ItemStack(Block.hopperBlock);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.itemDropper), new Object[] { "icc", "src", "icc", 'i', new ItemStack(Item.ingotIron), 'c', new ItemStack(Block.cobblestone), 'r', new ItemStack(Item.redstone), 's', specialItem }));
		}

		if (ConfigProcessing.canRegisterBlock(blockRopeLadderId)) {
			OpenBlocks.Blocks.ropeLadder = new BlockRopeLadder();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.ropeLadder), new Object[] { "sts", "sts", "sts", 't', "stickWood", 's', new ItemStack(Item.silk) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockDonationStationId)) {
			OpenBlocks.Blocks.donationStation = new BlockDonationStation();
			WeightedRandomChestContent drop = new WeightedRandomChestContent(new ItemStack(OpenBlocks.Blocks.donationStation), 1, 1, 2);
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
			ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.donationStation), new Object[] { "ppp", "pcp", "ppp", 'p', new ItemStack(Item.porkRaw), 'c', new ItemStack(Block.chest) }));
		}

		if (ConfigProcessing.canRegisterBlock(blockPaintMixer)) {
			OpenBlocks.Blocks.paintMixer = new BlockPaintMixer();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.paintMixer), "ooo", "i i", "iii", 'o', Block.obsidian, 'i', Item.ingotIron));
		}

		if (ConfigProcessing.canRegisterBlock(blockCanvasId)) {
			OpenBlocks.Blocks.canvas = new BlockCanvas();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.canvas, 9), "ppp", "pfp", "ppp", 'p', Item.paper, 'f', Block.fence));
		}

		if (experimentalFeatures && ConfigProcessing.canRegisterBlock(blockMachineOreCrusherId)) {
			OpenBlocks.Blocks.machineOreCrusher = new BlockMachineOreCrusher();
		}

		if (ConfigProcessing.canRegisterBlock(blockPaintCanId)) {
			OpenBlocks.Blocks.paintCan = new BlockPaintCan();
		}

		if (ConfigProcessing.canRegisterBlock(blockCanvasGlassId)) {
			OpenBlocks.Blocks.canvasGlass = new BlockCanvasGlass();
		}

		if (ConfigProcessing.canRegisterBlock(blockProjectorId)) {
			OpenBlocks.Blocks.projector = new BlockProjector();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.projector, "grl", "iri", "srs", 's', Block.stoneSingleSlab, 'r', Item.redstone, 'g', Item.glowstone, 'i', Item.ingotIron, 'l', new ItemStack(Item.dyePowder, 1, 4)));
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.projector, "lrg", "iri", "srs", 's', Block.stoneSingleSlab, 'r', Item.redstone, 'g', Item.glowstone, 'i', Item.ingotIron, 'l', new ItemStack(Item.dyePowder, 1, 4)));
		}

		/*
		if (ConfigProcessing.canRegisterBlock(blockGoldenEggId)) {
			OpenBlocks.Blocks.goldenEgg = new BlockGoldenEgg();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.goldenEgg, "ggg", "geg", "ggg", 'g', Item.ingotGold, 'e', Item.egg));
		}
		*/

		if (ConfigProcessing.canRegisterBlock(blockDrawingTable)) {
			OpenBlocks.Blocks.drawingTable = new BlockDrawingTable();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Blocks.drawingTable, "sks", "pcp", "ppp", 'p', Block.planks, 'c', Block.workbench, 's', MetasGeneric.unpreparedStencil.newItemStack(), 'k', MetasGeneric.sketchingPencil.newItemStack()));
		}

		if (itemHangGliderId > 0) {
			OpenBlocks.Items.hangGlider = new ItemHangGlider();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.hangGlider), new Object[] { "wsw", 'w', MetasGeneric.gliderWing.newItemStack(), 's', "stickWood" }));
		}

		if (itemLuggageId > 0) {
			OpenBlocks.Items.luggage = new ItemLuggage();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.luggage), new Object[] { "sds", "scs", "sss", 's', "stickWood", 'd', new ItemStack(Item.diamond), 'c', new ItemStack(Block.chest) }));
		}

		if (itemSonicGlassesId > 0) {
			OpenBlocks.Items.sonicGlasses = new ItemSonicGlasses();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.sonicGlasses), new Object[] { "ihi", "oso", "   ", 's', "stickWood", 'h', new ItemStack(Item.helmetIron), 'o', new ItemStack(Block.obsidian), 'i', new ItemStack(Item.ingotIron) }));
		}

		if (OpenBlocks.Blocks.imaginary != null) {
			if (itemGlassesPencil > 0) {
				OpenBlocks.Items.pencilGlasses = new ItemImaginationGlasses(itemGlassesPencil, ItemImaginationGlasses.Type.PENCIL);
				ItemStack block = new ItemStack(OpenBlocks.Blocks.imaginary, 1, 0);
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
			recipeList.add(new ShapedOreRecipe(ItemPaintBrush.createStackWithColor(0xFFFFFF), "w  ", " s ", "  s", 'w', Block.cloth, 's', "stickWood"));
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
					" m ", "mcm", " m ",
					'm', memory,
					'c', cpu
					));
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
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Items.tastyClay, 2), Item.clay, Item.bucketMilk, new ItemStack(Item.dyePowder, 1, 3)));
		}
		
		if (itemCursorId > 0) {
			OpenBlocks.Items.cursor = new ItemCursor();
			//TODO: add recipe
		}

		if (explosiveEnchantmentId > 0) {
			MinecraftForge.EVENT_BUS.register(new ExplosiveEnchantmentsHandler());
			OpenBlocks.explosiveEnch = new EnchantmentExplosive(explosiveEnchantmentId);
		}

		final String modId = "openblocks";
		ConfigProcessing.registerItems(OpenBlocks.Items.class, modId);
		ConfigProcessing.registerBlocks(OpenBlocks.Blocks.class, modId);
	}
}
