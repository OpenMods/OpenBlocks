package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;

import openblocks.common.Stencil;
import openmods.network.IEventPacketType;
import openmods.network.events.TileEntityMessageEventPacket;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ByteUtils;

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

	@Override
	public IEventPacketType getType() {
		return EventTypes.STENCIL_CRAFT;
	}
}
