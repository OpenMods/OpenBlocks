package openblocks.common;

import net.minecraftforge.event.ForgeSubscribe;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.network.TileEntityMessageEventPacket;

public class TileEntityEventHandler {

	@ForgeSubscribe
	public void onTileEntityEvent(TileEntityMessageEventPacket event) {
		OpenTileEntity tile = event.getTileEntity();
		if (tile != null) {
			tile.onEvent(event);
		}
	}
}
