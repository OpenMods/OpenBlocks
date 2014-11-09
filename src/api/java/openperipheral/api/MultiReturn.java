package openperipheral.api;

public class MultiReturn {
	public static IMultiReturn wrap(final Object... args) {
		return new IMultiReturn() {
			@Override
			public Object[] getObjects() {
				return args;
			}
		};
	}
}
