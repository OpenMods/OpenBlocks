package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiBigButton;
import openblocks.common.container.ContainerBigButton;
import openmods.api.IActivateAwareTile;
import openmods.api.IHasGui;
import openmods.api.ISurfaceAttachment;
import openmods.include.IncludeInterface;
import openmods.inventory.GenericInventory;
import openmods.inventory.IInventoryProvider;
import openmods.inventory.TileEntityInventory;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableFlags;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityBigButton extends SyncedTileEntity implements IActivateAwareTile, ISurfaceAttachment, IHasGui, IInventoryProvider, ISyncListener, ITickable {

	private int tickCounter = 0;

	public enum Flags {
		active
	}

	private SyncableFlags flags;

	private final GenericInventory inventory = registerInventoryCallback(new TileEntityInventory(this, "bigbutton", true, 1));

	public TileEntityBigButton() {
		syncMap.addUpdateListener(createRenderUpdateListener());
		syncMap.addSyncListener(this);
	}

	@Override
	protected void createSyncedFields() {
		flags = SyncableFlags.create(Flags.values().length);
	}

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			if (tickCounter > 0) {
				tickCounter--;
				if (tickCounter <= 0) {
					worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.click", 0.3F, 0.5F);
					flags.off(Flags.active);
					sync();
				}
			}
		}
	}

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
		ItemStack stack = inventory.getStackInSlot(0);
		return stack == null? 1 : stack.stackSize;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			if (player.isSneaking()) {
				openGui(OpenBlocks.instance, player);
			} else {
				flags.on(Flags.active);
				tickCounter = getTickTime();
				worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.click", 0.3F, 0.6F);
				sync();
			}
		}
		return true;
	}

	@Override
	public EnumFacing getSurfaceDirection() {
		return getOrientation().north();
	}

	public boolean isButtonActive() {
		return flags.get(Flags.active);
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

	@Override
	@IncludeInterface
	public IInventory getInventory() {
		return inventory;
	}

	@Override
	public void onSync(Set<ISyncableObject> changes) {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, OpenBlocks.Blocks.bigButton);
		final ForgeDirection rot = getOrientation().north();
		worldObj.notifyBlocksOfNeighborChange(xCoord + rot.offsetX, yCoord + rot.offsetY, zCoord + rot.offsetZ, OpenBlocks.Blocks.bigButton);
	}
}
