package openperipheral.api.helpers;

import openperipheral.api.meta.IMetaProvider;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public abstract class MetaProviderSimple<C> implements IMetaProvider<C> {
	private final TypeToken<C> type = new TypeToken<C>(getClass()) {};

	@Override
	@SuppressWarnings("unchecked")
	public final Class<? extends C> getTargetClass() {
		return (Class<? extends C>)type.getRawType();
	}
}
