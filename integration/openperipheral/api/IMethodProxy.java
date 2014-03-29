package openperipheral.api;

/**
 * Type of argument provided when using {@link ProxyArg} or {@link ProxyArgs}
 */
public interface IMethodProxy {
	public <T> T call(Object... args);
}
