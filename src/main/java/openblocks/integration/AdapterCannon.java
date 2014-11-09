package openblocks.integration;

import openblocks.common.tileentity.TileEntityCannon;
import openperipheral.api.*;

public class AdapterCannon implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityCannon.class;
	}

	@LuaCallable(description = "Set the pitch of the cannon")
	public void setPitch(TileEntityCannon cannon, @Arg(name = "pitch", description = "Set the pitch", type = LuaType.NUMBER) double pitch) {
		cannon.setPitch(pitch);
	}

	@LuaCallable(description = "Set the yaw of the cannon")
	public void setYaw(TileEntityCannon cannon, @Arg(name = "yaw", description = "Set the yaw", type = LuaType.NUMBER) double yaw) {
		cannon.setYaw(yaw);
	}

	@LuaCallable(description = "Set the speed of the items")
	public void setSpeed(TileEntityCannon cannon, @Arg(name = "speed", description = "Set the speed", type = LuaType.NUMBER) double speed) {
		cannon.setSpeed(speed);
	}

}
