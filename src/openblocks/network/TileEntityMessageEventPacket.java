package openblocks.network;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTBase;

public class TileEntityMessageEventPacket extends EventPacket {

	public int dimensionID;
	public int xCoord;
	public int yCoord;
	public int zCoord;
	public int messageID;
	public NBTBase data = null;
	
	@Override
	public EventType getType() {
		return EventType.TILE_MESSAGE;
	}

	@Override
	public void readFromStream(DataInput input) throws IOException {
		dimensionID = input.readInt();
		xCoord = input.readInt();
		yCoord = input.readInt();
		zCoord = input.readInt();
		messageID = input.readInt();
		if(input.readBoolean()) {
			data = NBTBase.readNamedTag(input);
		}
	}

	@Override
	public void writeToStream(DataOutput output) throws IOException {
		output.writeInt(dimensionID);
		output.writeInt(xCoord);
		output.writeInt(yCoord);
		output.writeInt(zCoord);
		output.writeInt(messageID);
		if(data != null) {
			output.writeBoolean(true);
			NBTBase.writeNamedTag(data, output);
		}else{
			output.writeBoolean(false);
		}
	}

}
