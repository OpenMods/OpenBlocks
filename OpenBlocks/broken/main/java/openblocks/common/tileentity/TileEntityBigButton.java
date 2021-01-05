package openblocks.common.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import openblocks.client.gui.GuiBigButton;
import openblocks.common.container.ContainerBigButton;
import openmods.api.ISurfaceAttachment;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryDelegate;
import openmods.inventory.TileEntityInventory;
import openmods.tileentity.OpenTileEntity;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityBigButton extends OpenTileEntity implements ISurfaceAttachment, IHasGui, IInventoryDelegate {

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "bigbutton", true, 8));

	@Override
	public Object getServerGui(PlayerEntity player) {
		return new ContainerBigButton(player.inventory, this);
	}

	@Override
	public Object getClientGui(PlayerEntity player) {
		return new GuiBigButton(new ContainerBigButton(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(PlayerEntity player) {
		return false;
	}

	public int getTickTime() {
		int result = 0;
		for (ItemStack stack : inventory.contents())
			result += stack.getCount();
		return Math.max(result, 1);
	}

	@Override
	public Direction getSurfaceDirection() {
		return getBack();
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

	@Override
	public IInventory getInventory() {
		return inventory;
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
