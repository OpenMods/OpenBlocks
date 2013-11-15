package openblocks.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.OpenTileEntity;

import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityMessageEventPacket extends EventPacket {
	
	private boolean toServer;
	private int dimensionID;
	private int xCoord;
	private int yCoord;
	private int zCoord;
	public NBTBase data = null;

	public TileEntityMessageEventPacket() {
	}
	
	public TileEntityMessageEventPacket(OpenTileEntity tile) {
		xCoord = tile.xCoord;
		yCoord = tile.yCoord;
		zCoord = tile.zCoord;
		toServer = tile.worldObj.isRemote;
		dimensionID = tile.worldObj.provider.dimensionId;
	}
	
	@Override
	public EventType getType() {
		return EventType.TILE_MESSAGE;
	}

	@Override
	public void readFromStream(DataInput input) throws IOException {
		toServer = input.readByte() == 1;
		dimensionID = input.readInt();
		xCoord = input.readInt();
		yCoord = input.readInt();
		zCoord = input.readInt();
		if(input.readBoolean()) {
			data = NBTBase.readNamedTag(input);
		}
	}

	@Override
	public void writeToStream(DataOutput output) throws IOException {
		output.writeByte(toServer ? 1 : 0);
		output.writeInt(dimensionID);
		output.writeInt(xCoord);
		output.writeInt(yCoord);
		output.writeInt(zCoord);
		if(data != null) {
			output.writeBoolean(true);
			NBTBase.writeNamedTag(data, output);
		}else{
			output.writeBoolean(false);
		}
	}
	
	public void setData(NBTBase data) {
		this.data = data;
	}
	
	public OpenTileEntity getTileEntity() {
		World world = null;
		if (toServer) {
			world = OpenBlocks.proxy.getServerWorld(dimensionID);
		} else {
			world = OpenBlocks.proxy.getClientWorld();
			if (world.provider.dimensionId != dimensionID) {
				return null;
			}
		}
		if (world != null) {
			TileEntity te = world.getBlockTileEntity(xCoord, yCoord, zCoord);
			if (te instanceof OpenTileEntity) {
				return (OpenTileEntity) te;
			}
		}
		return null;
	}

}
