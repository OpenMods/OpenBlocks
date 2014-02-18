package openblocks.events;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import openblocks.common.Stencil;
import openmods.network.IEventPacketType;
import openmods.network.events.TileEntityMessageEventPacket;
import openmods.tileentity.OpenTileEntity;
import openmods.utils.ByteUtils;

public class WallpaperEvent extends TileEntityMessageEventPacket {

	private int[] colors;
	
	public WallpaperEvent() {}
	
	public WallpaperEvent(OpenTileEntity te, int[] colors) {
		super(te);
		this.colors = colors;
	}
	
	@Override
	protected void readPayload(DataInput input) {
		colors = new int[256];
		try {
			for (int i = 0; i < 256; i++) {
				colors[i] = input.readInt();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void writePayload(DataOutput output) {
		try {
			for (int i = 0; i < 256; i++) {
				output.writeInt(colors[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int[] getColors() {
		return colors;
	}
	
	@Override
	public IEventPacketType getType() {
		return EventTypes.WALLPAPER;
	}
}
