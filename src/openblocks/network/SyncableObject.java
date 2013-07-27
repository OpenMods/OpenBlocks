package openblocks.network;

import java.util.ArrayList;

public abstract class SyncableObject implements ISyncableObject {

	protected Object value;
	protected boolean hasChanged = false;
	
	private ArrayList<IChangeListener> listeners = new ArrayList<IChangeListener>();
	
	public SyncableObject(Object value) {
		this.value = value;
	}
	
	public void addChangeListener(IChangeListener listener) {
		listeners.add(listener);
	}
	
	public void setValue(Object value) {
		if (this.value != value) {
			hasChanged = true;
			this.value = value;
		}
	}
	
	public void notifyListeners() {
		for (IChangeListener listener : listeners) {
			listener.onChanged(this);
		}
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
