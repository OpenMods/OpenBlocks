package openperipheral.api.helpers;

import openperipheral.api.IMetaProvider;

import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public abstract class MetaProviderSimple<C> extends TypeToken<C> implements IMetaProvider<C> {
	@Override
	@SuppressWarnings("unchecked")
	public final Class<? extends C> getTargetClass() {
		return (Class<? extends C>)getRawType();
	}
}
