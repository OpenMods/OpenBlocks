package openblocks.enchantments.flimflams;

import java.util.Arrays;
import java.util.Collections;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.item.ItemStack;
import openblocks.api.IFlimFlamAction;

public class InventoryShuffleFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		if (target.openContainer != null && !(target.openContainer instanceof ContainerPlayer)) return false;
		final ItemStack[] mainInventory = target.inventory.mainInventory;
		Collections.shuffle(Arrays.asList(mainInventory));

		return true;
	}

}
