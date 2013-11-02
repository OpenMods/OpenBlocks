package openblocks.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.api.IAwareTile;
import openblocks.utils.BlockUtils;
import openblocks.utils.InventoryUtils;
import openblocks.utils.OpenBlocksFakePlayer;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

public class TileEntityItemDropper extends OpenTileEntity
    implements IAwareTile, IInventory, IPipeConnection {
    static final int BUFFER_SIZE = 9;

    private boolean _redstoneSignal;
    private final GenericInventory inventory = new GenericInventory("itemDropper", false, 9);

    public void setRedstoneSignal(boolean redstoneSignal) {
        if(redstoneSignal != _redstoneSignal) {
            _redstoneSignal = redstoneSignal;
            if(_redstoneSignal && !InventoryUtils.inventoryIsEmpty(inventory)) {
                dropItem();
            }
        }
    }

    private void dropItem() {
        if(worldObj.isRemote) return;

        for(int i = 0, l = getSizeInventory(); i < l; i++) {
            ItemStack stack = getStackInSlot(i);
            if(stack == null || stack.stackSize == 0) continue;

            ItemStack dropped = stack.splitStack(1);
            if(stack.stackSize <= 0) {
                setInventorySlotContents(i, null);
            }

            OpenBlocksFakePlayer.getPlayerForWorld(worldObj).dropItemAt(dropped, xCoord, yCoord, zCoord, ForgeDirection.DOWN);

//            ItemStack newStack = OpenBlocksFakePlayer.getPlayerForWorld(worldObj)
//                    .equipWithAndRightClick(stack,
//                            Vec3.createVectorHelper(xCoord, yCoord, zCoord),
//                            Vec3.createVectorHelper(x, y - 1, z),
//                            ForgeDirection.UP,
//                            worldObj.blockExists(x, y, z) && worldObj.getBlockId(x, y, z) != 0 && !Block.blocksList[worldObj.getBlockId(x, y, z)].isBlockReplaceable(worldObj, x, y, z));
//
//            setInventorySlotContents(i, newStack.stackSize > 0 ? newStack : null);
            return;
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    /**
     * Returns the stack in slot
     */
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return inventory.decrStackSize(slot, count);
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return inventory.getStackInSlotOnClosing(slot);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        inventory.setInventorySlotContents(slot, itemstack);
    }

    /**
     * Returns the name of the inventory.
     */
    @Override
    public String getInvName() {
        return inventory.getInvName();
    }

    /**
     * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
     * language. Otherwise it will be used directly.
     */
    @Override
    public boolean isInvNameLocalized() {
        return inventory.isInvNameLocalized();
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return inventory.isUseableByPlayer(entityplayer);
    }

    @Override
    public void openChest() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeChest() {
        inventory.closeChest();
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        return inventory.isItemValidForSlot(slot, itemstack);
    }

    @Override
    public void onBlockBroken() {
        if (!worldObj.isRemote) {
            BlockUtils.dropItemStackInWorld(worldObj, xCoord, yCoord, zCoord, new ItemStack(OpenBlocks.Blocks.sprinkler));
        }
    }

    @Override
    public void onBlockAdded() {
        //To change body of implemented methods use File | Settings | File Templates.
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
    public void onNeighbourChanged(int blockId) {
        if(!worldObj.isRemote) {
            setRedstoneSignal(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord));
        }
    }

    @Override
    public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onBlockEventReceived(int eventId, int eventParam) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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
	public ConnectOverride overridePipeConnection(PipeType type,
			ForgeDirection with) {
		if(type == PipeType.ITEM && with.ordinal() == ForgeDirection.getOrientation(getMetadata()).getOpposite().ordinal())
			return ConnectOverride.CONNECT;
		return ConnectOverride.DISCONNECT;
	}
}
