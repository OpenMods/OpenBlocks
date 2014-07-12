package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;

import openblocks.common.Stencil;
import openmods.events.network.TileEntityMessageEventPacket;
import openmods.network.event.EventDirection;
import openmods.network.event.NetworkEventMeta;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ByteUtils;

@NetworkEventMeta(direction = EventDirection.C2S)
public class StencilCraftEvent extends TileEntityMessageEventPacket {
	private Stencil stencil;

	public StencilCraftEvent() {}

	public StencilCraftEvent(OpenTileEntity te, Stencil stencil) {
		super(te);
		this.stencil = stencil;
	}

	@Override
	protected void readPayload(DataInput input) {
		int stencilId = ByteUtils.readVLI(input);
		stencil = Stencil.values()[stencilId];
	}

	@Override
	protected void writePayload(DataOutput output) {
		ByteUtils.writeVLI(output, stencil.ordinal());
	}

	public Stencil getStencil() {
		return stencil;
	}
}
