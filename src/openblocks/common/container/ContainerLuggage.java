package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.entity.EntityLuggage;
import openmods.container.ContainerInventoryProvider;

public class ContainerLuggage extends ContainerInventoryProvider<EntityLuggage> {

	public ContainerLuggage(IInventory playerInventory, EntityLuggage luggage) {
		super(playerInventory, luggage);
		addInventoryGrid(8, 18, 9);
		addPlayerInventorySlots(luggage.isSpecial()? 139 : 85);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer) && !getOwner().isDead;
	}
}
