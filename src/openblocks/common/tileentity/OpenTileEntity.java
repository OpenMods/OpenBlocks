package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.common.GenericInventory;
import openblocks.common.api.IInventoryCallback;
import openblocks.common.block.OpenBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class OpenTileEntity extends TileEntity {

	private boolean initialized = false;
	private boolean isActive = false;

	private boolean isUsedForClientInventoryRendering = false;
	protected GenericInventory inventory;
	
	public void setInventory(GenericInventory inventory) {
		this.inventory = inventory;
	}
	
	public void addInventoryCallback(IInventoryCallback callback) {
		if (inventory != null) {
			inventory.addCallback(callback);
		}
	}
	
	public void setup() {
		
	}

	/**
	 * Get the current block rotation
	 * 
	 * @return the block rotation
	 */
	public ForgeDirection getRotation() {
		if (isUsedForClientInventoryRendering) { return getBlock().getInventoryRenderRotation(); }
		return ForgeDirection.getOrientation(getMetadata());
	}

	/**
	 * @param block
	 * @param metadata
	 */
	@SideOnly(Side.CLIENT)
	public void prepareForInventoryRender(Block block, int metadata) {
		if(this.worldObj != null) {
			System.out.println("SEVERE PROGRAMMER ERROR! Inventory Render on World TileEntity. Expect hell!");
		} // But of course, we continue, because YOLO.
		isUsedForClientInventoryRendering = true;
		this.blockType = block;
		this.blockMetadata = metadata;
	}

	@Override
	public void updateEntity() {
		isActive = true;
		if (!initialized) {
			initialize();
			initialized = true;
		}
	}
	
	protected void initialize(){}

	public boolean isLoaded() {
		return initialized;
	}

	public boolean isAddedToWorld() {
		return worldObj != null;
	}

	protected boolean isActive() {
		return isActive;
	}

	@Override
	public void onChunkUnload() {
		isActive = false;
	}

	public TileEntity getTileInDirection(ForgeDirection direction) {
		int x = xCoord + direction.offsetX;
		int y = yCoord + direction.offsetY;
		int z = zCoord + direction.offsetZ;
		/* TODO: Mikee, getBlockTileEntity returns null anyway, why the extra block check ? */
		if (worldObj != null && worldObj.blockExists(x, y, z)) { return worldObj.getBlockTileEntity(x, y, z); }
		return null;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s", xCoord, yCoord, zCoord);
	}

	public boolean isAirBlock(ForgeDirection direction) {
		return worldObj != null
				&& worldObj.isAirBlock(xCoord + direction.offsetX, yCoord
						+ direction.offsetY, zCoord + direction.offsetZ);
	}

	public void sendBlockEvent(int key, int value) {
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, worldObj.getBlockId(xCoord, yCoord, zCoord), key, value);
	}

	@Override
	public boolean shouldRefresh(int oldID, int newID, int oldMeta, int newMeta, World world, int x, int y, int z) {
		return oldID != newID;
	}

	public OpenBlock getBlock() {
		/* Hey look what I found */
		if(this.blockType instanceof OpenBlock) { /* This has broken other mods in the past, not this one! */ 
			return (OpenBlock)this.blockType;
		}
		return OpenBlock.getOpenBlock(worldObj, xCoord, yCoord, zCoord);
	}

	public int getMetadata() {
		if (blockMetadata > -1) {
			return blockMetadata;
		}
		return this.blockMetadata = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	}

	public void openGui(EntityPlayer player) {
		player.openGui(OpenBlocks.instance, -1, worldObj, xCoord, yCoord, zCoord);
	}

	public AxisAlignedBB getBB() {
		return AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}

	public boolean isRenderedInInventory() {
		return isUsedForClientInventoryRendering;
	}

	public int getSizeInventory() {
		if (inventory == null) return 0;
		return inventory.getSizeInventory();
	}

	public ItemStack getStackInSlot(int i) {
		if (inventory == null) return null;
		return inventory.getStackInSlot(i);
	}

	public ItemStack decrStackSize(int i, int j) {
		if (inventory == null) return null;
		return inventory.decrStackSize(i, j);
	}

	public ItemStack getStackInSlotOnClosing(int i) {
		if (inventory == null) return null;
		return inventory.getStackInSlotOnClosing(i);
	}

	public void setInventorySlotContents(int i, ItemStack itemstack) {
		if (inventory == null) return;
		inventory.setInventorySlotContents(i, itemstack);
	}

	public String getInvName() {
		if (inventory == null) return "";
		return inventory.getInvName();
	}

	public boolean isInvNameLocalized() {
		if (inventory == null) return false;
		return inventory.isInvNameLocalized();
	}

	public int getInventoryStackLimit() {
		if (inventory == null) return 0;
		return inventory.getInventoryStackLimit();
	}

	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (inventory == null) return false;
		return inventory.isUseableByPlayer(entityplayer);
	}

	public void openChest() {}

	public void closeChest() {}

	@SuppressWarnings("unused")
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (inventory != null) {
			inventory.writeToNBT(tag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (inventory != null) {
			inventory.readFromNBT(tag);
		}
	}
}
