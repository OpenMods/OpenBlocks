package openperipheral.api.meta;

/**
 *
 * Base class for meta providers (like {@link IEntityMetaProvider} and {@link IItemStackMetaProvider}).
 */
public interface IMetaProvider<C> {
	/**
	 * Used for selecting which meta providers should be called when collecting data about object.
	 */
	public Class<? extends C> getTargetClass();

	/**
	 * Name on entry in returned map. Should be unique.
	 */
	public String getKey();
}
