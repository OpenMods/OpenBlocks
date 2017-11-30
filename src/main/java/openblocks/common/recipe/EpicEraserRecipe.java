package openblocks.common.recipe;

import javax.annotation.Nonnull;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemEpicEraser;
import openblocks.enchantments.flimflams.LoreFlimFlam;
import openmods.utils.CustomRecipeBase;
import openmods.utils.InventoryUtils;

public class EpicEraserRecipe extends CustomRecipeBase {

	public EpicEraserRecipe() {
		super(OpenBlocks.location("eraser").toString());
	}

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
			if (!itemStack.isEmpty()) {
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
	@Nonnull
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack targetItem = ItemStack.EMPTY;
		boolean eraserFound = false;

		for (ItemStack itemStack : InventoryUtils.asIterable(inv)) {
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() instanceof ItemEpicEraser) {
					if (eraserFound) return ItemStack.EMPTY;
					eraserFound = true;
				} else if (hasLore(itemStack)) {
					if (!targetItem.isEmpty()) return ItemStack.EMPTY;
					targetItem = itemStack;
				} else return ItemStack.EMPTY;
			}
		}

		if (!eraserFound || targetItem.isEmpty()) return ItemStack.EMPTY;

		final ItemStack result = targetItem.copy();
		final NBTTagCompound itemTag = result.getTagCompound();
		final NBTTagCompound displayTag = itemTag.getCompoundTag("display");
		displayTag.removeTag("Lore");
		displayTag.removeTag(LoreFlimFlam.TAG_NAME);

		return result;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height > 1;
	}

}
