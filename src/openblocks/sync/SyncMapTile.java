package openblocks.sync;

import java.io.DataOutputStream;
import java.io.IOException;

public class SyncMapTile extends SyncMap {

	@Override
	protected void writeMapType(DataOutputStream dos) throws IOException {
		dos.writeByte(SyncableManager.TYPE_TILE);
	}

}
