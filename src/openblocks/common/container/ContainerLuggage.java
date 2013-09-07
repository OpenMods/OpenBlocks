package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.entity.EntityLuggage;

public class ContainerLuggage extends ContainerInventory<IInventory> {

	public final EntityLuggage luggage;
	
	public ContainerLuggage(IInventory playerInventory, EntityLuggage luggage) {
		super(playerInventory, luggage.getInventory());
		this.luggage = luggage;
		addInventoryGrid(8, 18, 9);
		addPlayerInventorySlots(luggage.isSpecial()? 139 : 85);
	}

}
