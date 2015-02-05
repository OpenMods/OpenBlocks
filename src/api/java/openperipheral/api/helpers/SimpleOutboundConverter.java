package openperipheral.api.helpers;

import openperipheral.api.converter.IConverter;
import openperipheral.api.converter.IOutboundTypeConverter;

import com.google.common.reflect.TypeToken;

public abstract class SimpleOutboundConverter<T> implements IOutboundTypeConverter {
	@SuppressWarnings("serial")
	private final Class<? super T> type = (new TypeToken<T>(getClass()) {}).getRawType();

	@Override
	@SuppressWarnings("unchecked")
	public Object fromJava(IConverter converter, Object obj) {
		return type.isInstance(obj)? convert(converter, (T)obj) : null;
	}

	public abstract Object convert(IConverter converter, T value);

}
