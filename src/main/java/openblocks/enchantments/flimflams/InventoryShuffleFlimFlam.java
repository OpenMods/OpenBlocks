package openblocks.enchantments.flimflams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamAction;

public class InventoryShuffleFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		if (target.openContainer != null && !(target.openContainer instanceof ContainerPlayer)) return false;
		final ItemStack[] mainInventory = target.inventory.mainInventory;
		List<ItemStack> stacks = Arrays.asList(mainInventory);
		Collections.shuffle(stacks);
		target.inventory.mainInventory = stacks.toArray(mainInventory);
		return true;
	}

}
