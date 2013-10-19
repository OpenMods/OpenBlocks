package openblocks.sync;

import java.util.List;

public interface ISyncHandler {

	public SyncMap<?> getSyncMap();

	public void onSynced(List<ISyncableObject> changes);

}
