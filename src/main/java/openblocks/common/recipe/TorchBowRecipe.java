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
		ItemStack bowStack = null;
		ItemStack flintStack = null;
		ItemStack boneStack = null;
		for (int i = 0; i < inventorycrafting.getSizeInventory(); i++) {
			ItemStack stack = inventorycrafting.getStackInSlot(i);
			if (stack != null) {
				if (stack.getItem() == Items.BOW && bowStack == null) {
					bowStack = stack;
					continue;
				} else if (stack.getItem() == Items.FLINT && flintStack == null) {
					flintStack = stack;
					continue;
				} else if (stack.getItem() == Items.BONE && boneStack == null) {
					boneStack = stack;
					continue;
				}
				return null;
			}
		}

		if (bowStack != null) {
			ItemStack clone = bowStack.copy();
			NBTTagCompound tag = clone.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
			}
			if (flintStack == null && boneStack == null) { return null; }
			if (flintStack == null) {
				// System.out.println("clear torch mode");
				tag.setBoolean("openblocks_torchmode", false);
				clone.setTagCompound(tag);
				return clone;
			} else if (boneStack == null) {
				// System.out.println("torch mode");
				tag.setBoolean("openblocks_torchmode", true);
				clone.setTagCompound(tag);
				return clone;
			}
		}
		return null;
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
