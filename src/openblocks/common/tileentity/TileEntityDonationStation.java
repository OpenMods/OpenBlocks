package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import openblocks.Mods;
import openblocks.client.gui.GuiDonationStation;
import openblocks.common.DonationUrlManager;
import openblocks.common.GenericInventory;
import openblocks.common.container.ContainerDonationStation;
import openmods.common.api.IActivateAwareTile;
import openmods.common.api.IHasGui;
import openmods.common.api.IInventoryCallback;
import openmods.common.tileentity.OpenTileEntity;
import openmods.sync.SyncableString;

import com.google.common.base.Joiner;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;

public class TileEntityDonationStation extends OpenTileEntity implements
		IInventory, IActivateAwareTile, IHasGui, IInventoryCallback {

	private SyncableString modName = new SyncableString();
	private SyncableString authors = new SyncableString();
	private String donateUrl;

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

	public String getDonateUrl() {
		return donateUrl;
	}

	public SyncableString getAuthors() {
		return authors;
	}

	private void findModNameForInventoryItem() {
		ItemStack stack = inventory.getStackInSlot(Slots.input);
		modName.setValue("Love an item?");
		authors.clear();
		donateUrl = null;
		if (stack != null) {
			modName.setValue("Vanilla / Unknown");
			ModContainer container = Mods.getModForItemStack(stack);
			if (container != null) {
				ModMetadata meta = container.getMetadata();
				if (meta != null && meta.authorList != null) {
					authors.setValue(Joiner.on(", ").join(meta.authorList));
				}
				donateUrl = DonationUrlManager.instance().getUrl(container.getModId());
				modName.setValue(container.getName());
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

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inventory.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inventory.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return inventory.getStackInSlotOnClosing(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getInvName() {
		return inventory.getInvName();
	}

	@Override
	public boolean isInvNameLocalized() {
		return inventory.isInvNameLocalized();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return inventory.isUseableByPlayer(entityplayer);
	}

	@Override
	public void openChest() {}

	@Override
	public void closeChest() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	public void showSomeLove() {
		// TODO: Impl.
	}
}
