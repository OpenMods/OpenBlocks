package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;

import openmods.events.network.TileEntityMessageEventPacket;
import openmods.movement.PlayerMovementEvent;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEventMeta;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ByteUtils;

@NetworkEventMeta(direction = EventDirection.C2S)
public class ElevatorActionEvent extends TileEntityMessageEventPacket {

	public ElevatorActionEvent() {}

	public ElevatorActionEvent(OpenTileEntity te, PlayerMovementEvent.Type type) {
		super(te);
		this.type = type;
	}

	public PlayerMovementEvent.Type type;

	@Override
	protected void readPayload(DataInput input) {
		int typeId = ByteUtils.readVLI(input);
		type = PlayerMovementEvent.Type.VALUES[typeId];
	}

	@Override
	protected void writePayload(DataOutput output) {
		ByteUtils.writeVLI(output, type.ordinal());
	}
}
