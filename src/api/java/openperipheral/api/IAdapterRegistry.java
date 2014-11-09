package openperipheral.api;

public interface IAdapterRegistry extends IApiInterface {
	public void register(IPeripheralAdapter adapter);

	public void register(IObjectAdapter adapter);

	public void registerInline(Class<?> cls);
}
