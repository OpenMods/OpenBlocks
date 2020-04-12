package openblocks.common.recipe;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.Iterator;
import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.OpenBlocks.Blocks;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginary.PlacementMode;
import openblocks.common.item.ItemImaginaryCrayon;
import openmods.utils.CustomRecipeBase;

public class CrayonMixingRecipe extends CustomRecipeBase {

	public CrayonMixingRecipe() {
		super(OpenBlocks.location("crayons"));
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		int count = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (!(stack.getItem() instanceof ItemImaginaryCrayon))
				return false;
			if (ItemImaginary.getUses(stack) < ItemImaginary.CRAFTING_COST) continue;
			count++;
		}
		return count > 1;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(CraftingInventory inv) {
		int count = 0;
		float r = 0, g = 0, b = 0;
		Multiset<ItemImaginary.PlacementMode> modes = HashMultiset.create();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty() || (ItemImaginary.getUses(stack) < ItemImaginary.CRAFTING_COST)) continue;

			final CompoundNBT tag = stack.getTagCompound();
			if (tag != null) {
				final int color = ItemImaginaryCrayon.getColor(tag);
				count++;

				r += ((color >> 16) & 0xFF);
				g += ((color >> 8) & 0xFF);
				b += ((color >> 0) & 0xFF);

				modes.add(ItemImaginary.getMode(tag));
			}
		}

		if (count < 2) return ItemStack.EMPTY;

		int color = (int)(r / count);
		color = (color << 8) + (int)(g / count);
		color = (color << 8) + (int)(b / count);

		final Iterator<Multiset.Entry<PlacementMode>> iterator = modes.entrySet().iterator();
		Multiset.Entry<PlacementMode> max = iterator.next();
		while (iterator.hasNext()) {
			Multiset.Entry<PlacementMode> tmp = iterator.next();
			if (tmp.getCount() > max.getCount()) max = tmp;
		}

		return ItemImaginaryCrayon.setupValues(new ItemStack(Blocks.imaginaryCrayon), color, max.getElement(), count * 0.9f);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height > 1;
	}
}
