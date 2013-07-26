package openblocks.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityTank extends TileEntity {

	private TileEntityValve valve;
	public void setValve(TileEntityValve valve) {
		this.valve = valve;
	}

	public void notifyTank() {
		if (valve == null) {
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			return;
		}
		this.valve.markForRecheck();
	}

}
