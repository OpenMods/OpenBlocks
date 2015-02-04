package openperipheral.api.adapter;

import net.minecraft.world.World;

/**
 * For synchronized (non {@link Asynchronous}) peripherals OpenPeripheral needs access to world instance. Use this interface to provide this, it it's not available otherwise (for example, when target object is not TileEntity)
 */
public interface IWorldProvider {
	public World getWorld();

	public boolean isValid();
}
