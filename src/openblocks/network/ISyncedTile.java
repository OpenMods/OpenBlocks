package openblocks.network;

import java.util.List;

public interface ISyncedTile {
	
	public SyncMap getSyncMap();
	public void onSynced(List<ISyncableObject> changes);

}
