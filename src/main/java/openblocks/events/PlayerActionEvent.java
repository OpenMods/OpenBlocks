package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEvent;
import openmods.network.event.NetworkEventMeta;
import openmods.utils.ByteUtils;

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
	protected void readFromStream(DataInput input) {
		int typeId = ByteUtils.readVLI(input);
		type = Type.values()[typeId];
	}

	@Override
	protected void writeToStream(DataOutput output) {
		ByteUtils.writeVLI(output, type.ordinal());
	}

}
