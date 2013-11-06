package openblocks.common.tileentity;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import openblocks.client.gui.GuiDonationStation;
import openblocks.common.GenericInventory;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.api.IHasGui;
import openblocks.common.api.IInventoryCallback;
import openblocks.common.container.ContainerDonationStation;
import openblocks.sync.SyncableString;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TileEntityDonationStation extends OpenTileEntity implements IInventory, IActivateAwareTile, IHasGui, IInventoryCallback {
	
	private SyncableString modName = new SyncableString();
	
	public enum Slots {
		input
	}
	
	public TileEntityDonationStation() {
		setInventory(new GenericInventory("donationstation", true, 1));
		addInventoryCallback(this);
	}

	@Override
	public void initialize() {
		findModNameForInventoryItem();
	}
	
	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {
		findModNameForInventoryItem();
	}
	
	private void findModNameForInventoryItem() {
		ItemStack stack = inventory.getStackInSlot(Slots.input);
		modName.clear();
		if (stack != null) {
			UniqueIdentifier ident = GameRegistry.findUniqueIdentifierFor(stack.getItem());
			if (ident != null) {
				modName.setValue(ident.modId);
			}
		}
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) { return false; }
		if (!worldObj.isRemote) {
			openGui(player);
		}
		return true;
	}

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerDonationStation(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiDonationStation(new ContainerDonationStation(player.inventory, this));
	}

	public SyncableString getModName() {
		return modName;
	}

}
