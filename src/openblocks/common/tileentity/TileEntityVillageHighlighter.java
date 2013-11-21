package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.village.Village;
import openblocks.Config;
import openblocks.OpenBlocks;
import openmods.common.tileentity.SyncedTileEntity;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableIntArray;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityVillageHighlighter extends SyncedTileEntity {

	public static int VALUES_PER_VILLAGE = 7;

	public SyncableIntArray villageData;

	private boolean previousBreedStatus = false;

	public TileEntityVillageHighlighter() {}

	@Override
	protected void createSyncedFields() {
		villageData = new SyncableIntArray();
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (OpenBlocks.proxy.getTicks(worldObj) % 10 == 0) {
				ArrayList<Integer> tmpDataList = new ArrayList<Integer>();
				for (Village village : (List<Village>)worldObj.villageCollectionObj.getVillageList()) {
					if (village.isInRange(xCoord, yCoord, zCoord)) {
						tmpDataList.add(village.getVillageRadius());
						tmpDataList.add(village.getCenter().posX - xCoord);
						tmpDataList.add(village.getCenter().posY - yCoord);
						tmpDataList.add(village.getCenter().posZ - zCoord);
						tmpDataList.add(village.getNumVillageDoors());
						tmpDataList.add(village.getNumVillagers());
						tmpDataList.add(System.identityHashCode(village));
					}
				}
				villageData.setValue(convertIntegers(tmpDataList));
				sync();
				boolean canBreed = canVillagersBreed();
				if (previousBreedStatus != canBreed) {
					worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, Config.blockVillageHighlighterId);
					previousBreedStatus = canBreed;
				}
			}
		}
	}

	public SyncableIntArray getVillageData() {
		return villageData;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().expand(200, 200, 200);
	}

	public boolean isPowered() {
		if (worldObj == null) return false;
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@SuppressWarnings("unchecked")
	public boolean canVillagersBreed() {
		for (Village village : (List<Village>)worldObj.villageCollectionObj.getVillageList()) {
			if (village.isInRange(xCoord, yCoord, zCoord)) {
				int i = (int)(village.getNumVillageDoors() * 0.35D);
				if (village.getNumVillagers() < i) { return true; }
			}
		}
		return false;
	}

	public int getSignalStrength() {
		return canVillagersBreed()? 15 : 0;
	}

}
