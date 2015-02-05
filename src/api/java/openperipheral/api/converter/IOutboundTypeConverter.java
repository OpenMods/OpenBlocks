package openperipheral.api.converter;

/**
 * A type converter can automatically convert objects from Java types to a types understood by script engine.
 */
public interface IOutboundTypeConverter {

	/**
	 * Convert to value understood by script enviroment.
	 * Return only types understood by underlying architecture (i.e. primitives, String, Maps), otherwise it will be converted to null values on script side.
	 * Return null to ignore value. Converter will continue to next handler.
	 *
	 * @param registry
	 *            caller of this method. May be used to recursively convert values
	 * @param obj
	 *            value to be converted
	 *
	 * @return converted value or null
	 */
	public abstract Object fromJava(IConverter converter, Object obj);

}