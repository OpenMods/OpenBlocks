package openperipheral.api.converter;

import java.lang.reflect.Type;

public interface IConverter extends ITypeConverterRegistry {

	public Object toJava(Object obj, Type expected);

	public <T> T toJava(Object obj, Class<? extends T> cls);

	public Object fromJava(Object obj);

}
