package openblocks.common.recipe;

import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginaryCrayon;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;

public class CrayonGlassesRecipe extends ShapelessRecipes {

	private static NonNullList<Ingredient> createFakeIngredientsList() {
		ItemStack block = new ItemStack(OpenBlocks.Blocks.imaginaryCrayon);
		ItemImaginaryCrayon.setupValues(block, 0x00FFFF, ItemImaginary.PlacementMode.BLOCK, ItemImaginary.DEFAULT_USE_COUNT);
		return NonNullList.from(Ingredient.EMPTY, Ingredient.fromItem(Items.PAPER), Ingredient.fromStacks(block));
	}

	@Nonnull
	private static ItemStack createFakeResult() {
		return ItemCrayonGlasses.createCrayonGlasses(OpenBlocks.Items.crayonGlasses, 0x00FFFF);
	}

	public CrayonGlassesRecipe() {
		super(OpenBlocks.location("crayons").toString(), createFakeResult(), createFakeIngredientsList()); // just for NEI
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		int crayonCount = 0;
		int paperCount = 0;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof ItemImaginaryCrayon) {
					crayonCount++;
				} else if (stack.getItem() == Items.PAPER) {
					paperCount++;
				} else return false;
			}
		}

		return crayonCount == 1 && paperCount == 1;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(CraftingInventory inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.getItem() instanceof ItemImaginaryCrayon) {
				int color = ItemImaginaryCrayon.getColor(stack);
				return ItemCrayonGlasses.createCrayonGlasses(OpenBlocks.Items.crayonGlasses, color);
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
