package openblocks.common.tileentity;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockVillageHighlighter;
import openmods.OpenMods;
import openmods.sync.SyncableIntArray;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityVillageHighlighter extends SyncedTileEntity implements ITickable {

	public static final int VALUES_PER_VILLAGE = 7;

	private SyncableIntArray villageData;

	private boolean previousBreedStatus = false;

	public TileEntityVillageHighlighter() {}

	@Override
	protected void createSyncedFields() {
		villageData = new SyncableIntArray();
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			if (OpenMods.proxy.getTicks(world) % 10 == 0 && isEnabled()) {
				List<Integer> tmpDataList = Lists.newArrayList();
				for (Village village : world.villageCollection.getVillageList()) {
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
				villageData.setValue(Ints.toArray(tmpDataList));
				sync();
				boolean canBreed = canVillagersBreed();
				if (previousBreedStatus != canBreed) {
					world.notifyNeighborsOfStateChange(pos, OpenBlocks.Blocks.villageHighlighter, false);
					previousBreedStatus = canBreed;
				}
			}
		}
	}

	private boolean isEnabled() {
		final BlockState state = world.getBlockState(pos);
		return state.getBlock() instanceof BlockVillageHighlighter && state.getValue(BlockVillageHighlighter.POWERED);
	}

	public SyncableIntArray getVillageData() {
		return villageData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().grow(200);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	public boolean canVillagersBreed() {
		if (world.isRemote) return false;

		for (Village village : world.villageCollection.getVillageList()) {
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

}
