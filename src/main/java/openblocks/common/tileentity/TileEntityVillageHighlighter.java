package openblocks.common.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openmods.OpenMods;
import openmods.api.INeighbourAwareTile;
import openmods.sync.SyncableBoolean;
import openmods.sync.SyncableIntArray;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityVillageHighlighter extends SyncedTileEntity implements ITickable, INeighbourAwareTile {

	public static int VALUES_PER_VILLAGE = 7;

	private SyncableIntArray villageData;

	private SyncableBoolean isEnabled;

	private boolean previousBreedStatus = false;

	public TileEntityVillageHighlighter() {}

	@Override
	protected void createSyncedFields() {
		villageData = new SyncableIntArray();
		isEnabled = new SyncableBoolean();
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
			if (OpenMods.proxy.getTicks(worldObj) % 10 == 0 && isEnabled.get()) {
				List<Integer> tmpDataList = Lists.newArrayList();
				for (Village village : worldObj.villageCollectionObj.getVillageList()) {
					if (village.isBlockPosWithinSqVillageRadius(pos)) {
						tmpDataList.add(village.getVillageRadius());
						BlockPos d = village.getCenter().subtract(pos);
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
		return isEnabled.get();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	public boolean canVillagersBreed() {
		if (worldObj.isRemote) return false;

		for (Village village : worldObj.villageCollectionObj.getVillageList()) {
			if (village.isBlockPosWithinSqVillageRadius(pos)) {
				int i = (int)(village.getNumVillageDoors() * 0.35D);
				if (village.getNumVillagers() < i) { return true; }
			}
		}
		return false;
	}

	public int getSignalStrength() {
		return canVillagersBreed()? 15 : 0;
	}

	@Override
	public void onNeighbourChanged(Block block) {
		isEnabled.set(worldObj.isBlockIndirectlyGettingPowered(pos) > 0);
		trySync();
	}

}
