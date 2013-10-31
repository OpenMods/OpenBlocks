package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.village.Village;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableIntArray;
import openblocks.utils.BlockUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityVillageHighlighter extends NetworkedTileEntity implements
		IAwareTile {

	public static int VALUES_PER_VILLAGE = 7;

	public SyncableIntArray villageData = new SyncableIntArray();

	private boolean previousBreedStatus = false;

	public TileEntityVillageHighlighter() {
		addSyncedObject(villageData);
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
				sync(false);
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
	public void onSynced(List<ISyncableObject> changes) {}

	@Override
	public void onBlockBroken() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBlockAdded() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNeighbourChanged(int blockId) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		setRotation(BlockUtils.get2dOrientation(player));
		sync();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().expand(200, 200, 200);
	}

	public boolean isPowered() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
	}

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
