package openblocks.common.tileentity.tank;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.ITankContainer;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import openblocks.api.IAwareTile;
import openblocks.common.tileentity.OpenTileEntity;
import openblocks.sync.ISyncHandler;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncMap;

public class TileEntityTankValve extends TileEntityTankBase implements ITankContainer {

	private LiquidTank fakeTank = new LiquidTank(0);
	
	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill) {
		return fill(0, resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill) {
		TileEntityTank tank = getTankInDirection(ForgeDirection.DOWN);
		int filled = 0;
		if (tank != null) {
			filled = tank.fill(resource, doFill, null);
		}
		return filled;
	}

	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction) {
		ILiquidTank tank = getTank(direction, null);
		if (tank != null) {
			return new ILiquidTank[] { fakeTank };
		}
		return new ILiquidTank[] { fakeTank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type) {
		TileEntityTank[] tanks = getSurroundingTanks();
		if (tanks.length > 0) {
			return tanks[0].getInternalTank();
		}
		return fakeTank;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		// TODO Auto-generated method stub
		
	}


}
