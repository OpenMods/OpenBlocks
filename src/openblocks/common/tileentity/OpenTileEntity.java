package openblocks.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public abstract class OpenTileEntity extends TileEntity {

	private boolean initialized = false;
	private boolean isActive = false;
	private boolean isLoaded = false;

	@Override
	public void updateEntity() {
		isActive = true;
		if (!initialized) {
			initialize();
			initialized = true;
		}
	}

	public boolean isLoaded() {
		return initialized;
	}

	public boolean isAddedToWorld() {
		return worldObj != null;
	}

	protected abstract void initialize();

	protected boolean isActive() {
		return isActive;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		isLoaded = true;
	}

	@Override
	public void onChunkUnload() {
		isActive = false;
	}

	public TileEntity getTileInDirection(ForgeDirection direction) {
		int x = xCoord + direction.offsetX;
		int y = yCoord + direction.offsetY;
		int z = zCoord + direction.offsetZ;
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
}
