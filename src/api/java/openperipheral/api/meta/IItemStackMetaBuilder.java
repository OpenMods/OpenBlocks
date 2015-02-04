package openperipheral.api.meta;

import java.util.Map;

import net.minecraft.item.ItemStack;
import openperipheral.api.IApiInterface;

public interface IItemStackMetaBuilder extends IApiInterface {
	public Map<String, Object> getItemStackMetadata(ItemStack stack);

	public void register(IItemStackMetaProvider<?> provider);
}