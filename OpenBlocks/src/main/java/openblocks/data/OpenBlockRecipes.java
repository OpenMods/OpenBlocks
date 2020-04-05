package openblocks.data;

import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import openblocks.OpenBlocks;

public class OpenBlockRecipes extends RecipeProvider {
	public OpenBlockRecipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void registerRecipes(final Consumer<IFinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapelessRecipe(OpenBlocks.Blocks.ladder)
				.addIngredient(Blocks.LADDER)
				.addIngredient(ItemTags.WOODEN_TRAPDOORS)
				.addCriterion("has_ladder", hasItem(ItemTags.WOODEN_TRAPDOORS))
				.build(consumer);
	}
}
