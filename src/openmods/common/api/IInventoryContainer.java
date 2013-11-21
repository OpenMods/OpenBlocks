package openmods.common.api;

import net.minecraft.inventory.IInventory;

public interface IInventoryContainer {
	public IInventory[] getInternalInventories();
}
