package openmods.utils;

public interface ITester<T> {
	public enum Result {
		ACCEPT,
		REJECT,
		CONTINUE;
	}

	Result test(T o);
}