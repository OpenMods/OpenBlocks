package openblocks.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.WeakHashMap;

import net.minecraft.network.packet.Packet250CustomPayload;

public class SyncableManager {
	
	private int index = 0;
	
	private WeakHashMap<SyncMap, Void> syncableMap = new WeakHashMap<SyncMap, Void>();


	public SyncMap newSyncMap(boolean isServer) {
		SyncMap map = new SyncMap();
		if (isServer) {
			map.setId(index++);
		}
		syncableMap.put(map, null);
		return map;
	}
	
	public SyncMap getByMapId(int id) {
		for (SyncMap map : syncableMap.keySet()) {
			if (map.getId() == id){
				return map;
			}
		}
		return null;
	}
	
	public void handlePacket(Packet250CustomPayload packet) throws IOException {
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.data));
		int id = dis.readInt();
		SyncMap map = getByMapId(id);
		if (map != null) {
			map.readFromStream(dis);
		}
		dis.close();
	}
}
