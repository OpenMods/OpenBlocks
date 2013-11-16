package openblocks.common.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openblocks.OpenBlocks.Items;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.ItemGeneric;
import openblocks.common.item.ItemGeneric.Metas;
import openblocks.utils.ItemUtils;

public class MapResizeRecipe extends ShapedOreRecipe {

	public MapResizeRecipe() {
		super(Items.emptyMap.createMap(1), " e ", "eme", " e ",
				'e', Items.generic.newItemStack(Metas.mapMemory),
				'm', Items.emptyMap.createMap(0));
	}

	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		if (inventory.getStackInRowAndColumn(0, 0) != null ||
				inventory.getStackInRowAndColumn(0, 2) != null ||
				inventory.getStackInRowAndColumn(2, 0) != null ||
				inventory.getStackInRowAndColumn(2, 2) != null) return false;

		for (int i = 0; i < 3; i++) {
			ItemStack left = inventory.getStackInRowAndColumn(0, i);

			if (left == null || !ItemGeneric.isA(left, Metas.mapMemory)) continue;

			ItemStack right = inventory.getStackInRowAndColumn(2, i);
			if (right == null || !ItemGeneric.isA(right, Metas.mapMemory)) continue;

			ItemStack middle = inventory.getStackInRowAndColumn(1, i);

			if (middle != null && middle.getItem() instanceof ItemEmptyMap) {
				NBTTagCompound tag = ItemUtils.getItemTag(middle);
				int scale = tag.getByte(ItemEmptyMap.TAG_SCALE);
				return scale < ItemEmptyMap.MAX_SCALE;
			}
		}

		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		for (int i = 0; i < 3; i++) {
			ItemStack middle = inventory.getStackInRowAndColumn(1, i);

			if (middle != null && middle.getItem() instanceof ItemEmptyMap) {
				ItemStack result = middle.copy();
				result.stackSize = 1;
				NBTTagCompound tag = ItemUtils.getItemTag(result);
				byte currentScale = tag.getByte(ItemEmptyMap.TAG_SCALE);
				tag.setByte(ItemEmptyMap.TAG_SCALE, (byte)Math.min(currentScale + 1, ItemEmptyMap.MAX_SCALE));
				return result;
			}
		}

		return null;
	}

}
