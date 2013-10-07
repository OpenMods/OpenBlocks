package openblocks.common.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.common.tileentity.TileEntityVacuumHopper;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;

public class ContainerVacuumHopper extends ContainerInventory<TileEntityVacuumHopper> {

	public enum Keys {
		test
	}
	
	public SyncableInt test = new SyncableInt();
	
	public ContainerVacuumHopper(IInventory playerInventory, TileEntityVacuumHopper hopper) {
		super(playerInventory, hopper);
		addInventoryGrid(44, 20, 5);
		addPlayerInventorySlots(69);
		addSyncedObject(Keys.test, test);
	}
	
	public SyncableInt getTest() {
		return test;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		System.out.println("Synced");
	}

	@Override
	public void onServerButtonClicked(EntityPlayer player, int buttonId) {
		System.out.println("Server:"+buttonId);
	}

	@Override
	public void onClientButtonClicked(int buttonId) {
		System.out.println("Client:"+buttonId);
		
	}

}
