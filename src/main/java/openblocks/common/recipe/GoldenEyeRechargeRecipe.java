package openblocks.common.recipe;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import openblocks.OpenBlocks;
import openblocks.common.item.ItemGoldenEye;

public class GoldenEyeRechargeRecipe extends ShapelessOreRecipe {

	private static final int PEARL_RECHARGE = 10;

	public GoldenEyeRechargeRecipe() {
		super(new ItemStack(OpenBlocks.Items.goldenEye, 1, ItemGoldenEye.MAX_DAMAGE - PEARL_RECHARGE), new ItemStack(OpenBlocks.Items.goldenEye, 1, ItemGoldenEye.MAX_DAMAGE), Items.ender_pearl);
	}

	@Override
	public boolean matches(InventoryCrafting inventory, World world) {
		ItemStack golden = null;
		int enderCount = 0;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) continue;

			Item item = stack.getItem();
			if (item instanceof ItemGoldenEye) golden = stack;
			else if (item instanceof ItemEnderPearl) enderCount++;
			else return false;
		}

		return golden != null && enderCount > 0 && golden.getItemDamage() - enderCount * PEARL_RECHARGE >= 0;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventory) {
		ItemStack golden = null;
		int enderCount = 0;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack == null) continue;

			Item item = stack.getItem();
			if (item instanceof ItemGoldenEye) golden = stack;
			else if (item instanceof ItemEnderPearl) enderCount++;
		}

		if (golden == null) return null;

		ItemStack result = golden.copy();
		result.setItemDamage(golden.getItemDamage() - enderCount * PEARL_RECHARGE);
		return result;
	}

}
