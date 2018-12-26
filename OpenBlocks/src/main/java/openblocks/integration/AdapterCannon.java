package openblocks.integration;

import openblocks.common.tileentity.TileEntityCannon;
import openperipheral.api.adapter.IPeripheralAdapter;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.architecture.FeatureGroup;

@FeatureGroup("openblocks-cannon")
public class AdapterCannon implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityCannon.class;
	}

	@Override
	public String getSourceId() {
		return "openblocks_cannon";
	}

	@ScriptCallable(description = "Set the pitch of the cannon")
	public void setPitch(TileEntityCannon cannon, @Arg(name = "pitch", description = "Set the pitch") double pitch) {
		cannon.setPitch(pitch);
	}

	@ScriptCallable(description = "Set the yaw of the cannon")
	public void setYaw(TileEntityCannon cannon, @Arg(name = "yaw", description = "Set the yaw") double yaw) {
		cannon.setYaw(yaw);
	}

	@ScriptCallable(description = "Set the speed of the items")
	public void setSpeed(TileEntityCannon cannon, @Arg(name = "speed", description = "Set the speed") double speed) {
		cannon.setSpeed(speed);
	}
}
