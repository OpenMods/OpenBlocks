package openblocks.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import openmods.tileentity.OpenTileEntity;

public abstract class Packet132TileEntity extends OpenTileEntity {

	@Override
	public Packet getDescriptionPacket() {
		return writeToPacket(this);
	}

	public static Packet writeToPacket(TileEntity te) {
		NBTTagCompound data = new NBTTagCompound();
		te.writeToNBT(data);
		return new Packet132TileEntityData(te.xCoord, te.yCoord, te.zCoord, 42, data);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.data);
	}
}