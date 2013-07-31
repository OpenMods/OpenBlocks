package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import openblocks.OpenBlocks;
import openblocks.api.IAwareTile;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableInt;
import openblocks.utils.Coord;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityHealBlock extends TileEntityMultiblock implements IAwareTile {

	SyncableInt myTestObject = null;

	public TileEntityHealBlock() {
	}

	public void setTestObject(SyncableInt newObject) {
		myTestObject = (SyncableInt) replaceObject(myTestObject, newObject);
	}

	@Override
	public void updateEntity() {

		if (worldObj.isRemote)
			return;

		List<EntityPlayer> playersOnTop = (List<EntityPlayer>) worldObj
				.getEntitiesWithinAABB(
						EntityPlayer.class,
						AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord,
								zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
		if (worldObj.getTotalWorldTime() % 20 == 0) {
			for (EntityPlayer player : playersOnTop) {
				if (!player.capabilities.isCreativeMode) {
					if (player.getHealth() < player.maxHealth)
						player.heal(1);
					if (player.getFoodStats().needFood())
						player.getFoodStats().setFoodLevel(
								player.getFoodStats().getFoodLevel() + 1);
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		myTestObject = new SyncableInt(0);
		if (!myTestObject.readFromNBT(nbt, "test")) {
			myTestObject = null;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if (myTestObject != null) {
			myTestObject.writeToNBT(nbt, "test");
		}
	}

	@Override
	public void onNeighbourChanged(int blockId) {
	}


	public void onBlockBroken() {
		if (!worldObj.isRemote){ 
			if (myTestObject != null) {

				invalidate();

				Collection<HashSet<TileEntity>> branches = findBranches();

				int currentValue = (Integer) myTestObject.getValue();

				int totalTiles = 0;
				for (HashSet<TileEntity> branch : branches) {
					totalTiles += branch.size();
				}

				double valuePerBlock = (double) currentValue / (double)totalTiles;

				myTestObject.clear();

				for (HashSet<TileEntity> branch : branches) {
					SyncableInt splitValue = new SyncableInt((int)(valuePerBlock * (double)branch.size()));
					for (TileEntity tile : branch) {
						((TileEntityHealBlock)tile).setTestObject(splitValue);
					}
				}

				myTestObject.unregisterTile(this);
				myTestObject = null;
			}
		}
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side,
			float hitX, float hitY, float hitZ) {
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX,
			float hitY, float hitZ) {
		if (!worldObj.isRemote) {
			if (myTestObject != null) {
				myTestObject.modify(1);
			}
		}
		return true;
	}

	@Override
	public void onBlockAdded() {
		if (!worldObj.isRemote) {
			SyncableInt val = new SyncableInt();
			for (TileEntity tile : floodFill(null, null)) {
				((TileEntityHealBlock)tile).setTestObject(val);
			}
		}
	}
}
