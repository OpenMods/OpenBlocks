package openperipheral.api;

import net.minecraft.item.ItemStack;

public interface IItemStackMetaProvider<C> extends IMetaProvider<C> {

	public Object getMeta(C target, ItemStack stack);

}
