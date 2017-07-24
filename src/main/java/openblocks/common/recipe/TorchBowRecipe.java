package openblocks.common.recipe;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openmods.utils.CustomRecipeBase;

public class TorchBowRecipe extends CustomRecipeBase {

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		return getCraftingResult(inventorycrafting) != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting) {
		ItemStack bowStack = ItemStack.EMPTY;
		ItemStack flintStack = ItemStack.EMPTY;
		ItemStack boneStack = ItemStack.EMPTY;
		for (int i = 0; i < inventorycrafting.getSizeInventory(); i++) {
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() == Items.BOW && bowStack.isEmpty()) {
					bowStack = stack;
					continue;
				} else if (stack.getItem() == Items.FLINT && flintStack.isEmpty()) {
					flintStack = stack;
					continue;
				} else if (stack.getItem() == Items.BONE && boneStack.isEmpty()) {
					boneStack = stack;
					continue;
				}
				return ItemStack.EMPTY;
			}
		}

		if (!bowStack.isEmpty()) {
			ItemStack clone = bowStack.copy();
			NBTTagCompound tag = clone.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			if (flintStack.isEmpty() && boneStack.isEmpty()) return ItemStack.EMPTY;

			if (flintStack.isEmpty()) {
				tag.setBoolean("openblocks_torchmode", false);
				clone.setTagCompound(tag);
				return clone;
			} else if (boneStack.isEmpty()) {
				tag.setBoolean("openblocks_torchmode", true);
				clone.setTagCompound(tag);
				return clone;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(Items.BOW);
	}

}
