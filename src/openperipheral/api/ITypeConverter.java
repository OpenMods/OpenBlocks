package openperipheral.api;

/**
 * A type converter can automatically convert objects between a Java format
 * and a Lua friendly format.
 * Basic ones are already built in (primitives, itemstacks, tanks), but you
 * can register custom ones.
 * 
 * If you don't think your converter should be handling the object passed in
 * just return null to allow other converters to attempt the conversion.
 * 
 * @author mikeef
 * 
 */
public interface ITypeConverter {

	/**
	 * Convert a lua type to the required type. tables in lua
	 * are passed in as Maps
	 * e.g.
	 * if (expected.equals(MyCustomClass.class) && o instanceof Map) {
	 * // return create new MyCustomClass from the map
	 * }
	 * return null;
	 * 
	 * @param obj
	 * @param expected
	 * @return either null if you're not handling this object, or a valid object
	 */
	public Object fromLua(Object obj, Class expected);

	/**
	 * Convert to a lua friendly format. You can pass most primitives back,
	 * but also maps. If it's not the specific class you're dealing with
	 * Just return null.
	 * e.g.
	 * if (obj instanceof MyClass) {
	 * // return ((MyClass)obj).toMap();
	 * }
	 * return null;
	 * 
	 * @param obj
	 * @return
	 */
	public Object toLua(Object obj);
}
