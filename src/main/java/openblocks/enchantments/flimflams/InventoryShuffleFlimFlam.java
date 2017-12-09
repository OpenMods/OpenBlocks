package openblocks.enchantments.flimflams;

import java.util.Collections;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerPlayer;
import openblocks.api.IFlimFlamAction;

public class InventoryShuffleFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(EntityPlayerMP target) {
		if (target.openContainer != null && !(target.openContainer instanceof ContainerPlayer)) return false;
		Collections.shuffle(target.inventory.mainInventory);

		return true;
	}

}
