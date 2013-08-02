package openblocks.wrapper;

public class IntWrapper {

	protected int value;
	
	public void setValue(int i) {
		value = i;
	}
	
	public int getValue() {
		return value;
	}
	
	public void modify(int by) {
		setValue(value + by);
	}
}
