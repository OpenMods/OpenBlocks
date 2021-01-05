package openblocks.common.tileentity;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.items.CapabilityItemHandler;
import openblocks.client.gui.GuiDonationStation;
import openblocks.common.DonationUrlManager;
import openblocks.common.container.ContainerDonationStation;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryDelegate;
import openmods.inventory.TileEntityInventory;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ModIdentifier;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityDonationStation extends OpenTileEntity implements IHasGui, IInventoryDelegate {

	public enum Slots {
		input
	}

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "donationstation", true, 1));

	public TileEntityDonationStation() {}

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerDonationStation(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiDonationStation(new ContainerDonationStation(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return true;
	}

	@Override
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);
		return tag;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	private ModContainer identifyDonationItem() {
		ItemStack stack = inventory.getStackInSlot(Slots.input);
		if (stack.isEmpty()) return null;

		return ModIdentifier.INSTANCE.getModItemStack(stack);
	}

	public boolean hasItem() {
		return !inventory.getStackInSlot(Slots.input).isEmpty();
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

	@Override
	public boolean hasCapability(Capability<?> capability, Direction facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory.getHandler();

		return super.getCapability(capability, facing);
	}
}
