package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vec3i;
import net.minecraft.village.Village;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openmods.OpenMods;
import openmods.sync.SyncableIntArray;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityVillageHighlighter extends SyncedTileEntity implements ITickable {

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

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			if (OpenMods.proxy.getTicks(worldObj) % 10 == 0) {
				ArrayList<Integer> tmpDataList = new ArrayList<Integer>();
				for (Village village : worldObj.villageCollectionObj.getVillageList()) {
					if (village.func_179866_a(pos)) {
						tmpDataList.add(village.getVillageRadius());
						Vec3i d = village.getCenter().subtract(pos);
						tmpDataList.add(d.getX());
						tmpDataList.add(d.getY());
						tmpDataList.add(d.getZ());
						tmpDataList.add(village.getNumVillageDoors());
						tmpDataList.add(village.getNumVillagers());
						tmpDataList.add(System.identityHashCode(village));
					}
				}
				villageData.setValue(convertIntegers(tmpDataList));
				sync();
				boolean canBreed = canVillagersBreed();
				if (previousBreedStatus != canBreed) {
					worldObj.notifyBlockOfStateChange(pos, OpenBlocks.Blocks.villageHighlighter);
					previousBreedStatus = canBreed;
				}
			}
		}
	}

	public SyncableIntArray getVillageData() {
		return villageData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().expand(200, 200, 200);
	}

	public boolean isPowered() {
		// TODO 1.8.9 - either state or sync
		if (worldObj == null) return false;
		return worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	public boolean canVillagersBreed() {
		if (worldObj.isRemote) return false;

		for (Village village : worldObj.villageCollectionObj.getVillageList()) {
			if (village.func_179866_a(pos)) {
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
