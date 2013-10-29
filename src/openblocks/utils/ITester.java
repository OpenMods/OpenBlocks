package openblocks.utils;

public interface ITester<T> {
	public enum Result {
		ACCEPT, REJECT, CONTINUTE;
	}

	Result test(T o);
}