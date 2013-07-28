package openblocks.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTank extends TileEntity {

	private TileEntityValve valve;
	private int valveX;
	private int valveY;
	private int valveZ;
	
	public void setValve(TileEntityValve valve) {
		this.valve = valve;
	}
	
	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (valve == null) {
				TileEntity te = worldObj.getBlockTileEntity(
						valveX,
						valveY,
						valveZ
				);
				if (te != null && te instanceof TileEntityValve) {
					valve = (TileEntityValve) te;
				}
				if (valve == null) {
					worldObj.setBlockToAir(xCoord, yCoord, zCoord);
				}
			}
		}
	}

	public void notifyTank() {
		if (valve == null) {
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			return;
		}
		this.valve.markForRecheck();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (valve != null) {
			tag.setInteger("valvex", valve.xCoord);
			tag.setInteger("valvey", valve.yCoord);
			tag.setInteger("valvez", valve.zCoord);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("valvex") && tag.hasKey("valvey") && tag.hasKey("valvez")) {
			valveX = tag.getInteger("valvex");
			valveY = tag.getInteger("valvey");
			valveZ = tag.getInteger("valvez");
		}
	}

}
