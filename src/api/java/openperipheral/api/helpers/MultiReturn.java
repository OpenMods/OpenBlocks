package openperipheral.api.helpers;

import openperipheral.api.adapter.method.IMultiReturn;

/**
 *
 * Helper class for creating {@link IMultiReturn}.
 */
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
