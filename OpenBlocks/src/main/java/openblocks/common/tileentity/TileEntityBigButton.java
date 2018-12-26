package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import openblocks.client.gui.GuiBigButton;
import openblocks.common.container.ContainerBigButton;
import openmods.api.IHasGui;
import openmods.api.ISurfaceAttachment;
import openmods.fixers.GenericInventoryTeFixerWalker;
import openmods.fixers.RegisterFixer;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.tileentity.OpenTileEntity;

@RegisterFixer(GenericInventoryTeFixerWalker.class)
public class TileEntityBigButton extends OpenTileEntity implements ISurfaceAttachment, IHasGui, IInventoryProvider {

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "bigbutton", true, 8));

	@Override
	public Object getServerGui(EntityPlayer player) {
		return new ContainerBigButton(player.inventory, this);
	}

	@Override
	public Object getClientGui(EntityPlayer player) {
		return new GuiBigButton(new ContainerBigButton(player.inventory, this));
	}

	@Override
	public boolean canOpenGui(EntityPlayer player) {
		return false;
	}

	public int getTickTime() {
		int result = 0;
		for (ItemStack stack : inventory.contents())
			result += stack.getCount();
		return Math.max(result, 1);
	}

	@Override
	public EnumFacing getSurfaceDirection() {
		return getBack();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		inventory.writeToNBT(tag);

		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag);
	}

	@Override
	@IncludeInterface
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				super.hasCapability(capability, facing);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory.getHandler();

		return super.getCapability(capability, facing);
	}

}
