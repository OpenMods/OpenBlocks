package openperipheral.api;

/**
 * @deprecated Use methods from {@link OpenPeripheralAPI}
 */
@Deprecated
public class IntegrationRegistry {

	/**
	 * @deprecated Use static version
	 *             {@link OpenPeripheralAPI#register(IPeripheralAdapter)}
	 */
	@Deprecated
	public boolean registerAdapter(IPeripheralAdapter adapter) {
		return OpenPeripheralAPI.register(adapter);
	}

	/**
	 * @deprecated Use static version
	 *             {@link OpenPeripheralAPI#register(ITypeConverter)}
	 */
	@Deprecated
	public boolean registerTypeConverter(ITypeConverter converter) {
		return OpenPeripheralAPI.register(converter);
	}
}
