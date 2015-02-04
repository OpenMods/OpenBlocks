package openperipheral.api.converter;

import java.lang.reflect.Type;

public interface IConverter {

	public void register(IGenericTypeConverter converter);

	public void register(ITypeConverter converter);

	/**
	 * Register type that should be left unconverted and passed to Lua
	 */
	public void registerIgnored(Class<?> ignored, boolean includeSubclasses);

	public Object fromLua(Object obj, Type expected);

	public Object toLua(Object obj);

}
