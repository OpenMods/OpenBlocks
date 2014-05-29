package openblocks.integration;

import openblocks.common.tileentity.TileEntityCannon;

public class AdapterCannon implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityCannon.class;
	}

	@LuaMethod(onTick = true, returnType = LuaType.VOID, description = "Set the pitch of the cannon", args = {
			@Arg(name = "pitch", description = "Set the pitch", type = LuaType.NUMBER)
	})
	public void setPitch(TileEntityCannon cannon, double pitch) {
		cannon.setPitch(pitch);
	}

	@LuaMethod(onTick = true, returnType = LuaType.VOID, description = "Set the yaw of the cannon", args = {
			@Arg(name = "yaw", description = "Set the yaw", type = LuaType.NUMBER)
	})
	public void setYaw(TileEntityCannon cannon, double yaw) {
		cannon.setYaw(yaw);
	}

	@LuaMethod(onTick = true, returnType = LuaType.VOID, description = "Set the speed of the items", args = {
			@Arg(name = "speed", description = "Set the speed", type = LuaType.NUMBER)
	})
	public void setSpeed(TileEntityCannon cannon, double speed) {
		cannon.setSpeed(speed);
	}

}
