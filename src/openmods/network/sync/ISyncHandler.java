package openmods.network.sync;

import java.util.Set;

public interface ISyncHandler {

	public SyncMap<?> getSyncMap();

	public void onSynced(Set<ISyncableObject> changes);

}
