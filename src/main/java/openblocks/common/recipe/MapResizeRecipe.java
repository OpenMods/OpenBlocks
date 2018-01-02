package openblocks.common.recipe;

import javax.annotation.Nonnull;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Items;
import openblocks.common.item.ItemEmptyMap;
import openblocks.common.item.MetasGeneric;
import openmods.utils.ItemUtils;

public class MapResizeRecipe extends ShapedOreRecipe {

	private static ShapedPrimer createExample() {
		final ShapedPrimer example = new ShapedPrimer();
		example.width = 3;
		example.height = 3;

		Ingredient e = Ingredient.fromStacks(MetasGeneric.mapMemory.newItemStack());
		Ingredient m = Ingredient.fromStacks(ItemEmptyMap.createMap(Items.emptyMap, 0));

		example.input = NonNullList.from(
				Ingredient.EMPTY, e, Ingredient.EMPTY,
				e, m, e,
				Ingredient.EMPTY, e, Ingredient.EMPTY);

		return example;
	}

	public MapResizeRecipe() {
		super(OpenBlocks.location("crayons"), ItemEmptyMap.createMap(Items.emptyMap, 1), createExample());
	}

	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
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

	@Override
	public boolean isDynamic() {
		return true;
	}

}
