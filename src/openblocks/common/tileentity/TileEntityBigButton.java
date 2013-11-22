package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.client.gui.GuiBigButton;
import openblocks.common.container.ContainerBigButton;
import openmods.common.GenericInventory;
import openmods.common.api.IActivateAwareTile;
import openmods.common.api.IHasGui;
import openmods.common.api.ISurfaceAttachment;
import openmods.common.tileentity.SyncedTileEntity;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableFlags;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBigButton extends SyncedTileEntity implements IActivateAwareTile, ISurfaceAttachment, IInventory, IHasGui {

	private int tickCounter = 0;

	public enum Flags {
		active
	}

	private SyncableFlags flags;

	public TileEntityBigButton() {
		setInventory(new GenericInventory("bigbutton", true, 1));
	}

	@Override
	protected void createSyncedFields() {
		flags = new SyncableFlags();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
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

	public int getTickTime() {
		ItemStack stack = inventory.getStackInSlot(0);
		return stack == null? 1 : stack.stackSize;
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			if (player.isSneaking()) {
				openGui(player);
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
	public ForgeDirection getSurfaceDirection() {
		return getRotation();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void prepareForInventoryRender(Block block, int metadata) {
		super.prepareForInventoryRender(block, metadata);
	}

	public void onSync() {
            worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, OpenBlocks.Blocks.bigButton.blockID);
            ForgeDirection rot = getRotation();
            worldObj.notifyBlocksOfNeighborChange(xCoord + rot.offsetX, yCoord
                            + rot.offsetY, zCoord + rot.offsetZ, OpenBlocks.Blocks.bigButton.blockID);
	}
	

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}

	public boolean isButtonActive() {
		return flags.get(Flags.active);
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
}
