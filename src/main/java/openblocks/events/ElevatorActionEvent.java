package openblocks.events;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import openmods.events.network.BlockEventPacket;
import openmods.movement.PlayerMovementEvent;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEventMeta;

@NetworkEventMeta(direction = EventDirection.C2S)
public class ElevatorActionEvent extends BlockEventPacket {

	public ElevatorActionEvent() {}

	public ElevatorActionEvent(int dimension, BlockPos pos, PlayerMovementEvent.Type type) {
		super(dimension, pos);
		this.type = type;
	}

	public PlayerMovementEvent.Type type;

	@Override
	protected void readFromStream(PacketBuffer input) {
		super.readFromStream(input);
		final int typeId = input.readVarIntFromBuffer();
		type = PlayerMovementEvent.Type.VALUES[typeId];
	}

	@Override
	protected void writeToStream(PacketBuffer output) {
		super.writeToStream(output);
		output.writeVarIntToBuffer(type.ordinal());
	}
}
