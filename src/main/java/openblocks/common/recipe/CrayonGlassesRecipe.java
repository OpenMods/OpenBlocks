package openblocks.common.recipe;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginary;
import openmods.utils.ItemUtils;

import com.google.common.collect.Lists;

public class CrayonGlassesRecipe extends ShapelessRecipes {

	private static List<ItemStack> createFakeIngredientsList() {
		ItemStack block = new ItemStack(OpenBlocks.Blocks.imaginary, 1, ItemImaginary.DAMAGE_CRAYON);
		ItemImaginary.setupValues(0x00FFFF, block);
		return Lists.newArrayList(new ItemStack(Items.paper), block);
	}

	private static ItemStack createFakeResult() {
		return OpenBlocks.Items.crayonGlasses.createCrayonGlasses(0x00FFFF);
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
				} else if (stack.getItem() == Items.paper) {
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
				NBTTagCompound tag = ItemUtils.getItemTag(stack);
				int color = tag.getInteger(ItemImaginary.TAG_COLOR);
				return OpenBlocks.Items.crayonGlasses.createCrayonGlasses(color);
			}
		}

		return null;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}
}
