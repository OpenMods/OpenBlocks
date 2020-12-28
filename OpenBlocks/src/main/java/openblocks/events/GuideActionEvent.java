package openblocks.events;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import openmods.events.network.BlockEventPacket;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEventMeta;

@NetworkEventMeta(direction = EventDirection.C2S)
public class GuideActionEvent extends BlockEventPacket {

	public String command;

	public GuideActionEvent() {}

	public GuideActionEvent(final RegistryKey<World> dimension, final BlockPos pos, final String event) {
		super(dimension, pos);
		this.command = event;
	}

	@Override
	protected void readFromStream(PacketBuffer input) {
		super.readFromStream(input);
		this.command = input.readString(Short.MAX_VALUE);
	}

	@Override
	protected void writeToStream(PacketBuffer output) {
		super.writeToStream(output);
		output.writeString(this.command);
	}

}
