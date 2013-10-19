package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface ISyncableObject {
	public boolean isDirty();

	public void markClean();

	public void markDirty();

	public void readFromStream(DataInput stream) throws IOException;

	public void writeToStream(DataOutput stream, boolean fullData) throws IOException;

	public void resetChangeTimer();
}
