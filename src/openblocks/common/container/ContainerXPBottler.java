package openblocks.common.container;

import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityXPBottler;
import openmods.container.ContainerInventory;

public class ContainerXPBottler extends ContainerInventory<TileEntityXPBottler> {

	public ContainerXPBottler(IInventory playerInventory, TileEntityXPBottler xpbottler) {
		super(playerInventory, xpbottler);
		addSlotToContainer(new RestrictedSlot(getOwner(), 0, 48, 30));
		addSlotToContainer(new RestrictedSlot(getOwner(), 1, 110, 30));
		addPlayerInventorySlots(69);
		if (!xpbottler.worldObj.isRemote) xpbottler.sync();
	}

}
