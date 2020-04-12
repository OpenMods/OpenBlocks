package openblocks.common.recipe;

import javax.annotation.Nonnull;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemGoldenEye;

public class GoldenEyeRechargeRecipe extends ShapelessRecipes {

	private static final int PEARL_RECHARGE = 10;

	private static ItemStack createDummyOutput() {
		return new ItemStack(OpenBlocks.Items.goldenEye, 1, ItemGoldenEye.MAX_DAMAGE - PEARL_RECHARGE);
	}

	private static Ingredient createDummyInput() {
		return Ingredient.fromStacks(new ItemStack(OpenBlocks.Items.goldenEye, 1, ItemGoldenEye.MAX_DAMAGE));
	}

	public GoldenEyeRechargeRecipe() {
		super(OpenBlocks.location("golden_eye").toString(), createDummyOutput(), NonNullList.from(Ingredient.EMPTY, createDummyInput(), Ingredient.fromItem(Items.ENDER_PEARL)));
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		ItemStack golden = ItemStack.EMPTY;
		int enderCount = 0;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) continue;

			Item item = stack.getItem();
			if (item instanceof ItemGoldenEye) golden = stack;
			else if (item instanceof EnderPearlItem) enderCount++;
			else return false;
		}

		return !golden.isEmpty() && enderCount > 0 && golden.getItemDamage() - enderCount * PEARL_RECHARGE >= 0;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(CraftingInventory inventory) {
		ItemStack golden = ItemStack.EMPTY;
		int enderCount = 0;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack.isEmpty()) continue;

			Item item = stack.getItem();
			if (item instanceof ItemGoldenEye) golden = stack;
			else if (item instanceof EnderPearlItem) enderCount++;
		}

		if (golden.isEmpty()) return ItemStack.EMPTY;

		ItemStack result = golden.copy();
		result.setItemDamage(golden.getItemDamage() - enderCount * PEARL_RECHARGE);
		return result;
	}

}
