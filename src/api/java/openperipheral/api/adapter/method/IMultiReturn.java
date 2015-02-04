package openperipheral.api.adapter.method;

/**
 * Used for returning multiple objects. Each object will be individually passed through the type converters
 *
 * @see MultipleReturn
 *
 * @author mikeef
 *
 */
public interface IMultiReturn {
	public Object[] getObjects();
}
