package openblocks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.entity.EntityLuggage;
import openmods.container.ContainerInventory;

public class ContainerLuggage extends ContainerInventory<IInventory> {

	public final EntityLuggage luggage;

	public ContainerLuggage(IInventory playerInventory, EntityLuggage luggage) {
		super(playerInventory, luggage.getInventory());
		this.luggage = luggage;
		addInventoryGrid(8, 18, 9);
		addPlayerInventorySlots(luggage.isSpecial()? 139 : 85);
	}


	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return owner.isUseableByPlayer(entityplayer) && !luggage.isDead;
	}
}
