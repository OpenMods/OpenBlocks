package openperipheral.api.converter;

import java.lang.reflect.Type;

/**
 * A generic type converter can automatically convert objects from values returned by script engine to types needed by Java methods.
 *
 */
public interface IGenericInboundTypeConverter {

	/**
	 * Convert a script value to required Java type.
	 *
	 * @param converter
	 *            caller of this method. May be used to recursively convert values
	 * @param obj
	 *            value to convert. Limited to types available on scripting side
	 * @param expected
	 *            type needed on Java side. Return should have same type or subtype
	 *
	 * @return either null if you're not handling this object, or a valid object of type {@code expected}
	 */
	public abstract Object toJava(IConverter converter, Object obj, Type expected);

}