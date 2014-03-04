package openblocks.enchantments.flimflams;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import openblocks.api.IAttackFlimFlam;

public class InventoryShuffleFlimFlam implements IAttackFlimFlam {

	@Override
	public String name() {
		return "inventoryshuffle";
	}

	@Override
	public float weight() {
		return 1;
	}
	
	@Override
	public void execute(EntityPlayer source, EntityPlayer target) {
		if (target.worldObj.isRemote) return;
		int inventorySize = target.inventory.mainInventory.length;
		List<ItemStack> stacks = Arrays.asList(target.inventory.mainInventory);
		Collections.shuffle(stacks);
		target.inventory.mainInventory = stacks.toArray(new ItemStack[inventorySize]);
	}

}
