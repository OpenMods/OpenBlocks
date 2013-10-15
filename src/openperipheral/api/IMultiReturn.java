package openperipheral.api;

/**
 * Used for returning multiple objects back to lua. Each object will
 * be individually passed through the type converters into a lua friendly
 * format
 * e.g. local x, y, z = p.getLocation()
 * 
 * @author mikeef
 * 
 */
public interface IMultiReturn {
	public Object[] getObjects();
}
