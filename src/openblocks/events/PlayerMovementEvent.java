package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;

import openmods.common.tileentity.OpenTileEntity;
import openmods.network.IEventPacketType;
import openmods.network.events.TileEntityMessageEventPacket;
import openmods.utils.ByteUtils;

public class PlayerMovementEvent extends TileEntityMessageEventPacket {

	public enum Type {
		JUMP,
		SNEAK
	}

	public PlayerMovementEvent() {}

	public PlayerMovementEvent(OpenTileEntity te, Type type) {
		super(te);
		this.type = type;
	}

	public Type type;

	@Override
	protected void readPayload(DataInput input) {
		int typeId = ByteUtils.readVLI(input);
		type = Type.values()[typeId];
	}

	@Override
	protected void writePayload(DataOutput output) {
		ByteUtils.writeVLI(output, type.ordinal());
	}

	@Override
	public IEventPacketType getType() {
		return EventTypes.PLAYER_MOVEMENT;
	}

}
