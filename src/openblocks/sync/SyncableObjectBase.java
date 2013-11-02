package openblocks.sync;

import openblocks.OpenBlocks;
import net.minecraft.world.World;

public abstract class SyncableObjectBase implements ISyncableObject {
	
	protected long lastChangeTime = 0;
	protected boolean dirty = false;

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void markClean() {
		dirty = false;
	}

	@Override
	public void markDirty() {
		dirty = true;
	}

	@Override
	public void resetChangeTimer(World world) {
		lastChangeTime = OpenBlocks.proxy.getTicks(world);
	}
	
	@Override
	public int getTicksSinceChange(World world) {
		return (int)(OpenBlocks.proxy.getTicks(world) - lastChangeTime);
	}
}
