package openperipheral.api;

import net.minecraft.item.ItemStack;

public interface IItemStackMetadataProvider<C> extends IMetaProvider<C> {

	public Object getMeta(C target, ItemStack stack);

}
