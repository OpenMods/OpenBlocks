package openblocks.utils;

public class HackyUtils {
	public static boolean isCalledFromClass(Class<?> klazz) {
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			if (element.getClassName().equals(klazz.getName())) {
				return true;
			}
		}
		return false;
	}
}
