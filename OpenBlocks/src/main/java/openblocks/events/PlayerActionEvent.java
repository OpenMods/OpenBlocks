package openblocks.events;

import net.minecraft.network.PacketBuffer;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;

@NetworkEventMeta(direction = EventDirection.C2S)
public class PlayerActionEvent extends NetworkEvent {
	public enum Type {
		BOO
	}

	public Type type;

	public PlayerActionEvent() {}

	public PlayerActionEvent(Type type) {
		this.type = type;
	}

	@Override
	protected void readFromStream(PacketBuffer input) {
		type = input.readEnumValue(Type.class);
	}

	@Override
	protected void writeToStream(PacketBuffer output) {
		output.writeEnumValue(type);
	}

}
