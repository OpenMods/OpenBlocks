package openblocks.common.tileentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidHandler;

import openblocks.OpenBlocks;
import openblocks.common.api.IAwareTile;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableFlags;

public abstract class BaseTileEntityXPMachine extends NetworkedTileEntity implements IAwareTile {

	// the tank used to store the XP
	protected FluidTank tank;
	
	// an xp fluid stack
	protected FluidStack xpFluid = new FluidStack(OpenBlocks.Fluids.openBlocksXPJuice, 1);
	
	// the directions of tanks surrounding this TE
	protected List<ForgeDirection> surroundingTanks = new ArrayList<ForgeDirection>();

	public BaseTileEntityXPMachine() {
		
	}

	@Override
	public void initialize() {
		if (!worldObj.isRemote) {
			refreshSurroundingTanks();
		}
	}
	
	@Override
	public void onNeighbourChanged(int blockId) {
		if (!worldObj.isRemote) {
			refreshSurroundingTanks();
		}
	}
	
	public abstract SyncableFlags getXPSides();
	
	protected void refreshSurroundingTanks() {
		surroundingTanks = new ArrayList<ForgeDirection>();
		SyncableFlags sides = getXPSides();
		for (Integer sideOrd : sides.getActiveSlots()) {
			ForgeDirection side = ForgeDirection.getOrientation(sideOrd);
			TileEntity tile = getTileInDirection(side);
			if (tile instanceof IFluidHandler) {
				surroundingTanks.add(side);
			}
		}
	}
	
	protected void trySuckXP() {
		int tankSpace = tank.getCapacity() - tank.getFluidAmount();
		if (tankSpace > 0 && surroundingTanks.size() > 0) {
			Collections.shuffle(surroundingTanks);
			for (ForgeDirection side : surroundingTanks) {
				int space = tank.getCapacity() - tank.getFluidAmount();
				TileEntity otherTank = getTileInDirection(side);
				if (otherTank instanceof IFluidHandler) {
					IFluidHandler handler = (IFluidHandler)otherTank;
					FluidStack drainStack = xpFluid.copy();
					drainStack.amount = Math.min(80, space);
					FluidStack drained = handler.drain(side.getOpposite(), drainStack, true);
					tank.fill(drained, true);
					if (tank.getCapacity() == tank.getFluidAmount()) {
						break;
					}					
				}
			}
		}
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
	}

}
