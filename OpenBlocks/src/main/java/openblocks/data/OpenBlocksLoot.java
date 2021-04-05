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
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import openblocks.OpenBlocks;
import openmods.sync.drops.SyncDropsFunction;

public class OpenBlocksLoot extends LootTableProvider {

	private static class BlockLootProvider extends BlockLootTables {
		private static LootTable.Builder createDroppableTileEntityDrops(final Block block) {
			return LootTable.builder().addLootPool(
					withSurvivesExplosion(block,
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
			registerDropSelfLootTable(OpenBlocks.Blocks.ladder);
			registerDropSelfLootTable(OpenBlocks.Blocks.vacuumHopper);
			registerLootTable(OpenBlocks.Blocks.guide, BlockLootProvider::createDroppableTileEntityDrops);
			registerLootTable(OpenBlocks.Blocks.builderGuide, BlockLootProvider::createDroppableTileEntityDrops);
			registerDropSelfLootTable(OpenBlocks.Blocks.heal);

			registerDropSelfLootTable(OpenBlocks.Blocks.whiteElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.orangeElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.magentaElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.lightBlueElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.yellowElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.limeElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.pinkElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.grayElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.lightGrayElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.cyanElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.purpleElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.blueElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.brownElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.greenElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.redElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.blackElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.whiteRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.orangeRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.magentaRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.lightBlueRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.yellowRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.limeRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.pinkRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.grayRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.lightGrayRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.cyanRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.purpleRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.blueRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.brownRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.greenRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.redRotatingElevator);
			registerDropSelfLootTable(OpenBlocks.Blocks.blackRotatingElevator);

			registerLootTable(OpenBlocks.Blocks.tank, BlockLootProvider::createDroppableTileEntityDrops);
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
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationresults) {
		map.forEach((id, lootTable) -> {
			LootTableManager.validateLootTable(validationresults, id, lootTable);
		});
	}
}
