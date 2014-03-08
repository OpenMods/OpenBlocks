package openperipheral.api;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public interface IIntegrationModule {

	public String getModId();

	public void init();

	public void appendEntityInfo(Map<String, Object> map, Entity entity, Vec3 relativePos);

	public void appendItemInfo(Map<String, Object> map, ItemStack itemstack);
}
