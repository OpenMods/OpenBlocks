package openblocks.enchantments.flimflams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamEffect;

public class InventoryShuffleFlimFlam implements IFlimFlamEffect {

	@Override
	public String name() {
		return "inventoryshuffle";
	}

	@Override
	public boolean execute(EntityPlayerMP target) {
		final ItemStack[] mainInventory = target.inventory.mainInventory;
		List<ItemStack> stacks = Arrays.asList(mainInventory);
		Collections.shuffle(stacks);
		target.inventory.mainInventory = stacks.toArray(mainInventory);
		return true;
	}

	@Override
	public int weight() {
		return 10;
	}

	@Override
	public int cost() {
		return 10;
	}

	@Override
	public boolean isSilent() {
		return false;
	}

}
