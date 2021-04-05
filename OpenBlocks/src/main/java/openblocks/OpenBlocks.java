package openblocks;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import openblocks.client.ClientProxy;
import openblocks.common.ElevatorActionHandler;
import openblocks.common.GuideActionHandler;
import openblocks.common.ServerProxy;
import openblocks.common.block.BlockBuilderGuide;
import openblocks.common.block.BlockElevator;
import openblocks.common.block.BlockGuide;
import openblocks.common.block.BlockLadder;
import openblocks.common.block.BlockRotatingElevator;
import openblocks.common.block.BlockTank;
import openblocks.common.block.BlockVacuumHopper;
import openblocks.common.container.ContainerVacuumHopper;
import openblocks.common.item.ItemGuide;
import openblocks.common.item.SlimalyzerItem;
import openblocks.common.tileentity.HealTileEntity;
import openblocks.common.item.ItemTankBlock;
import openblocks.common.tileentity.TileEntityBuilderGuide;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.data.OpenBlockRecipes;
import openblocks.data.OpenBlocksLoot;
import openblocks.data.OpenBlocksModels;
import openblocks.events.ElevatorActionEvent;
import openblocks.events.GuideActionEvent;
import openblocks.rpc.IGuideAnimationTrigger;
import openmods.block.OpenBlock;
import openmods.colors.ColorMeta;
import openmods.container.TileEntityContainerFactory;
import openmods.network.event.NetworkEventEntry;
import openmods.network.event.NetworkEventManager;
import openmods.network.rpc.MethodEntry;
import openmods.network.rpc.RpcCallDispatcher;
import openmods.sync.SyncableObjectType;

@Mod(OpenBlocks.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class OpenBlocks {
	public static final String MODID = "openblocks";

	private static final String BLOCK_LADDER = "ladder";
	private static final String BLOCK_GUIDE = "guide";
	private static final String BLOCK_BUILDER_GUIDE = "builder_guide";
	private static final String BLOCK_VACUUM_HOPPER = "vacuum_hopper";
	private static final String BLOCK_WHITE_ELEVATOR = "white_elevator";
	private static final String BLOCK_ORANGE_ELEVATOR = "orange_elevator";
	private static final String BLOCK_MAGENTA_ELEVATOR = "magenta_elevator";
	private static final String BLOCK_LIGHT_BLUE_ELEVATOR = "light_blue_elevator";
	private static final String BLOCK_YELLOW_ELEVATOR = "yellow_elevator";
	private static final String BLOCK_LIME_ELEVATOR = "lime_elevator";
	private static final String BLOCK_PINK_ELEVATOR = "pink_elevator";
	private static final String BLOCK_GRAY_ELEVATOR = "gray_elevator";
	private static final String BLOCK_LIGHT_GRAY_ELEVATOR = "light_gray_elevator";
	private static final String BLOCK_CYAN_ELEVATOR = "cyan_elevator";
	private static final String BLOCK_PURPLE_ELEVATOR = "purple_elevator";
	private static final String BLOCK_BLUE_ELEVATOR = "blue_elevator";
	private static final String BLOCK_BROWN_ELEVATOR = "brown_elevator";
	private static final String BLOCK_GREEN_ELEVATOR = "green_elevator";
	private static final String BLOCK_RED_ELEVATOR = "red_elevator";
	private static final String BLOCK_BLACK_ELEVATOR = "black_elevator";
	private static final String BLOCK_WHITE_ROTATING_ELEVATOR = "white_rotating_elevator";
	private static final String BLOCK_ORANGE_ROTATING_ELEVATOR = "orange_rotating_elevator";
	private static final String BLOCK_MAGENTA_ROTATING_ELEVATOR = "magenta_rotating_elevator";
	private static final String BLOCK_LIGHT_BLUE_ROTATING_ELEVATOR = "light_blue_rotating_elevator";
	private static final String BLOCK_YELLOW_ROTATING_ELEVATOR = "yellow_rotating_elevator";
	private static final String BLOCK_LIME_ROTATING_ELEVATOR = "lime_rotating_elevator";
	private static final String BLOCK_PINK_ROTATING_ELEVATOR = "pink_rotating_elevator";
	private static final String BLOCK_GRAY_ROTATING_ELEVATOR = "gray_rotating_elevator";
	private static final String BLOCK_LIGHT_GRAY_ROTATING_ELEVATOR = "light_gray_rotating_elevator";
	private static final String BLOCK_CYAN_ROTATING_ELEVATOR = "cyan_rotating_elevator";
	private static final String BLOCK_PURPLE_ROTATING_ELEVATOR = "purple_rotating_elevator";
	private static final String BLOCK_BLUE_ROTATING_ELEVATOR = "blue_rotating_elevator";
	private static final String BLOCK_BROWN_ROTATING_ELEVATOR = "brown_rotating_elevator";
	private static final String BLOCK_GREEN_ROTATING_ELEVATOR = "green_rotating_elevator";
	private static final String BLOCK_RED_ROTATING_ELEVATOR = "red_rotating_elevator";
	private static final String BLOCK_BLACK_ROTATING_ELEVATOR = "black_rotating_elevator";
	private static final String BLOCK_HEAL = "heal";
	private static final String BLOCK_TANK = "tank";

	private static final String ITEM_SLIMALYZER = "slimalyzer";

	private static final String SOUND_ELEVATOR_ACTIVATE = "elevator.activate";
	private static final String SOUND_GRAVE_ROB = "grave.rob";
	private static final String SOUND_BEARTRAP_OPEN = "beartrap.open";
	private static final String SOUND_BEARTRAP_CLOSE = "beartrap.close";
	private static final String SOUND_CANNON_ACTIVATE = "cannon.activate";
	private static final String SOUND_TARGET_OPEN = "target.open";
	private static final String SOUND_TARGET_CLOSE = "target.close";
	private static final String SOUND_BOTTLER_SIGNAL = "bottler.signal";
	private static final String SOUND_CRAYON_PLACE = "crayon.place";
	private static final String SOUND_LUGGAGE_WALK = "luggage.walk";
	private static final String SOUND_LUGGAGE_EAT_FOOD = "luggage.eat.food";
	private static final String SOUND_LUGGAGE_EAT_ITEM = "luggage.eat.item";
	private static final String SOUND_PEDOMETER_USE = "pedometer.use";
	private static final String SOUND_SLIMALYZER_SIGNAL = "slimalyzer.signal";
	private static final String SOUND_SQUEEGEE_USE = "squeegee.use";
	private static final String SOUND_BEST_FEATURE_EVER_FART = "best.feature.ever.fart";
	private static final String SOUND_ANNOYING_MOSQUITO = "annoying.mosquito";
	private static final String SOUND_ANNOYING_ALARMCLOCK = "annoying.alarmclock";
	private static final String SOUND_ANNOYING_VIBRATE = "annoying.vibrate";

	private static final String FLUID_XP_STILL = "xpjuice_still";
	private static final String FLUID_XP_FLOWING = "xpjuice_flowing";

	public static ResourceLocation location(String path) {
		return new ResourceLocation(MODID, path);
	}

	public static IOpenBlocksProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public static final ItemGroup OPEN_BLOCKS_TAB = new ItemGroup("openblocks") {
		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack createIcon() {
			return new ItemStack(Blocks.ladder);
		}
	};

	@ObjectHolder(MODID)
	public static class Blocks {
		@ObjectHolder(BLOCK_LADDER)
		public static Block ladder;

		@ObjectHolder(BLOCK_GUIDE)
		public static Block guide;

		@ObjectHolder(BLOCK_BUILDER_GUIDE)
		public static Block builderGuide;

		@ObjectHolder(BLOCK_VACUUM_HOPPER)
		public static Block vacuumHopper;

		@ObjectHolder(BLOCK_WHITE_ELEVATOR)
		public static Block whiteElevator;

		@ObjectHolder(BLOCK_ORANGE_ELEVATOR)
		public static Block orangeElevator;

		@ObjectHolder(BLOCK_MAGENTA_ELEVATOR)
		public static Block magentaElevator;

		@ObjectHolder(BLOCK_LIGHT_BLUE_ELEVATOR)
		public static Block lightBlueElevator;

		@ObjectHolder(BLOCK_YELLOW_ELEVATOR)
		public static Block yellowElevator;

		@ObjectHolder(BLOCK_LIME_ELEVATOR)
		public static Block limeElevator;

		@ObjectHolder(BLOCK_PINK_ELEVATOR)
		public static Block pinkElevator;

		@ObjectHolder(BLOCK_GRAY_ELEVATOR)
		public static Block grayElevator;

		@ObjectHolder(BLOCK_LIGHT_GRAY_ELEVATOR)
		public static Block lightGrayElevator;

		@ObjectHolder(BLOCK_CYAN_ELEVATOR)
		public static Block cyanElevator;

		@ObjectHolder(BLOCK_PURPLE_ELEVATOR)
		public static Block purpleElevator;

		@ObjectHolder(BLOCK_BLUE_ELEVATOR)
		public static Block blueElevator;

		@ObjectHolder(BLOCK_BROWN_ELEVATOR)
		public static Block brownElevator;

		@ObjectHolder(BLOCK_GREEN_ELEVATOR)
		public static Block greenElevator;

		@ObjectHolder(BLOCK_RED_ELEVATOR)
		public static Block redElevator;

		@ObjectHolder(BLOCK_BLACK_ELEVATOR)
		public static Block blackElevator;

		@ObjectHolder(BLOCK_WHITE_ROTATING_ELEVATOR)
		public static Block whiteRotatingElevator;

		@ObjectHolder(BLOCK_ORANGE_ROTATING_ELEVATOR)
		public static Block orangeRotatingElevator;

		@ObjectHolder(BLOCK_MAGENTA_ROTATING_ELEVATOR)
		public static Block magentaRotatingElevator;

		@ObjectHolder(BLOCK_LIGHT_BLUE_ROTATING_ELEVATOR)
		public static Block lightBlueRotatingElevator;

		@ObjectHolder(BLOCK_YELLOW_ROTATING_ELEVATOR)
		public static Block yellowRotatingElevator;

		@ObjectHolder(BLOCK_LIME_ROTATING_ELEVATOR)
		public static Block limeRotatingElevator;

		@ObjectHolder(BLOCK_PINK_ROTATING_ELEVATOR)
		public static Block pinkRotatingElevator;

		@ObjectHolder(BLOCK_GRAY_ROTATING_ELEVATOR)
		public static Block grayRotatingElevator;

		@ObjectHolder(BLOCK_LIGHT_GRAY_ROTATING_ELEVATOR)
		public static Block lightGrayRotatingElevator;

		@ObjectHolder(BLOCK_CYAN_ROTATING_ELEVATOR)
		public static Block cyanRotatingElevator;

		@ObjectHolder(BLOCK_PURPLE_ROTATING_ELEVATOR)
		public static Block purpleRotatingElevator;

		@ObjectHolder(BLOCK_BLUE_ROTATING_ELEVATOR)
		public static Block blueRotatingElevator;

		@ObjectHolder(BLOCK_BROWN_ROTATING_ELEVATOR)
		public static Block brownRotatingElevator;

		@ObjectHolder(BLOCK_GREEN_ROTATING_ELEVATOR)
		public static Block greenRotatingElevator;

		@ObjectHolder(BLOCK_RED_ROTATING_ELEVATOR)
		public static Block redRotatingElevator;

		@ObjectHolder(BLOCK_BLACK_ROTATING_ELEVATOR)
		public static Block blackRotatingElevator;

		@ObjectHolder(BLOCK_HEAL)
		public static Block heal;

		@ObjectHolder(BLOCK_TANK)
		public static Block tank;
	}

	@ObjectHolder(MODID)
	public static class Items {
		@ObjectHolder(BLOCK_LADDER)
		public static Item ladder;

		@ObjectHolder(BLOCK_GUIDE)
		public static Item guide;

		@ObjectHolder(BLOCK_BUILDER_GUIDE)
		public static Item builderGuide;

		@ObjectHolder(BLOCK_VACUUM_HOPPER)
		public static Item vacuumHopper;

		@ObjectHolder(BLOCK_HEAL)
		public static Item heal;

		@ObjectHolder(ITEM_SLIMALYZER)
		public static Item slimalyzer;
		@ObjectHolder(BLOCK_TANK)
		public static Item tank;
	}

	@ObjectHolder(MODID)
	public static class TileEntities {
		@ObjectHolder(BLOCK_GUIDE)
		public static TileEntityType<TileEntityGuide> guide;

		@ObjectHolder(BLOCK_BUILDER_GUIDE)
		public static TileEntityType<TileEntityBuilderGuide> builderGuide;

		@ObjectHolder(BLOCK_VACUUM_HOPPER)
		public static TileEntityType<TileEntityVacuumHopper> vacuumHopper;

		@ObjectHolder(BLOCK_HEAL)
		public static TileEntityType<TileEntityVacuumHopper> heal;

		@ObjectHolder(BLOCK_TANK)
		public static TileEntityType<TileEntityTank> tank;
	}

	@ObjectHolder(MODID)
	public static class Sounds {
		@ObjectHolder(SOUND_ELEVATOR_ACTIVATE)
		public static SoundEvent BLOCK_ELEVATOR_ACTIVATE;

		@ObjectHolder(SOUND_GRAVE_ROB)
		public static SoundEvent BLOCK_GRAVE_ROB;

		@ObjectHolder(SOUND_BEARTRAP_OPEN)
		public static SoundEvent BLOCK_BEARTRAP_OPEN;

		@ObjectHolder(SOUND_BEARTRAP_CLOSE)
		public static SoundEvent BLOCK_BEARTRAP_CLOSE;

		@ObjectHolder(SOUND_CANNON_ACTIVATE)
		public static SoundEvent BLOCK_CANNON_ACTIVATE;

		@ObjectHolder(SOUND_TARGET_OPEN)
		public static SoundEvent BLOCK_TARGET_OPEN;

		@ObjectHolder(SOUND_TARGET_CLOSE)
		public static SoundEvent BLOCK_TARGET_CLOSE;

		@ObjectHolder(SOUND_BOTTLER_SIGNAL)
		public static SoundEvent BLOCK_XPBOTTLER_DONE;

		@ObjectHolder(SOUND_CRAYON_PLACE)
		public static SoundEvent ITEM_CRAYON_PLACE;

		@ObjectHolder(SOUND_LUGGAGE_WALK)
		public static SoundEvent ENTITY_LUGGAGE_WALK;

		@ObjectHolder(SOUND_LUGGAGE_EAT_FOOD)
		public static SoundEvent ENTITY_LUGGAGE_EAT_FOOD;

		@ObjectHolder(SOUND_LUGGAGE_EAT_ITEM)
		public static SoundEvent ENTITY_LUGGAGE_EAT_ITEM;

		@ObjectHolder(SOUND_PEDOMETER_USE)
		public static SoundEvent ITEM_PEDOMETER_USE;

		@ObjectHolder(SOUND_SLIMALYZER_SIGNAL)
		public static SoundEvent ITEM_SLIMALYZER_PING;

		@ObjectHolder(SOUND_SQUEEGEE_USE)
		public static SoundEvent ITEM_SQUEEGEE_ACTION;

		@ObjectHolder(SOUND_BEST_FEATURE_EVER_FART)
		public static SoundEvent PLAYER_WHOOPS;

		@ObjectHolder(SOUND_ANNOYING_MOSQUITO)
		public static SoundEvent MISC_MOSQUITO;

		@ObjectHolder(SOUND_ANNOYING_ALARMCLOCK)
		public static SoundEvent MISC_ALARM_CLOCK;

		@ObjectHolder(SOUND_ANNOYING_VIBRATE)
		public static SoundEvent MISC_VIBRATE;

		@ObjectHolder("minecraft:entity.painting.place")
		public static SoundEvent ENTITY_GLYPH_PLACE;

		@ObjectHolder("minecraft:entity.painting.break")
		public static SoundEvent ENTITY_GLYPH_BREAK;
	}

	@ObjectHolder(MODID)
	public static class Enchantments {
	}

	@ObjectHolder(MODID)
	public static class Fluids {
		@ObjectHolder(FLUID_XP_STILL)
		public static Fluid xpJuice;

		@ObjectHolder(FLUID_XP_FLOWING)
		public static Fluid xpJuiceFlowing;
	}

	@ObjectHolder(MODID)
	public static class Containers {
		@ObjectHolder(BLOCK_VACUUM_HOPPER)
		public static ContainerType<ContainerVacuumHopper> vacuumHopper;
	}

	public OpenBlocks() {
		PROXY.eventInit();
	}

	@SubscribeEvent
	public void registerRegistry(RegistryEvent.NewRegistry e) {
		PROXY.syncInit();
	}

	@SubscribeEvent
	public static void registerSyncTypes(final RegistryEvent.Register<SyncableObjectType> type) {
	}

	@SubscribeEvent
	public static void registerMethodTypes(final RegistryEvent.Register<MethodEntry> evt) {
		RpcCallDispatcher.startMethodRegistration(evt.getRegistry())
				.registerInterface(location("guide_animation"), IGuideAnimationTrigger.class);
	}

	@SubscribeEvent
	public static void registerNetworkEvents(final RegistryEvent.Register<NetworkEventEntry> evt) {
		NetworkEventManager.startRegistration(evt.getRegistry())
				.register(location("guide_action"), GuideActionEvent.class, GuideActionEvent::new)
				.register(location("elevator_action"), ElevatorActionEvent.class, ElevatorActionEvent::new);
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> evt) {
		final IForgeRegistry<Block> registry = evt.getRegistry();
		registry.register(new BlockLadder(Block.Properties.from(net.minecraft.block.Blocks.OAK_TRAPDOOR)).setRegistryName(BLOCK_LADDER));
		registry.register(new BlockGuide(Block.Properties.create(Material.ROCK).notSolid().setLightLevel(v -> 10)).setTileEntity(TileEntityGuide.class).setRegistryName(BLOCK_GUIDE));
		registry.register(new BlockBuilderGuide(Block.Properties.create(Material.ROCK).notSolid().setLightLevel(v -> 10)).setTileEntity(TileEntityBuilderGuide.class).setRegistryName(BLOCK_BUILDER_GUIDE));
		registry.register(new BlockVacuumHopper(Block.Properties.create(Material.ROCK)).setTileEntity(TileEntityVacuumHopper.class).setRegistryName(BLOCK_VACUUM_HOPPER));
		registry.register(new OpenBlock(Block.Properties.create(Material.ROCK)).setTileEntity(HealTileEntity.class).setRegistryName(BLOCK_HEAL));

		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.WHITE).setRegistryName(BLOCK_WHITE_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.ORANGE).setRegistryName(BLOCK_ORANGE_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.MAGENTA).setRegistryName(BLOCK_MAGENTA_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.LIGHT_BLUE).setRegistryName(BLOCK_LIGHT_BLUE_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.YELLOW).setRegistryName(BLOCK_YELLOW_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.LIME).setRegistryName(BLOCK_LIME_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.PINK).setRegistryName(BLOCK_PINK_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.GRAY).setRegistryName(BLOCK_GRAY_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.LIGHT_GRAY).setRegistryName(BLOCK_LIGHT_GRAY_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.CYAN).setRegistryName(BLOCK_CYAN_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.PURPLE).setRegistryName(BLOCK_PURPLE_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.BLUE).setRegistryName(BLOCK_BLUE_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.BROWN).setRegistryName(BLOCK_BROWN_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.GREEN).setRegistryName(BLOCK_GREEN_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.RED).setRegistryName(BLOCK_RED_ELEVATOR));
		registry.register(BlockElevator.create(Material.ROCK, ColorMeta.BLACK).setRegistryName(BLOCK_BLACK_ELEVATOR));

		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.WHITE).setRegistryName(BLOCK_WHITE_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.ORANGE).setRegistryName(BLOCK_ORANGE_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.MAGENTA).setRegistryName(BLOCK_MAGENTA_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.LIGHT_BLUE).setRegistryName(BLOCK_LIGHT_BLUE_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.YELLOW).setRegistryName(BLOCK_YELLOW_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.LIME).setRegistryName(BLOCK_LIME_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.PINK).setRegistryName(BLOCK_PINK_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.GRAY).setRegistryName(BLOCK_GRAY_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.LIGHT_GRAY).setRegistryName(BLOCK_LIGHT_GRAY_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.CYAN).setRegistryName(BLOCK_CYAN_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.PURPLE).setRegistryName(BLOCK_PURPLE_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.BLUE).setRegistryName(BLOCK_BLUE_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.BROWN).setRegistryName(BLOCK_BROWN_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.GREEN).setRegistryName(BLOCK_GREEN_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.RED).setRegistryName(BLOCK_RED_ROTATING_ELEVATOR));
		registry.register(BlockRotatingElevator.create(Material.ROCK, ColorMeta.BLACK).setRegistryName(BLOCK_BLACK_ROTATING_ELEVATOR));

		registry.register(new BlockTank(Block.Properties.create(Material.ROCK).notSolid()).setTileEntity(TileEntityTank.class).setRegistryName(BLOCK_TANK));
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> evt) {
		final IForgeRegistry<Item> registry = evt.getRegistry();
		registry.register(new BlockItem(Blocks.ladder, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_LADDER));
		registry.register(new ItemGuide(Blocks.guide, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_GUIDE));
		registry.register(new ItemGuide(Blocks.builderGuide, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_BUILDER_GUIDE));
		registry.register(new BlockItem(Blocks.vacuumHopper, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_VACUUM_HOPPER));
		registry.register(new BlockItem(Blocks.heal, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_HEAL));

		registry.register(new BlockItem(Blocks.whiteElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_WHITE_ELEVATOR));
		registry.register(new BlockItem(Blocks.orangeElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_ORANGE_ELEVATOR));
		registry.register(new BlockItem(Blocks.magentaElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_MAGENTA_ELEVATOR));
		registry.register(new BlockItem(Blocks.lightBlueElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_LIGHT_BLUE_ELEVATOR));
		registry.register(new BlockItem(Blocks.yellowElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_YELLOW_ELEVATOR));
		registry.register(new BlockItem(Blocks.limeElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_LIME_ELEVATOR));
		registry.register(new BlockItem(Blocks.pinkElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_PINK_ELEVATOR));
		registry.register(new BlockItem(Blocks.grayElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_GRAY_ELEVATOR));
		registry.register(new BlockItem(Blocks.lightGrayElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_LIGHT_GRAY_ELEVATOR));
		registry.register(new BlockItem(Blocks.cyanElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_CYAN_ELEVATOR));
		registry.register(new BlockItem(Blocks.purpleElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_PURPLE_ELEVATOR));
		registry.register(new BlockItem(Blocks.blueElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_BLUE_ELEVATOR));
		registry.register(new BlockItem(Blocks.brownElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_BROWN_ELEVATOR));
		registry.register(new BlockItem(Blocks.greenElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_GREEN_ELEVATOR));
		registry.register(new BlockItem(Blocks.redElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_RED_ELEVATOR));
		registry.register(new BlockItem(Blocks.blackElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_BLACK_ELEVATOR));

		registry.register(new BlockItem(Blocks.whiteRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_WHITE_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.orangeRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_ORANGE_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.magentaRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_MAGENTA_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.lightBlueRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_LIGHT_BLUE_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.yellowRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_YELLOW_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.limeRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_LIME_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.pinkRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_PINK_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.grayRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_GRAY_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.lightGrayRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_LIGHT_GRAY_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.cyanRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_CYAN_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.purpleRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_PURPLE_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.blueRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_BLUE_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.brownRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_BROWN_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.greenRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_GREEN_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.redRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_RED_ROTATING_ELEVATOR));
		registry.register(new BlockItem(Blocks.blackRotatingElevator, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_BLACK_ROTATING_ELEVATOR));

		registry.register(new SlimalyzerItem(new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(ITEM_SLIMALYZER));
		registry.register(new ItemTankBlock(Blocks.tank, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_TANK));
	}

	@SubscribeEvent
	public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> evt) {
		final IForgeRegistry<TileEntityType<?>> registry = evt.getRegistry();
		registry.register(new TileEntityType<>(TileEntityGuide::new, ImmutableSet.of(Blocks.guide), null).setRegistryName(BLOCK_GUIDE));
		registry.register(new TileEntityType<>(TileEntityBuilderGuide::new, ImmutableSet.of(Blocks.builderGuide), null).setRegistryName(BLOCK_BUILDER_GUIDE));
		registry.register(new TileEntityType<>(TileEntityVacuumHopper::new, ImmutableSet.of(Blocks.vacuumHopper), null).setRegistryName(BLOCK_VACUUM_HOPPER));
		registry.register(new TileEntityType<>(HealTileEntity::new, ImmutableSet.of(Blocks.heal), null).setRegistryName(BLOCK_HEAL));
		registry.register(new TileEntityType<>(TileEntityTank::new, ImmutableSet.of(Blocks.tank), null).setRegistryName(BLOCK_TANK));
	}

	@SubscribeEvent
	public static void registerFluids(final RegistryEvent.Register<Fluid> evt) {
		final IForgeRegistry<Fluid> registry = evt.getRegistry();
		FluidAttributes.Builder xpJuiceAttributes = FluidAttributes.builder(location("block/xp_juice_still"), location("block/xp_juice_flowing"))
				.luminosity(10)
				.density(800)
				.viscosity(1500)
				.translationKey("fluid.openblocks.xp_juice")
				.sound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP);

		registry.register(new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(
				() -> Fluids.xpJuice,
				() -> Fluids.xpJuiceFlowing,
				xpJuiceAttributes
		)).setRegistryName(FLUID_XP_STILL));

		registry.register(new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(
				() -> Fluids.xpJuice,
				() -> Fluids.xpJuiceFlowing,
				xpJuiceAttributes
		)).setRegistryName(FLUID_XP_FLOWING));
	}

	@SubscribeEvent
	public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> evt) {
		final IForgeRegistry<ContainerType<?>> registry = evt.getRegistry();
		registry.register(new ContainerType<>(new TileEntityContainerFactory<>(ContainerVacuumHopper::new, TileEntities.vacuumHopper)).setRegistryName(BLOCK_VACUUM_HOPPER));
	}

	@SubscribeEvent
	public static void registerSounds(final RegistryEvent.Register<SoundEvent> evt) {
		final IForgeRegistry<SoundEvent> registry = evt.getRegistry();

		registerSound(registry, SOUND_ELEVATOR_ACTIVATE);
		registerSound(registry, SOUND_GRAVE_ROB);
		registerSound(registry, SOUND_BEARTRAP_OPEN);
		registerSound(registry, SOUND_BEARTRAP_CLOSE);
		registerSound(registry, SOUND_CANNON_ACTIVATE);
		registerSound(registry, SOUND_TARGET_OPEN);
		registerSound(registry, SOUND_TARGET_CLOSE);
		registerSound(registry, SOUND_BOTTLER_SIGNAL);
		registerSound(registry, SOUND_CRAYON_PLACE);
		registerSound(registry, SOUND_LUGGAGE_WALK);
		registerSound(registry, SOUND_LUGGAGE_EAT_FOOD);
		registerSound(registry, SOUND_LUGGAGE_EAT_ITEM);
		registerSound(registry, SOUND_PEDOMETER_USE);
		registerSound(registry, SOUND_SLIMALYZER_SIGNAL);
		registerSound(registry, SOUND_SQUEEGEE_USE);
		registerSound(registry, SOUND_BEST_FEATURE_EVER_FART);
		registerSound(registry, SOUND_ANNOYING_MOSQUITO);
		registerSound(registry, SOUND_ANNOYING_ALARMCLOCK);
		registerSound(registry, SOUND_ANNOYING_VIBRATE);
	}

	private static void registerSound(IForgeRegistry<SoundEvent> registry, String name) {
		final ResourceLocation id = location(name);
		registry.register(new SoundEvent(id).setRegistryName(id));
	}

	@SubscribeEvent
	public static void commonInit(final FMLCommonSetupEvent evt) {
		MinecraftForge.EVENT_BUS.addListener(GuideActionHandler::onEvent);
		MinecraftForge.EVENT_BUS.addListener(ElevatorActionHandler::onElevatorEvent);
		MinecraftForge.EVENT_BUS.register(new TileEntityTank.BucketFillHandler()
				.addFilledBucket(net.minecraft.item.Items.LAVA_BUCKET)
				.addFilledBucket(net.minecraft.item.Items.WATER_BUCKET)
				// TODO xp bucket? Figure out correct approach.
		);
	}

	@SubscribeEvent
	public static void clientInit(final FMLClientSetupEvent evt) {
		PROXY.clientInit(evt);
	}

	@SubscribeEvent
	public static void registerGenerators(final GatherDataEvent event) {
		final DataGenerator generator = event.getGenerator();
		generator.addProvider(new OpenBlockRecipes(generator));
		generator.addProvider(new OpenBlocksLoot(generator));
		generator.addProvider(new OpenBlocksModels(generator));
	}
}
