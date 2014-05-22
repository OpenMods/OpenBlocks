package openperipheral.api;

import net.minecraft.world.World;

/**
 * For synchronized ({@link OnTick}) peripherals OpenPeripheral needs access to world instance. Use this interface to provide this, it it's not available otherwise (for example, target object is TileEntity)
 */
public interface IWorldProvider {
	public World getWorld();

	public boolean isValid();
}
