package openperipheral.api;

import java.util.Map;

import net.minecraft.item.ItemStack;

public interface IItemStackMetadataBuilder extends IApiInterface {
	public Map<String, Object> getItemStackMetadata(ItemStack stack);

	public void register(IItemStackMetadataProvider<?> provider);
}