package openblocks.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.ValidationResults;
import net.minecraftforge.registries.ForgeRegistries;
import openblocks.OpenBlocks;
import openmods.sync.drops.SyncDropsFunction;

public class OpenBlocksLoot extends LootTableProvider {

	private static class BlockLootProvider extends BlockLootTables {
		private static LootTable.Builder createGuideDrops(final Block block) {
			return LootTable.builder().addLootPool(
					func_218560_a(block,
							LootPool.builder()
									.rolls(ConstantRange.of(1))
									.addEntry(
											ItemLootEntry.builder(block).acceptFunction(SyncDropsFunction.builder())
									)
					)
			);
		}

		@Override
		public void addTables() {
			func_218492_c(OpenBlocks.Blocks.ladder);
			registerLootTable(OpenBlocks.Blocks.guide, BlockLootProvider::createGuideDrops);
			registerLootTable(OpenBlocks.Blocks.builderGuide, BlockLootProvider::createGuideDrops);
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return Iterables.filter(ForgeRegistries.BLOCKS, b -> b.getRegistryName().getNamespace().equals(OpenBlocks.MODID));
		}
	}

	public OpenBlocksLoot(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
		return ImmutableList.of(Pair.of(BlockLootProvider::new, LootParameterSets.BLOCK));
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationResults validationresults) {
		map.forEach((p_218436_2_, p_218436_3_) -> {
			LootTableManager.func_215302_a(validationresults, p_218436_2_, p_218436_3_, map::get);
		});
	}
}
