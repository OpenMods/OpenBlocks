package openblocks.common.tileentity;

import java.util.Set;

import openmods.api.INeighbourAwareTile;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableBoolean;
import openmods.tileentity.SyncedTileEntity;

public class TileEntitySky extends SyncedTileEntity implements INeighbourAwareTile {

	private SyncableBoolean powered;

	@Override
	protected void createSyncedFields() {
		powered = new SyncableBoolean();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	protected void initialize() {
		if (!worldObj.isRemote) updateState();
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		if (!worldObj.isRemote) updateState();
	}

	protected void updateState() {
		final boolean inverted = worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != 0;
		final boolean isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
		powered.setValue(isPowered ^ inverted);
		sync();
	}

	public boolean isPowered() {
		return powered.getValue();
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}
}
