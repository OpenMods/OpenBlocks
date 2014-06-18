package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.client.gui.GuiDonationStation;
import openblocks.common.DonationUrlManager;
import openblocks.common.container.ContainerDonationStation;
import openmods.GenericInventory;
import openmods.IInventoryProvider;
import openmods.Mods;
import openmods.api.IHasGui;
import openmods.api.IInventoryCallback;
import openmods.include.IExtendable;
import openmods.include.IncludeInterface;
import openmods.sync.SyncableString;
import openmods.tileentity.OpenTileEntity;

import com.google.common.base.Joiner;

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;

public class TileEntityDonationStation extends OpenTileEntity implements IHasGui, IInventoryCallback, IExtendable, IInventoryProvider {

	private SyncableString modName = new SyncableString();
	private SyncableString authors = new SyncableString();
	private String donateUrl;

	public enum Slots {
		input
	}

	private final GenericInventory inventory = new GenericInventory("donationstation", true, 1);

	public TileEntityDonationStation() {
		inventory.addCallback(this);
	}

	@Override
	public void initialize() {
		findModNameForInventoryItem();
	}

	@Override
	public void onInventoryChanged(IInventory inventory, int slotNumber) {
		findModNameForInventoryItem();
		markUpdated();
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
	public Object getServerGui(EntityPlayer player) {
		return new ContainerDonationStation(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiDonationStation(new ContainerDonationStation(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return true;
	}

	public SyncableString getModName() {
		return modName;
	}

	public void showSomeLove() {
		// TODO: Impl.
	}

	@Override
	@IncludeInterface
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		inventory.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}
}
