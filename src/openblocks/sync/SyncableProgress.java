package openblocks.sync;

public class SyncableProgress extends SyncableInt {

	private int max;

	public SyncableProgress(int max) {
		this.max = max;
	}

	public double getPercent() {
		return (double)value / (double)max;
	}

	public boolean isComplete() {
		return value >= max;
	}

	public void reset() {
		setValue(0);
	}

	public void increase() {
		modify(1);
	}

}
