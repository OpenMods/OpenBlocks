package openblocks.data;

import static net.minecraft.data.ShapedRecipeBuilder.shapedRecipe;
import static net.minecraft.data.ShapelessRecipeBuilder.shapelessRecipe;

import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import openblocks.OpenBlocks;

public class OpenBlockRecipes extends RecipeProvider {
	public OpenBlockRecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(final Consumer<IFinishedRecipe> consumer) {
		shapelessRecipe(OpenBlocks.Blocks.ladder)
				.addIngredient(Blocks.LADDER)
				.addIngredient(ItemTags.WOODEN_TRAPDOORS)
				.addCriterion("has_ladder", hasItem(ItemTags.WOODEN_TRAPDOORS))
				.build(consumer);

		shapedRecipe(OpenBlocks.Blocks.builderGuide)
				.patternLine("grg")
				.patternLine("ete")
				.patternLine("grg")
				.key('r', Tags.Items.DUSTS_REDSTONE)
				.key('t', Items.TORCH)
				.key('e', Items.ENDER_PEARL)
				.key('g', Tags.Items.GLASS)
				.addCriterion("has_guide", hasItem(OpenBlocks.Items.guide))
				.build(consumer);
		;

		shapedRecipe(OpenBlocks.Blocks.guide)
				.patternLine("grg")
				.patternLine("gtg")
				.patternLine("grg")
				.key('r', Tags.Items.DUSTS_REDSTONE)
				.key('t', Items.TORCH)
				.key('g', Tags.Items.GLASS)
				.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
				.build(consumer);
	}
}
