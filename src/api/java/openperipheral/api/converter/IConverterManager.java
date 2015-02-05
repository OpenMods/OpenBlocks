package openperipheral.api.converter;

import openperipheral.api.Constants;
import openperipheral.api.IApiInterface;

/**
 * Used for getting architecture specific {@link IConverter} and registering global {@link ITypeConverter}s
 *
 * @see Constants
 *
 */
public interface IConverterManager extends IApiInterface, ITypeConverterRegistry {

	/**
	 * Get converted for specific architecture
	 */
	public IConverter getConverter(String architecture);
}
