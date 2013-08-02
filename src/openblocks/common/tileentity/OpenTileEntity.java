package openblocks.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

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
}
