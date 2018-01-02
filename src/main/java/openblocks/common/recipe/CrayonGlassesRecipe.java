package openblocks.common.recipe;

import javax.annotation.Nonnull;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginationGlasses.ItemCrayonGlasses;
import openmods.utils.ItemUtils;

public class CrayonGlassesRecipe extends ShapelessRecipes {

	private static NonNullList<Ingredient> createFakeIngredientsList() {
		ItemStack block = new ItemStack(OpenBlocks.Blocks.imaginary, 1, ItemImaginary.DAMAGE_CRAYON);
		ItemImaginary.setupValues(block, 0x00FFFF);
		return NonNullList.from(Ingredient.fromItem(Items.PAPER), Ingredient.fromStacks(block));
	}

	@Nonnull
	private static ItemStack createFakeResult() {
		return ItemCrayonGlasses.createCrayonGlasses(OpenBlocks.Items.crayonGlasses, 0x00FFFF);
	}

	public CrayonGlassesRecipe() {
		super(OpenBlocks.location("crayons").toString(), createFakeResult(), createFakeIngredientsList()); // just for NEI
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean gotCrayon = false;
		boolean gotPaper = false;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (ItemImaginary.isCrayon(stack)) {
					if (gotCrayon || ItemImaginary.getUses(stack) < ItemImaginary.CRAFTING_COST) return false;
					gotCrayon = true;
				} else if (stack.getItem() == Items.PAPER) {
					if (gotPaper) return false;
					gotPaper = true;
				} else return false;
			}
		}

		return gotCrayon && gotPaper;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty() && ItemImaginary.isCrayon(stack)) {
				NBTTagCompound tag = ItemUtils.getItemTag(stack);
				int color = tag.getInteger(ItemImaginary.TAG_COLOR);
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
