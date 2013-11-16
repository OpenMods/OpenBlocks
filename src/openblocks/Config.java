package openblocks;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openblocks.OpenBlocks.Blocks;
import openblocks.asm.ClassTransformerEntityPlayer;
import openblocks.common.EntityEventHandler;
import openblocks.common.Stencil;
import openblocks.common.TrophyHandler;
import openblocks.common.block.*;
import openblocks.common.entity.EntityMount;
import openblocks.common.item.*;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openblocks.common.recipe.CrayonGlassesRecipe;
import openblocks.common.recipe.CrayonMixingRecipe;
import openblocks.common.recipe.MapCloneRecipe;
import openblocks.common.recipe.MapResizeRecipe;
import openblocks.utils.ColorUtils;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class Config {
	public static boolean failIdsQuietly = true;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface BlockId {
		String description();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface ItemId {
		String description();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface RegisterItem {
		public String name();
	}

	@BlockId(description = "The id of the ladder")
	public static int blockLadderId = 2540;

	@BlockId(description = "The id of the guide")
	public static int blockGuideId = 2541;

	@BlockId(description = "The id of the elevator block")
	public static int blockElevatorId = 2542;

	@BlockId(description = "The id of the heal block")
	public static int blockHealId = 2543;

	@BlockId(description = "The id of the lightbox block")
	public static int blockLightboxId = 2544;

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

	public static int elevatorTravelDistance = 20;
	public static boolean elevatorBlockMustFaceDirection = false;
	public static boolean elevatorIgnoreHalfBlocks = false;
	public static int elevatorMaxBlockPassCount = 4;
	public static int bucketsPerTank = 16;
	public static boolean enableGraves = false;
	public static boolean tryHookPlayerRenderer = true;
	public static double trophyDropChance = 0.001;
	public static boolean irregularBlocksArePassable = true;
	public static boolean tanksEmitLight = true;
	public static int sprinklerFertilizeChance = 500;
	public static int sprinklerBonemealFertizizeChance = 200;
	public static int sprinklerEffectiveRange = 4;
	public static double sonicGlassesOpacity = 0.95;
	public static boolean sonicGlassesUseTexture = true;
	public static float imaginaryFadingSpeed = 0.0075f;
	public static float imaginaryItemUseCount = 10;
	public static List<String> disableMobNames = Lists.newArrayList();
	public static boolean doCraneCollisionCheck = false;
	public static boolean craneShiftControl = true;
	public static double turtleMagnetRange = 4;
	public static boolean addCraneTurtles = true;
	public static boolean experimentalFeatures = false;

	private static void getBlock(Configuration configFile, Field field, String description) {
		try {
			int defaultValue = field.getInt(null);
			Property prop = configFile.getBlock("block", field.getName(), defaultValue, description);
			field.set(null, prop.getInt());
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	private static void getItem(Configuration configFile, Field field, String description) {
		try {
			int defaultValue = field.getInt(null);
			Property prop = configFile.getItem("item", field.getName(), defaultValue, description);
			field.set(null, prop.getInt());
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

	private static void processAnnotations(Configuration configFile) {
		for (Field f : Config.class.getFields()) {
			{
				ItemId a = f.getAnnotation(ItemId.class);
				if (a != null) {
					getItem(configFile, f, a.description());
					continue;
				}
			}

			{
				BlockId a = f.getAnnotation(BlockId.class);
				if (a != null) {
					getBlock(configFile, f, a.description());
				}
			}
		}
	}

	static void readConfig(Configuration configFile) {
		Property prop = configFile.get("openblocks", "failIdsQuietly", failIdsQuietly, "If true, OpenBlocks will not throw an error when a block cannot be loaded due to ID conflict.");
		failIdsQuietly = prop.getBoolean(failIdsQuietly);

		processAnnotations(configFile);

		prop = configFile.get("dropblock", "searchDistance", elevatorTravelDistance, "The range of the drop block");
		elevatorTravelDistance = prop.getInt();

		prop = configFile.get("dropblock", "mustFaceDirection", elevatorBlockMustFaceDirection, "Must the user face the direction they want to travel?");
		elevatorBlockMustFaceDirection = prop.getBoolean(elevatorBlockMustFaceDirection);

		prop = configFile.get("dropblock", "maxPassThrough", elevatorMaxBlockPassCount, "The maximum amount of blocks the elevator can pass through before the teleport fails. -1 disables this");
		elevatorMaxBlockPassCount = prop.getInt();

		if (elevatorMaxBlockPassCount < -1) {
			elevatorMaxBlockPassCount = -1;
		}
		prop.set(elevatorMaxBlockPassCount);

		prop = configFile.get("dropblock", "ignoreHalfBlocks", elevatorIgnoreHalfBlocks, "The elevator will ignore half blocks when counting the blocks it can pass through");
		elevatorIgnoreHalfBlocks = prop.getBoolean(elevatorIgnoreHalfBlocks);

		prop = configFile.get("dropblock", "irregularBlocksArePassable", irregularBlocksArePassable, "The elevator will try to pass through blocks that have custom collision boxes");
		irregularBlocksArePassable = prop.getBoolean(irregularBlocksArePassable);

		prop = configFile.get("grave", "enableGraves", enableGraves, "Enable graves on player death");
		enableGraves = prop.getBoolean(enableGraves);

		prop = configFile.get("tanks", "bucketsPerTank", bucketsPerTank, "The amount of buckets each tank can hold");
		bucketsPerTank = prop.getInt(bucketsPerTank);

		prop = configFile.get("tanks", "emitLight", tanksEmitLight, "Tanks will emit light when they contain a liquid that glows (eg. lava)");
		tanksEmitLight = prop.getBoolean(tanksEmitLight);

		prop = configFile.get("trophy", "trophyDropChance", trophyDropChance, "The chance (from 0 to 1) of a trophy drop. for example, 0.001 for 1/1000");
		trophyDropChance = prop.getDouble(trophyDropChance);

		prop = configFile.get("sprinkler", "fertilizeChance", sprinklerFertilizeChance, "1/chance that crops will be fertilized without bonemeal");
		sprinklerFertilizeChance = prop.getInt(sprinklerFertilizeChance);

		prop = configFile.get("sprinkler", "bonemealFertilizeChance", sprinklerBonemealFertizizeChance, "1/chance that crops will be fertilized with bonemeal");
		sprinklerBonemealFertizizeChance = prop.getInt(sprinklerBonemealFertizizeChance);

		prop = configFile.get("sprinkler", "effectiveRange", sprinklerEffectiveRange, "The range in each cardinal direction that crops will be affected.");
		sprinklerEffectiveRange = prop.getInt(sprinklerEffectiveRange);

		prop = configFile.get("hacks", "tryHookPlayerRenderer", tryHookPlayerRenderer, "Allow OpenBlocks to hook the player renderer to apply special effects");
		tryHookPlayerRenderer = prop.getBoolean(tryHookPlayerRenderer);

		prop = configFile.get("glasses", "opacity", sonicGlassesOpacity, "0.0 - no visible change to world, 1.0 - world fully obscured");
		sonicGlassesOpacity = prop.getDouble(sonicGlassesOpacity);

		prop = configFile.get("glasses", "useTexture", sonicGlassesUseTexture, "Use texture for obscuring world");
		sonicGlassesUseTexture = prop.getBoolean(sonicGlassesUseTexture);

		prop = configFile.get("imaginary", "fadingSpeed", imaginaryFadingSpeed, "Speed of imaginary blocks fading/appearing");
		imaginaryFadingSpeed = (float)prop.getDouble(imaginaryFadingSpeed);

		prop = configFile.get("imaginary", "numberOfUses", imaginaryItemUseCount, "Number of newly created crayon/pencil uses");
		imaginaryItemUseCount = (float)prop.getDouble(imaginaryItemUseCount);

		prop = configFile.get("crane", "doCraneCollisionCheck", doCraneCollisionCheck, "Enable collision checking of crane arm");
		doCraneCollisionCheck = prop.getBoolean(doCraneCollisionCheck);

		prop = configFile.get("crane", "boringMode", craneShiftControl, "Use shift to control crane direction (otherwise, toggle every time)");
		craneShiftControl = prop.getBoolean(craneShiftControl);

		prop = configFile.get("crane", "turtleMagnetRange", turtleMagnetRange, "Range of magnet CC peripheral");
		turtleMagnetRange = prop.getDouble(turtleMagnetRange);

		prop = configFile.get("crane", "addTurtles", addCraneTurtles, "Enable magnet turtles in creative list");
		addCraneTurtles = prop.getBoolean(addCraneTurtles);

		prop = configFile.get("hacks", "enableExperimentalFeatures", experimentalFeatures, "Enable experimental features that may be buggy or broken entirely");
		experimentalFeatures = prop.getBoolean(experimentalFeatures);

	}

	public static void register() {

		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

		@SuppressWarnings("unchecked")
		final List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();

		if (Config.canRegisterBlock(blockLadderId)) {
			OpenBlocks.Blocks.ladder = new BlockLadder();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.ladder), new ItemStack(Block.ladder), new ItemStack(Block.trapdoor)));
		}
		if (Config.canRegisterBlock(blockGuideId)) {
			OpenBlocks.Blocks.guide = new BlockGuide();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.guide), new Object[] { "ggg", "gtg", "ggg", 'g', new ItemStack(Block.glass), 't', new ItemStack(Block.torchWood) }));
		}
		if (Config.canRegisterBlock(blockElevatorId)) {
			OpenBlocks.Blocks.elevator = new BlockElevator();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.elevator), new Object[] { "www", "wew", "www", 'w', new ItemStack(Block.cloth, 1, Short.MAX_VALUE), 'e', new ItemStack(Item.enderPearl) }));
		}
		if (Config.canRegisterBlock(blockHealId)) {
			OpenBlocks.Blocks.heal = new BlockHeal();
		}
		if (Config.canRegisterBlock(blockLightboxId)) {
			OpenBlocks.Blocks.lightbox = new BlockLightbox();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.lightbox), new Object[] { "igi", "iti", "iii", 'i', new ItemStack(Item.ingotIron), 'g', new ItemStack(Block.thinGlass), 't', new ItemStack(Block.torchWood) }));
		}
		if (Config.canRegisterBlock(blockTargetId)) {
			OpenBlocks.Blocks.target = new BlockTarget();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.target), new Object[] { "www", "www", "s s", 'w', new ItemStack(Block.cloth, 1, Short.MAX_VALUE), 's', "stickWood" }));
		}
		if (Config.canRegisterBlock(blockGraveId)) {
			OpenBlocks.Blocks.grave = new BlockGrave();
		}
		if (Config.canRegisterBlock(blockFlagId)) {
			OpenBlocks.Blocks.flag = new BlockFlag();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.flag, 3), new Object[] { "scc", "sc ", "s  ", 'c', new ItemStack(Block.carpet, 1, Short.MAX_VALUE), 's', "stickWood" }));
		}
		if (Config.canRegisterBlock(blockTankId)) {
			OpenBlocks.Blocks.tank = new BlockTank();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.tank, 2), new Object[] { "ogo", "ggg", "ogo", 'g', new ItemStack(Block.thinGlass), 'o', new ItemStack(Block.obsidian) }));
		}
		if (Config.canRegisterBlock(blockTrophyId)) {
			OpenBlocks.Blocks.trophy = new BlockTrophy();
			MinecraftForge.EVENT_BUS.register(new TrophyHandler());
		}
		if (Config.canRegisterBlock(blockBearTrapId)) {
			OpenBlocks.Blocks.bearTrap = new BlockBearTrap();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.bearTrap), new Object[] { "fif", "fif", "fif", 'f', new ItemStack(Block.fenceIron), 'i', new ItemStack(Item.ingotIron) }));
		}

		if (Config.canRegisterBlock(blockSprinklerId)) {
			OpenBlocks.Blocks.sprinkler = new BlockSprinkler();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.sprinkler, 1), new Object[] { "ifi", "iri", "ifi", 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Block.torchRedstoneActive), 'f', new ItemStack(Block.fenceIron) }));
		}

		if (Config.canRegisterBlock(blockCannonId)) {
			OpenBlocks.Blocks.cannon = new BlockCannon();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.cannon), new Object[] { " d ", " f ", "iri", 'd', new ItemStack(Block.dispenser), 'f', new ItemStack(Block.fenceIron), 'i', new ItemStack(Item.ingotIron), 'r', new ItemStack(Block.blockRedstone) }));
		}

		if (Config.canRegisterBlock(blockVacuumHopperId)) {
			OpenBlocks.Blocks.vacuumHopper = new BlockVacuumHopper();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.vacuumHopper), new ItemStack(Block.hopperBlock), new ItemStack(Block.obsidian), new ItemStack(Item.enderPearl)));
		}

		if (Config.canRegisterBlock(blockSpongeId)) {
			OpenBlocks.Blocks.sponge = new BlockSponge();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.sponge), new ItemStack(Block.cloth, 1, Short.MAX_VALUE), new ItemStack(Item.slimeBall)));
		}

		if (Config.canRegisterBlock(blockBigButton)) {
			OpenBlocks.Blocks.bigButton = new BlockBigButton();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.bigButton), new Object[] { "bb", "bb", 'b', new ItemStack(Block.stoneButton) }));
		}

		if (Config.canRegisterBlock(blockImaginaryId)) {
			OpenBlocks.Blocks.imaginary = new BlockImaginary();
			{
				ItemStack pencil = ItemImaginary.setupValues(null, new ItemStack(OpenBlocks.Blocks.imaginary, 1, 0));
				recipeList.add(new ShapelessOreRecipe(pencil, Item.coal, "stickWood", Item.enderPearl, Item.slimeBall));
			}

			for (Map.Entry<String, Integer> e : ColorUtils.COLORS.entrySet()) {
				ItemStack crayon = ItemImaginary.setupValues(e.getValue(), new ItemStack(OpenBlocks.Blocks.imaginary, 1, 0));
				recipeList.add(new ShapelessOreRecipe(crayon, e.getKey(), Item.paper, Item.enderPearl, Item.slimeBall));
			}

			recipeList.add(new CrayonMixingRecipe());
		}

		if (Config.canRegisterBlock(blockFanId)) {
			OpenBlocks.Blocks.fan = new BlockFan();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.fan), new Object[] { "f", "i", "s", 'f', new ItemStack(Block.fenceIron), 'i', new ItemStack(Item.ingotIron), 's', new ItemStack(Block.stoneSingleSlab) }));
		}

		if (Config.canRegisterBlock(blockXPBottlerId)) {
			OpenBlocks.Blocks.xpBottler = new BlockXPBottler();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.xpBottler), new Object[] { "iii", "ibi", "iii", 'i', new ItemStack(Item.ingotIron), 'b', new ItemStack(Item.glassBottle) }));
		}

		if (Config.canRegisterBlock(blockVillageHighlighterId)) {
			OpenBlocks.Blocks.villageHighlighter = new BlockVillageHighlighter();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.villageHighlighter), new Object[] { "www", "wew", "ccc", 'w', "plankWood", 'e', new ItemStack(Item.emerald), 'c', new ItemStack(Block.cobblestone) }));
		}

		if (Config.canRegisterBlock(blockPathId)) {
			OpenBlocks.Blocks.path = new BlockPath();
			recipeList.add(new ShapelessOreRecipe(new ItemStack(OpenBlocks.Blocks.path, 2), "stone", "cobblestone"));
		}

		if (Config.canRegisterBlock(blockAutoAnvilId)) {
			OpenBlocks.Blocks.autoAnvil = new BlockAutoAnvil();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.autoAnvil), new Object[] { "iii", "iai", "rrr", 'i', new ItemStack(Item.ingotIron), 'a', new ItemStack(Block.anvil, 1, Short.MAX_VALUE), 'r', new ItemStack(Item.redstone) }));
		}

		if (Config.canRegisterBlock(blockAutoEnchantmentTableId)) {
			OpenBlocks.Blocks.autoEnchantmentTable = new BlockAutoEnchantmentTable();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.autoEnchantmentTable), new Object[] { "iii", "iei", "rrr", 'i', new ItemStack(Item.ingotIron), 'e', new ItemStack(Block.enchantmentTable, 1, Short.MAX_VALUE), 'r', new ItemStack(Item.redstone) }));
		}

		if (Config.canRegisterBlock(blockXPDrainId)) {
			OpenBlocks.Blocks.xpDrain = new BlockXPDrain();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.xpDrain), new Object[] { "iii", "iii", "iii", 'i', new ItemStack(Block.fenceIron) }));
		}
		if (Config.canRegisterBlock(blockBlockBreakerId)) {
			OpenBlocks.Blocks.blockBreaker = new BlockBlockBreaker();
			ItemStack specialItem = new ItemStack(Item.pickaxeDiamond);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.blockBreaker), new Object[] { "icc", "src", "icc", 'i', new ItemStack(Item.ingotIron), 'c', new ItemStack(Block.cobblestone), 'r', new ItemStack(Item.redstone), 's', specialItem }));
		}

		if (Config.canRegisterBlock(blockBlockPlacerId)) {
			OpenBlocks.Blocks.blockPlacer = new BlockBlockPlacer();
			ItemStack specialItem = new ItemStack(Block.pistonBase);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.blockPlacer), new Object[] { "icc", "src", "icc", 'i', new ItemStack(Item.ingotIron), 'c', new ItemStack(Block.cobblestone), 'r', new ItemStack(Item.redstone), 's', specialItem }));
		}

		if (Config.canRegisterBlock(blockItemDropperId)) {
			OpenBlocks.Blocks.itemDropper = new BlockItemDropper();
			ItemStack specialItem = new ItemStack(Block.hopperBlock);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.itemDropper), new Object[] { "icc", "src", "icc", 'i', new ItemStack(Item.ingotIron), 'c', new ItemStack(Block.cobblestone), 'r', new ItemStack(Item.redstone), 's', specialItem }));
		}

		if (Config.canRegisterBlock(blockRopeLadderId)) {
			OpenBlocks.Blocks.ropeLadder = new BlockRopeLadder();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.ropeLadder), new Object[] { "sts", "sts", "sts", 't', "stickWood", 's', new ItemStack(Item.silk) }));
		}

		if (Config.canRegisterBlock(blockDonationStationId)) {
			OpenBlocks.Blocks.donationStation = new BlockDonationStation();
			WeightedRandomChestContent drop = new WeightedRandomChestContent(new ItemStack(OpenBlocks.Blocks.donationStation), 1, 1, 2);
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(drop);
			ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(drop);
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.donationStation), new Object[] { "ppp", "pcp", "ppp", 'p', new ItemStack(Item.porkRaw), 'c', new ItemStack(Block.chest) }));
		}

		if (Config.canRegisterBlock(blockPaintMixer)) {
			OpenBlocks.Blocks.paintMixer = new BlockPaintMixer();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.paintMixer), "ooo", "i i", "iii", 'o', Block.obsidian, 'i', Item.ingotIron));
		}

		if (Config.canRegisterBlock(blockCanvasId)) {
			OpenBlocks.Blocks.canvas = new BlockCanvas();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Blocks.canvas, 9), "ppp", "pfp", "ppp", 'p', Item.paper, 'f', Block.fence));
		}

		if (experimentalFeatures && Config.canRegisterBlock(blockMachineOreCrusherId)) {
			OpenBlocks.Blocks.machineOreCrusher = new BlockMachineOreCrusher();
		}

		if (Config.canRegisterBlock(blockPaintCanId)) {
			OpenBlocks.Blocks.paintCan = new BlockPaintCan();
		}

		if (Config.canRegisterBlock(blockCanvasGlassId)) {
			OpenBlocks.Blocks.canvasGlass = new BlockCanvasGlass();
		}

		if (Config.canRegisterBlock(blockProjectorId)) {
			OpenBlocks.Blocks.projector = new BlockProjector();
		}

		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());

		// There is no fail checking here because if the Generic item fails,
		// then I doubt anyone wants this to be silent.
		// Too many items would suffer from this. - NC
		OpenBlocks.Items.generic = new ItemGeneric();
		OpenBlocks.Items.generic.registerItems();
		if (itemFilledBucketId > 0) {
			OpenBlocks.Items.filledBucket = new ItemFilledBucket();
			OpenBlocks.Items.filledBucket.registerItems();
		}
		if (itemHangGliderId > 0) {
			OpenBlocks.Items.hangGlider = new ItemHangGlider();
			recipeList.add(new ShapedOreRecipe(new ItemStack(OpenBlocks.Items.hangGlider), new Object[] { "wsw", 'w', ItemGeneric.Metas.gliderWing.newItemStack(), 's', "stickWood" }));
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

		if (Blocks.cannon != null) {
			EntityRegistry.registerModEntity(EntityMount.class, "BlockEntity", 99, OpenBlocks.instance, Integer.MAX_VALUE, 8, false);
		}

		if (itemCraneControl > 0) {
			OpenBlocks.Items.craneControl = new ItemCraneControl();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.craneControl, "ili", "grg", "iri", 'i', Item.ingotIron, 'g', Item.goldNugget, 'l', Item.glowstone, 'r', Item.redstone));
		}

		if (itemCraneId > 0) {
			OpenBlocks.Items.craneBackpack = new ItemCraneBackpack();
			ItemStack line = ItemGeneric.Metas.line.newItemStack();
			ItemStack beam = ItemGeneric.Metas.beam.newItemStack();
			recipeList.add(new ShapelessOreRecipe(OpenBlocks.Items.craneBackpack, ItemGeneric.Metas.craneEngine.newItemStack(), ItemGeneric.Metas.craneMagnet.newItemStack(), beam, beam, line, line, line, Item.leather));
		}

		if (itemSlimalyzerId > 0) {
			OpenBlocks.Items.slimalyzer = new ItemSlimalyzer();
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.slimalyzer, "igi", "isi", "iri", 'i', Item.ingotIron, 'g', Block.thinGlass, 's', Item.slimeBall, 'r', Item.redstone));
		}

		if (itemSleepingBagId > 0 && ClassTransformerEntityPlayer.IsInBedHookSuccess) {
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
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.squeegee, "sss", " w ", " w ", 's', OpenBlocks.Blocks.sponge, 'w', Item.stick));
		}

		if (itemHeightMap > 0) {
			OpenBlocks.Items.heightMap = new ItemHeightMap();
			if (itemEmptyMap > 0) recipeList.add(new MapCloneRecipe());
		}

		if (itemEmptyMap > 0) {
			OpenBlocks.Items.emptyMap = new ItemEmptyMap();
			recipeList.add(new MapResizeRecipe());

			ItemStack memory = ItemGeneric.Metas.mapMemory.newItemStack(2);
			ItemStack cpu = ItemGeneric.Metas.mapController.newItemStack(1);
			recipeList.add(new ShapedOreRecipe(OpenBlocks.Items.emptyMap.createMap(0),
					" m ", "mcm", " m ",
					'm', memory,
					'c', cpu
					));
		}

		if (itemCartographerId > 0) {
			OpenBlocks.Items.cartographer = new ItemCartographer();
		}

		// move it and cpw will shout at you
		registerItems();
	}

	private static boolean canRegisterBlock(int blockId) {
		if (blockId > 0) {
			if (Block.blocksList[blockId] != null) {
				if (!failIdsQuietly) { throw new RuntimeException("OpenBlocks tried to register a block for ID: "
						+ blockId
						+ " but it was in use. failIdsQuietly is false so I'm yelling at you now."); }
				Log.info("Block ID " + blockId
						+ " in use. This block will *NOT* be loaded.");
				return false;
			}
			return true;
		}
		return false; // Block disabled, fail silently
	}

	private static void registerItems() {
		for (Field f : OpenBlocks.Items.class.getFields()) {
			if (Modifier.isStatic(f.getModifiers()) && Item.class.isAssignableFrom(f.getType())) {
				RegisterItem annotation = f.getAnnotation(RegisterItem.class);
				if (annotation != null) {
					try {
						Item item = (Item)f.get(null);
						if (item != null) {
							String name = "openblocks." + annotation.name();
							GameRegistry.registerItem(item, name);
						}
					} catch (Exception e) {
						throw Throwables.propagate(e);
					}
				} else {
					Log.warn("Field %s has valid type for registration, but no annotation", f);
				}
			}
		}
	}
}