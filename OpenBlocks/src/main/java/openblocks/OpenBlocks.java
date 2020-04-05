package openblocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import openblocks.common.block.BlockLadder;
import openblocks.data.OpenBlockRecipes;
import openmods.network.event.NetworkEventEntry;
import openmods.network.rpc.MethodEntry;
import openmods.sync.SyncableObjectType;

@Mod(OpenBlocks.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class OpenBlocks {
	public static final String MODID = "openblocks";

	public static ResourceLocation location(String path) {
		return new ResourceLocation(MODID, path);
	}

	private static final String BLOCK_LADDER = "ladder";

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
	}

	@ObjectHolder(MODID)
	public static class Items {
		@ObjectHolder(BLOCK_LADDER)
		public static Item ladder;
	}

	@ObjectHolder(MODID)
	public static class Sounds {

	}

	@ObjectHolder(MODID)
	public static class Enchantments {
	}

	@SubscribeEvent
	public static void registerSyncTypes(final RegistryEvent.Register<SyncableObjectType> type) {
	}

	@SubscribeEvent
	public static void registerMethodTypes(final RegistryEvent.Register<MethodEntry> evt) {
	}

	@SubscribeEvent
	public static void registerNetworkEvents(final RegistryEvent.Register<NetworkEventEntry> evt) {
	}

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> evt) {
		final IForgeRegistry<Block> registry = evt.getRegistry();
		registry.register(new BlockLadder(Block.Properties.from(net.minecraft.block.Blocks.OAK_TRAPDOOR)).setRegistryName(BLOCK_LADDER));
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> evt) {
		final IForgeRegistry<Item> registry = evt.getRegistry();
		registry.register(new BlockItem(Blocks.ladder, new Item.Properties().group(OPEN_BLOCKS_TAB)).setRegistryName(BLOCK_LADDER));
	}

	@SubscribeEvent
	public static void preInit(final FMLCommonSetupEvent evt) {

	}

	@SubscribeEvent
	public static void registerGenerators(final GatherDataEvent event) {
		final DataGenerator generator = event.getGenerator();
		generator.addProvider(new OpenBlockRecipes(generator));
	}
}
