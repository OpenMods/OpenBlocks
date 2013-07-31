package openblocks.network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;

public interface ISyncHandler {

	public SyncMap getSyncMap();

	public void onSynced(List<ISyncableObject> changes);

	public void writeIdentifier(DataOutputStream dos) throws IOException;

}
