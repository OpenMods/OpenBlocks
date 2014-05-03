package openperipheral.api;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

/**
 * Interface for declaring optional modules, that will be enabled only when module defined by {@link IIntegrationModule#getModId()} is loaded.
 * Register with {@link OpenPeripheralAPI#register(IIntegrationModule)}
 */
public interface IIntegrationModule {

	/**
	 * ModId of required mod
	 */
	public String getModId();

	/**
	 * Will be called on module initialization. Place adapter registration here.
	 * If unhandled exception is thrown here, module will be disabled and not called later
	 */
	public void init();

	/**
	 * Get information about entity. Data added to map will be converted before passing to Lua
	 * If unhandled exception is thrown here, module will be disabled and not called later.
	 */
	public void appendEntityInfo(Map<String, Object> result, Entity entity, Vec3 relativePos);

	/**
	 * Get information item. Data added to map will be converted before passing to Lua
	 * If unhandled exception is thrown here, module will be disabled and not called later
	 */
	public void appendItemInfo(Map<String, Object> result, ItemStack itemstack);
}
