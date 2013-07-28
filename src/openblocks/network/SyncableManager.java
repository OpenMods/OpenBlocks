package openblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SyncableManager {
	
	public void handlePacket(Packet250CustomPayload packet) throws IOException {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
		int x = dis.readInt();
		int y = dis.readInt();
		int z = dis.readInt();
		World world = Minecraft.getMinecraft().theWorld;
		if (world != null) {
			if (world.blockExists(x, y, z)) {
				TileEntity tile = world.getBlockTileEntity(x, y, z);
				if (tile instanceof ISyncedTile) {
					ISyncedTile syncedTile = (ISyncedTile)tile;
					List<ISyncableObject> changes = syncedTile.getSyncMap().readFromStream(dis);
					syncedTile.onSynced(changes);
				}
			}
		}
		dis.close();
	}
}
