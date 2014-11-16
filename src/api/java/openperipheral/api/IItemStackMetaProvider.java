package openperipheral.api;

import net.minecraft.item.ItemStack;

/**
 * This interface is used to return information about in-game entities. It can be registered in {@link IItemStackMetaBuilder#register(IItemStackMetaProvider)}.
 * Collected result (from all registered providers) can be created by calling {@link IItemStackMetaProvider#getMeta(Object, ItemStack)}
 */
public interface IItemStackMetaProvider<C> extends IMetaProvider<C> {

	public Object getMeta(C target, ItemStack stack);

}
