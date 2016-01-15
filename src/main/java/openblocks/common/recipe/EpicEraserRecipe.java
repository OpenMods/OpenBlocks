package openblocks.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import openblocks.common.item.ItemEpicEraser;
import openblocks.enchantments.flimflams.LoreFlimFlam;
import openmods.utils.CustomRecipeBase;
import openmods.utils.InventoryUtils;

public class EpicEraserRecipe extends CustomRecipeBase {

	private static boolean hasLore(ItemStack itemStack) {
		final NBTTagCompound itemTag = itemStack.getTagCompound();
		if (itemTag != null) {
			if (itemTag.hasKey("display", Constants.NBT.TAG_COMPOUND)) {
				final NBTTagCompound displayTag = itemTag.getCompoundTag("display");
				return displayTag.hasKey(LoreFlimFlam.TAG_NAME, Constants.NBT.TAG_LIST) ||
						displayTag.hasKey("Lore", Constants.NBT.TAG_LIST);
			}
		}

		return false;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		boolean eraserFound = false;
		boolean loreItemFound = false;

		for (ItemStack itemStack : InventoryUtils.asIterable(inv)) {
			if (itemStack != null) {
				if (itemStack.getItem() instanceof ItemEpicEraser) {
					if (eraserFound) return false;
					eraserFound = true;
				} else if (hasLore(itemStack)) {
					if (loreItemFound) return false;
					loreItemFound = true;
				} else return false;
			}
		}

		return eraserFound && loreItemFound;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack targetItem = null;
		boolean eraserFound = false;

		for (ItemStack itemStack : InventoryUtils.asIterable(inv)) {
			if (itemStack != null) {
				if (itemStack.getItem() instanceof ItemEpicEraser) {
					if (eraserFound) return null;
					eraserFound = true;
				} else if (hasLore(itemStack)) {
					if (targetItem != null) return null;
					targetItem = itemStack;
				} else return null;
			}
		}

		if (!eraserFound || targetItem == null) return null;

		final ItemStack result = targetItem.copy();
		final NBTTagCompound itemTag = result.getTagCompound();
		final NBTTagCompound displayTag = itemTag.getCompoundTag("display");
		displayTag.removeTag("Lore");
		displayTag.removeTag(LoreFlimFlam.TAG_NAME);

		return result;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

}
