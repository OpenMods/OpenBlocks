package openperipheral.api.converter;

/**
 * Base class for objects that aggregate type converters.
 *
 * @see IConverter
 * @see IConverterManager
 *
 */
public interface ITypeConverterRegistry {

	public void register(IInboundTypeConverter converter);

	public void register(IGenericInboundTypeConverter converter);

	public void register(IOutboundTypeConverter converter);

	public void register(IGenericTypeConverter converter);

	public void register(ITypeConverter converter);

	/**
	 * Register type that should be left unconverted and passed to Lua
	 */
	public void registerIgnored(Class<?> ignored, boolean includeSubclasses);

}
