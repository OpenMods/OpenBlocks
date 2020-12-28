package openblocks.data;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.data.BlockModelDefinition;
import net.minecraft.data.BlockModelFields;
import net.minecraft.data.BlockModelWriter;
import net.minecraft.data.BlockStateVariantBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.FinishedVariantBlockState;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.IFinishedBlockState;
import net.minecraft.data.ModelTextures;
import net.minecraft.data.ModelsResourceUtil;
import net.minecraft.data.ModelsUtil;
import net.minecraft.data.StockTextureAliases;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import openblocks.OpenBlocks;
import openmods.block.BlockRotationMode;
import openmods.geometry.Orientation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OpenBlocksModels implements IDataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
	private final DataGenerator generator;

	public OpenBlocksModels(DataGenerator generator) {
		this.generator = generator;
	}

	private static class BlockModelProvider extends net.minecraft.data.BlockModelProvider {
		private final Consumer<IFinishedBlockState> blockStateOutput;
		private final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;

		public BlockModelProvider(Consumer<IFinishedBlockState> stateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, Consumer<Item> itemOutput) {
			super(stateOutput, modelOutput, itemOutput);
			this.blockStateOutput = stateOutput;
			this.modelOutput = modelOutput;
		}

		@Override
		public void func_239863_a_() {
			makeGuide(OpenBlocks.Blocks.guide, OpenBlocks.location("block/guide_center_normal"));
			makeGuide(OpenBlocks.Blocks.builderGuide, OpenBlocks.location("block/guide_center_ender"));
		}

		private void makeGuide(Block block, final ResourceLocation center) {
			// TODO 1.16 rename inner
			ModelsUtil guide = new ModelsUtil(Optional.of(OpenBlocks.location("block/template_guide")), Optional.empty(), StockTextureAliases.SIDE, StockTextureAliases.END, StockTextureAliases.TORCH);
			ModelsUtil guideHorizontal = new ModelsUtil(Optional.of(OpenBlocks.location("block/template_guide_horizontal")), Optional.of("_horizontal"), StockTextureAliases.SIDE, StockTextureAliases.END, StockTextureAliases.TORCH);

			ModelTextures textures = ModelTextures.func_240375_j_(OpenBlocks.Blocks.guide).func_240349_a_(StockTextureAliases.TORCH, center);

			ResourceLocation topModel = guide.func_240228_a_(block, textures, modelOutput);
			ResourceLocation sideModel = guideHorizontal.func_240228_a_(block, textures, modelOutput);

			blockStateOutput.accept(FinishedVariantBlockState.func_240119_a_(block).func_240125_a_(createThreeFourDispatch(topModel, sideModel)));

			modelOutput.accept(ModelsResourceUtil.func_240219_a_(block.asItem()), new BlockModelWriter(topModel));
		}

		private static BlockStateVariantBuilder.One<Orientation> createThreeFourDispatch(ResourceLocation topModel, ResourceLocation sideModel) {
			return BlockStateVariantBuilder
					.func_240133_a_(BlockRotationMode.THREE_FOUR_DIRECTIONS.getProperty())
					.func_240143_a_(Orientation.XP_YP, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, topModel))
					.func_240143_a_(Orientation.XN_YP, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, topModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R180))
					.func_240143_a_(Orientation.ZP_YP, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, topModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R90))
					.func_240143_a_(Orientation.ZN_YP, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, topModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R270))

					.func_240143_a_(Orientation.YP_XN, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, sideModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R180).replaceInfoValue(BlockModelFields.field_240200_a_, BlockModelFields.Rotation.R270))
					.func_240143_a_(Orientation.YN_XN, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, sideModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R180).replaceInfoValue(BlockModelFields.field_240200_a_, BlockModelFields.Rotation.R90))
					.func_240143_a_(Orientation.ZP_XN, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, sideModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R180))
					.func_240143_a_(Orientation.ZN_XN, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, sideModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R180).replaceInfoValue(BlockModelFields.field_240200_a_, BlockModelFields.Rotation.R180))

					.func_240143_a_(Orientation.XP_ZN, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, sideModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R90))
					.func_240143_a_(Orientation.XN_ZN, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, sideModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R90).replaceInfoValue(BlockModelFields.field_240200_a_, BlockModelFields.Rotation.R180))
					.func_240143_a_(Orientation.YP_ZN, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, sideModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R90).replaceInfoValue(BlockModelFields.field_240200_a_, BlockModelFields.Rotation.R270))
					.func_240143_a_(Orientation.YN_ZN, BlockModelDefinition.getNewModelDefinition().replaceInfoValue(BlockModelFields.field_240202_c_, sideModel).replaceInfoValue(BlockModelFields.field_240201_b_, BlockModelFields.Rotation.R90).replaceInfoValue(BlockModelFields.field_240200_a_, BlockModelFields.Rotation.R90));
		}
	}

	public void act(DirectoryCache cache) {
		Path path = this.generator.getOutputFolder();
		Map<Block, IFinishedBlockState> blocks = Maps.newHashMap();
		Consumer<IFinishedBlockState> blockstateOutput = state -> {
			Block block = state.func_230524_a_();
			IFinishedBlockState prev = blocks.put(block, state);
			if (prev != null) {
				throw new IllegalStateException("Duplicate blockstate definition for " + block);
			}
		};
		Map<ResourceLocation, Supplier<JsonElement>> items = Maps.newHashMap();
		Set<Item> autoItems = Sets.newHashSet();
		BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = (id, model) -> {
			Supplier<JsonElement> prev = items.put(id, model);
			if (prev != null) {
				throw new IllegalStateException("Duplicate model definition for " + id);
			}
		};
		Consumer<Item> itemOutput = autoItems::add;
		new BlockModelProvider(blockstateOutput, modelOutput, itemOutput).func_239863_a_();
		//(new ItemModelProvider(modelOutput)).func_240074_a_();

		this.saveCollection(cache, path, blocks, OpenBlocksModels::getBlockstateLocation);
		this.saveCollection(cache, path, items, OpenBlocksModels::getModelLocation);
	}

	private <T> void saveCollection(DirectoryCache cache, Path root, Map<T, ? extends Supplier<JsonElement>> data, BiFunction<Path, T, Path> nameMapper) {
		data.forEach((id, element) -> {
			Path path = nameMapper.apply(root, id);

			try {
				IDataProvider.save(GSON, cache, element.get(), path);
			} catch (Exception exception) {
				LOGGER.error("Couldn't save {}", path, exception);
			}
		});
	}

	private static Path getBlockstateLocation(Path path, Block id) {
		ResourceLocation resourcelocation = Registry.BLOCK.getKey(id);
		return path.resolve("assets/" + resourcelocation.getNamespace() + "/blockstates/" + resourcelocation.getPath() + ".json");
	}

	private static Path getModelLocation(Path path, ResourceLocation id) {
		return path.resolve("assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json");
	}

	public String getName() {
		return "OpenMods Block State Definitions";
	}
}
