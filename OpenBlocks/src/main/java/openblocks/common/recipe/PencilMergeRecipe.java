package openblocks.common.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginary.PlacementMode;
import openblocks.common.item.ItemImaginaryPencil;

public class PencilMergeRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	@Override
	public String getGroup() {
		return OpenBlocks.location("crayons").toString();
	}

	@Override
	public boolean matches(CraftingInventory inv, World worldIn) {
		int count = 0;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			final ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ItemImaginaryPencil)) return false;
			count++;
		}

		return count >= 2;
	}

	@Override
	public ItemStack getCraftingResult(CraftingInventory inv) {
		float uses = 0;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			final ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ItemImaginaryPencil)) return ItemStack.EMPTY;
			uses += ItemImaginary.getUses(stack);
		}

		if (uses == 0) return ItemStack.EMPTY;
		return ItemImaginaryPencil.setupValues(new ItemStack(OpenBlocks.Blocks.imaginaryPencil), PlacementMode.BLOCK, uses);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height > 1;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}

}
