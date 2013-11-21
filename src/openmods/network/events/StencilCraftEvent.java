package openmods.network.events;

import java.io.DataInput;
import java.io.DataOutput;

import openblocks.common.Stencil;
import openmods.common.tileentity.OpenTileEntity;
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
	public EventType getType() {
		return EventType.STENCIL_CRAFT;
	}
}
