package openperipheral.api.converter;

import openperipheral.api.Constants;
import openperipheral.api.IApiInterface;

/**
 * Used for getting architecture specific {@link IConverter} and registering global {@link ITypeConverter}s
 *
 * @see Constants
 *
 */
public interface IConverterManager extends IApiInterface {

	public void register(IGenericTypeConverter converter);

	public void register(ITypeConverter converter);

	/**
	 * Register type that should be left unconverted and passed to Lua
	 */
	public void registerIgnored(Class<?> ignored, boolean includeSubclasses);

	/**
	 * Get converted for specific architecture
	 */
	public IConverter getConverter(String architecture);
}
