package openblocks.enchantments.flimflams;

import java.util.Collections;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import openblocks.api.IFlimFlamAction;

public class InventoryShuffleFlimFlam implements IFlimFlamAction {

	@Override
	public boolean execute(ServerPlayerEntity target) {
		if (target.openContainer != null && !(target.openContainer instanceof PlayerContainer)) return false;
		Collections.shuffle(target.inventory.mainInventory);

		return true;
	}

}
