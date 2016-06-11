package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import openmods.events.network.BlockEventPacket;
import openmods.movement.PlayerMovementEvent;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEventMeta;
import openmods.utils.ByteUtils;

@NetworkEventMeta(direction = EventDirection.C2S)
public class ElevatorActionEvent extends BlockEventPacket {

	public ElevatorActionEvent() {}

	public ElevatorActionEvent(int dimension, int xCoord, int yCoord, int zCoord, PlayerMovementEvent.Type type) {
		super(dimension, xCoord, yCoord, zCoord);
		this.type = type;
	}

	public PlayerMovementEvent.Type type;

	@Override
	protected void readFromStream(DataInput input) throws IOException {
		super.readFromStream(input);
		int typeId = ByteUtils.readVLI(input);
		type = PlayerMovementEvent.Type.VALUES[typeId];
	}

	@Override
	protected void writeToStream(DataOutput output) throws IOException {
		super.writeToStream(output);
		ByteUtils.writeVLI(output, type.ordinal());
	}
}
