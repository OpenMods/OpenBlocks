package openblocks.common.recipe;

import info.openmods.calc.utils.OptionalInt;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginary.PlacementMode;

public class CrayonMergeRecipe implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		OptionalInt color = OptionalInt.ABSENT;
		OptionalInt meta = OptionalInt.ABSENT;
		int count = 0;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			final ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ItemImaginary)) return false;

			final int stackMeta = stack.getMetadata();
			if (meta.isPresent()) {
				if (meta.get() != stackMeta) return false;
			} else {
				meta = OptionalInt.of(stackMeta);
			}

			final NBTTagCompound tag = stack.getTagCompound();
			if (tag != null) {
				final Integer stackColor = ItemImaginary.getColor(tag);
				if (stackColor != null) {
					if (color.isPresent()) {
						if (color.get() != stackColor) return false;
					} else {
						color = OptionalInt.of(stackColor);
					}
				}
			}

			count++;
		}

		return count >= 2;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		OptionalInt color = OptionalInt.ABSENT;
		OptionalInt meta = OptionalInt.ABSENT;
		float uses = 0;

		for (int i = 0; i < inv.getSizeInventory(); i++) {
			final ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) continue;
			if (!(stack.getItem() instanceof ItemImaginary)) return ItemStack.EMPTY;

			final int stackMeta = stack.getMetadata();
			if (meta.isPresent()) {
				if (meta.get() != stackMeta) return ItemStack.EMPTY;
			} else {
				meta = OptionalInt.of(stackMeta);
			}

			final Integer stackColor = ItemImaginary.getColor(stack);
			if (stackColor != null) {
				if (color.isPresent()) {
					if (color.get() != stackColor) return ItemStack.EMPTY;
				} else {
					color = OptionalInt.of(stackColor);
				}
			}

			uses += ItemImaginary.getUses(stack);
		}

		if (!meta.isPresent() || uses == 0) return ItemStack.EMPTY;
		return ItemImaginary.setupValues(new ItemStack(OpenBlocks.Blocks.imaginary, 1, meta.get()), asNullable(color), PlacementMode.BLOCK, uses);
	}

	private static Integer asNullable(OptionalInt color) {
		return color.isPresent()? color.get() : null;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
	}

}
