package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import openblocks.client.gui.GuiDonationStation;
import openblocks.common.DonationUrlManager;
import openblocks.common.container.ContainerDonationStation;
import openmods.api.IHasGui;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ModIdentifier;
import cpw.mods.fml.common.ModContainer;

public class TileEntityDonationStation extends OpenTileEntity implements IHasGui, IInventoryProvider {

	public enum Slots {
		input
	}

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "donationstation", true, 1));

	public TileEntityDonationStation() {}

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

	private ModContainer identifyDonationItem() {
		ItemStack stack = inventory.getStackInSlot(Slots.input);
		if (stack == null) return null;

		return ModIdentifier.INSTANCE.getModItemStack(stack);
	}

	public boolean hasItem() {
		return inventory.getStackInSlot(Slots.input) != null;
	}

	public String getDonationUrl() {
		ModContainer container = identifyDonationItem();
		return container != null? DonationUrlManager.instance().getUrl(container.getModId()) : null;
	}

	public String getModName() {
		ModContainer container = identifyDonationItem();
		return container != null? container.getName() : null;
	}

	public List<String> getModAuthors() {
		ModContainer container = identifyDonationItem();
		return container != null? container.getMetadata().authorList : null;
	}
}
