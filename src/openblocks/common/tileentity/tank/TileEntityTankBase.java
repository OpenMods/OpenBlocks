package openblocks.common.tileentity.tank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import openblocks.api.IAwareTile;
import openblocks.common.tileentity.TileEntityHealBlock;
import openblocks.common.tileentity.TileEntityMultiblock;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;
import openblocks.sync.SyncMapEntity;
import openblocks.sync.SyncMapTile;
import openblocks.sync.SyncableDouble;
import openblocks.sync.SyncableIntArray;


public abstract class TileEntityTankBase extends TileEntityMultiblock implements ISyncHandler {

	public enum ClientSyncKeys {
		childCoords
	}
	
	protected int value = 0;
	
	protected SyncableIntArray childCoords = new SyncableIntArray();
	
	SyncMapTile syncMap = new SyncMapTile();
	
	public TileEntityTankBase() {
		syncMap.put(ClientSyncKeys.childCoords, childCoords);
	}
	
	public void updateEntity() {
		super.updateEntity();
		syncMap.sync(worldObj, this, (double)xCoord + 0.5, (double)yCoord + 0.5, (double)zCoord + 0.5);
	}
	
	protected void onChildAdded(TileEntityMultiblock tile) {
		refreshChildCoords();
	}

	@Override
	protected void onChildRemoved(TileEntityMultiblock tile) {
		refreshChildCoords();
	}
	
	private void refreshChildCoords() {
		Set<TileEntityMultiblock> children = getChildren();
		int[] childCoordArray = new int[children.size() * 3];
		int j = 0;
		for (TileEntityMultiblock child : children) {
			childCoordArray[j++] = child.xCoord;
			childCoordArray[j++] = child.yCoord;
			childCoordArray[j++] = child.zCoord;
		}
		childCoords.setValue(childCoordArray);
	}
	
	public TileEntityTankBase getOwnerTank() {
		return (TileEntityTankBase) getOwner();
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public boolean isValidNeighbour(TileEntity tile) {
		return tile instanceof TileEntityTankBase && super.isValidNeighbour(tile);
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote) { 
			getOwnerTank().value += 1;
		}
		return true;
	}
	
	@Override
	public void transferDataTo(TileEntityMultiblock ... tiles) {
		int remainder = value % tiles.length;
		int perTile = (int)Math.floor((double) value / tiles.length);
		for (TileEntityMultiblock tile : tiles) {
			((TileEntityTankBase)tile).value += perTile + remainder;
			remainder = 0;
		}
		value = 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("value", value);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("value")) {
			value = tag.getInteger("value");
		}
	}

	@Override
	public SyncMap getSyncMap() {
		return syncMap;
	}
	
	@Override
	public void onSynced(List<ISyncableObject> changes) {
		System.out.println(this + ": Coords synced = "+childCoords.size());
	}
	
	@Override
	public void writeIdentifier(DataOutputStream dos) throws IOException {
		dos.writeInt(xCoord);
		dos.writeInt(yCoord);
		dos.writeInt(zCoord);
	}
}
