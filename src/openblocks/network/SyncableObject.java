package openblocks.network;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

public abstract class SyncableObject implements ISyncableObject {

	protected Object value;
	protected boolean hasChanged = false;
	
	public SyncableObject(Object value) {
		this.value = value;
	}
	
	public void setValue(Object newValue) {
		if (!equals(newValue)) {
			hasChanged = true;
			this.value = newValue;
		}
	}
	
	public boolean equals(Object otherValue) {
		return value == otherValue;
	}
	
	public Object getValue() {
		return value;
	}
	
	public boolean hasChanged() {
		return hasChanged;
	}

	public void resetChangeStatus() {
		hasChanged = false;
	}
}
