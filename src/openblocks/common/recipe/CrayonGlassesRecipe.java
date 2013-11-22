package openblocks.common.recipe;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Blocks;
import openblocks.OpenBlocks.Items;
import openblocks.common.item.ItemImaginary;
import openmods.utils.ItemUtils;

import com.google.common.collect.Lists;

public class CrayonGlassesRecipe extends ShapelessRecipes {

	private static List<ItemStack> createFakeIngredientsList() {
		ItemStack block = new ItemStack(Blocks.imaginary);
		ItemImaginary.setupValues(0x00FFFF, block);
		return Lists.newArrayList(new ItemStack(Item.paper), block);
	}

	private static ItemStack createFakeResult() {
		return Items.crayonGlasses.createCrayon(0x00FFFF);
	}

	public CrayonGlassesRecipe() {
		super(createFakeResult(), createFakeIngredientsList()); // just for NEI
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean gotCrayon = false;
		boolean gotPaper = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() instanceof ItemImaginary) {
					if (gotCrayon
							|| ItemImaginary.getUses(stack) < ItemImaginary.CRAFTING_COST) return false;

					gotCrayon = true;
				} else if (stack.getItem() == Item.paper) {
					if (gotPaper) return false;

					gotPaper = true;
				} else return false;
			}
		}

		return gotCrayon && gotPaper;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && stack.getItem() instanceof ItemImaginary) {
				Integer color = ItemUtils.getInt(stack, ItemImaginary.TAG_COLOR);
				return Items.crayonGlasses.createCrayon(color);
			}
		}

		return null;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}
}
