package openblocks.network;


public abstract class SyncableObject implements ISyncableObject {

	protected Object value;
	protected boolean hasChanged = false;
	private int ticksSinceChanged = 0;
	
	public SyncableObject(Object value) {
		this.value = value;
	}
	
	public void setValue(Object newValue) {
		if (!equals(newValue)) {
			setHasChanged();
			this.value = newValue;
		}
	}
	
	public int ticksSinceChanged() {
		return ticksSinceChanged;
	}
	
	public void setHasChanged() {
		hasChanged = true;
		ticksSinceChanged = 0;
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
		ticksSinceChanged++;
	}
}
