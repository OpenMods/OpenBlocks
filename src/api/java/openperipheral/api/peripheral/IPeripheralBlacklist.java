package openperipheral.api.peripheral;

import openperipheral.api.IApiInterface;

/**
 * Blacklist for peripherals.
 * Classes on this list will be ignored by OpenPeripheral peripheral handler.
 * There are few other ways to blacklist class, see blacklisting section in main API doc.
 */
public interface IPeripheralBlacklist extends IApiInterface {

	public void addToBlacklist(Class<?> cls);

	public void addToBlacklist(String clsName);

	public boolean isBlacklisted(Class<?> cls);
}
