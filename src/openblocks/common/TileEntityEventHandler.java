package openblocks.common;

import net.minecraftforge.event.ForgeSubscribe;
import openmods.Log;
import openmods.network.events.TileEntityMessageEventPacket;
import openmods.tileentity.OpenTileEntity;

public class TileEntityEventHandler {

	@ForgeSubscribe
	public void onTileEntityEvent(TileEntityMessageEventPacket event) {
		OpenTileEntity tile = event.getTileEntity();
		if (tile != null) {
			tile.onEvent(event);
		} else {
			Log.warn("Received packet for invalid te @ (%d,%d,%d)", event.xCoord, event.yCoord, event.zCoord);
		}
	}
}
