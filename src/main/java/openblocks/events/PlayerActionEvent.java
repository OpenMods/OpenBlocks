package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import openmods.network.event.EventPacket;
import openmods.network.event.IEventPacketType;
import openmods.utils.ByteUtils;

public class PlayerActionEvent extends EventPacket {
	public static final IEventPacketType EVENT_TYPE = EventTypes.PLAYER_ACTION;

	public enum Type {
		BOO
	}

	public Type type;

	public PlayerActionEvent() {}

	public PlayerActionEvent(Type type) {
		this.type = type;
	}

	@Override
	protected void readFromStream(DataInput input) throws IOException {
		int typeId = ByteUtils.readVLI(input);
		type = Type.values()[typeId];
	}

	@Override
	protected void writeToStream(DataOutput output) throws IOException {
		ByteUtils.writeVLI(output, type.ordinal());
	}

}
