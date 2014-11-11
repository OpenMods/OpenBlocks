package openperipheral.api;

public interface IMetaProvider<C> {
	public Class<? extends C> getTargetClass();

	public String getKey();
}
