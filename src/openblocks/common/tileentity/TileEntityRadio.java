package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import openblocks.client.gui.GuiRadio;
import openblocks.common.container.ContainerRadio;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.api.IHasGui;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableString;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityRadio extends SyncedTileEntity implements IHasGui, IInventoryProvider {

	private GenericInventory inventory = new GenericInventory("openblocks.radio", false, 0);

	private SyncableString url;
	private SyncableBoolean enabled;

	@Override
	protected void createSyncedFields() {
		url = new SyncableString("http://icy-e-05.sharp-stream.com/magic1152n.mp3.m3u");
		enabled = new SyncableBoolean();
	}

	public SyncableString getUrl() {
		return url;
	}

	public SyncableBoolean getEnabled() {
		return enabled;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		if (changes.size() > 0) {
			System.out.println(enabled.getValue());
			System.out.println(worldObj + " SYNCED " + url.getValue() + " " + changes.size());
		}
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerRadio(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiRadio(new ContainerRadio(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

}
