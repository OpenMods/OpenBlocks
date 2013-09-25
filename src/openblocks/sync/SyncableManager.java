package openblocks.sync;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SyncableManager {

	public static final byte TYPE_TILE = 0;
	public static final byte TYPE_ENTITY = 1;

	public void handlePacket(Packet250CustomPayload packet) throws IOException {

		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
				packet.data));

		byte type = dis.readByte();

		World world = net.minecraft.client.Minecraft.getMinecraft().theWorld;

		ISyncHandler handler = null;

		if (type == TYPE_TILE) {
			int x = dis.readInt();
			int y = dis.readInt();
			int z = dis.readInt();
			if (world != null) {
				if (world.blockExists(x, y, z)) {
					TileEntity tile = world.getBlockTileEntity(x, y, z);
					if (tile instanceof ISyncHandler) {
						handler = (ISyncHandler) tile;
					}
				}
			}
		} else if (type == TYPE_ENTITY) {
			int entityId = dis.readInt();
			Entity entity = world.getEntityByID(entityId);
			if (entity != null && entity instanceof ISyncHandler) {
				handler = (ISyncHandler) entity;
			}
		}
		if (handler != null) {
			List<ISyncableObject> changes = handler.getSyncMap()
					.readFromStream(dis);
			handler.onSynced(changes);
		}
		dis.close();
	}
}
