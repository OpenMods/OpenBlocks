package openblocks.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import openblocks.OpenBlocks.Blocks;
import openblocks.common.item.ItemImaginary;
import openblocks.utils.ItemUtils;

public class CrayonMixingRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		int count = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack == null) continue;
			if ((!(stack.getItem() instanceof ItemImaginary))
					|| !ItemImaginary.isCrayon(stack)) return false;
			if (ItemImaginary.getUses(stack) < ItemImaginary.CRAFTING_COST) continue;
			count++;
		}
		return count > 1;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		int count = 0;
		float r = 0, g = 0, b = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack == null
					|| (ItemImaginary.getUses(stack) < ItemImaginary.CRAFTING_COST)) continue;
			count++;

			int color = ItemUtils.getInt(stack, ItemImaginary.TAG_COLOR);

			r += ((color >> 16) & 0xFF);
			g += ((color >> 8) & 0xFF);
			b += ((color >> 0) & 0xFF);
		}

		if (count < 2) return null;

		int color = (int)(r / count);
		color = (color << 8) + (int)(g / count);
		color = (color << 8) + (int)(b / count);

		return ItemImaginary.setupValues(color, new ItemStack(Blocks.imaginary), count * 0.9f);
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
