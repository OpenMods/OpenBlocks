package openblocks.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import openblocks.common.entity.EntityLuggage;
import openmods.container.ContainerBase;

public class ContainerLuggage extends ContainerBase<EntityLuggage> {

	public ContainerLuggage(IInventory playerInventory, EntityLuggage luggage) {
		super(playerInventory, luggage.getChestInventory(), luggage);
		addInventoryGrid(8, 18, 9);
		addPlayerInventorySlots(luggage.isSpecial()? 139 : 85);
	}

	@Override
	public boolean canInteractWith(PlayerEntity entityplayer) {
		return inventory.isUsableByPlayer(entityplayer) && !getOwner().isDead;
	}
}
