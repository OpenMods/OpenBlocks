package openblocks.common.recipe;

import javax.annotation.Nonnull;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openblocks.OpenBlocks.Items;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.MetasGeneric;
import openmods.utils.ItemUtils;

public class MapResizeRecipe extends ShapedOreRecipe {

	public MapResizeRecipe() {
		super(Items.emptyMap.createMap(1), " e ", "eme", " e ",
				'e', MetasGeneric.mapMemory.newItemStack(),
				'm', Items.emptyMap.createMap(0));
	}

	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		if (inventory.getStackInRowAndColumn(0, 0) != null ||
				inventory.getStackInRowAndColumn(0, 2) != null ||
				inventory.getStackInRowAndColumn(2, 0) != null ||
				inventory.getStackInRowAndColumn(2, 2) != null)
			return false;

		for (int i = 0; i < 3; i++) {
			ItemStack left = inventory.getStackInRowAndColumn(0, i);

			if (left.isEmpty() || !MetasGeneric.mapMemory.isA(left)) continue;

			ItemStack right = inventory.getStackInRowAndColumn(2, i);
			if (right.isEmpty() || !MetasGeneric.mapMemory.isA(right)) continue;

			ItemStack middle = inventory.getStackInRowAndColumn(1, i);

			if (!middle.isEmpty() && middle.getItem() instanceof ItemEmptyMap) {
				NBTTagCompound tag = ItemUtils.getItemTag(middle);
				int scale = tag.getByte(ItemEmptyMap.TAG_SCALE);
				return scale < ItemEmptyMap.MAX_SCALE;
			}
		}

		return false;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		for (int i = 0; i < 3; i++) {
			ItemStack middle = inventory.getStackInRowAndColumn(1, i);

			if (!middle.isEmpty() && middle.getItem() instanceof ItemEmptyMap) {
				ItemStack result = middle.copy();
				result.setCount(1);
				NBTTagCompound tag = ItemUtils.getItemTag(result);
				byte currentScale = tag.getByte(ItemEmptyMap.TAG_SCALE);
				tag.setByte(ItemEmptyMap.TAG_SCALE, (byte)Math.min(currentScale + 1, ItemEmptyMap.MAX_SCALE));
				return result;
			}
		}

		return ItemStack.EMPTY;
	}

}
