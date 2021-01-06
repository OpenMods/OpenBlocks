package openblocks.events;

import net.minecraft.network.PacketBuffer;
import openmods.movement.PlayerMovementEvent;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;

@NetworkEventMeta(direction = EventDirection.C2S)
public class ElevatorActionEvent extends NetworkEvent {

	public ElevatorActionEvent() {}

	public ElevatorActionEvent(PlayerMovementEvent.Type type) {
		this.type = type;
	}

	public PlayerMovementEvent.Type type;

	@Override
	protected void readFromStream(PacketBuffer input) {
		type = input.readEnumValue(PlayerMovementEvent.Type.class);
	}

	@Override
	protected void writeToStream(PacketBuffer output) {
		output.writeEnumValue(type);
	}
}
