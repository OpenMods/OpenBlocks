package openblocks.common;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import openblocks.utils.ByteUtils;

public class HeightMapData extends WorldSavedData {

	public static class LayerData {
		public byte alpha;
		public byte[] heightMap = new byte[64 * 64];
		public byte[] colorMap = new byte[64 * 64];

		public void readFromNBT(NBTTagCompound tag) {
			alpha = tag.getByte("Alpha");
			heightMap = tag.getByteArray("Height");
			colorMap = tag.getByteArray("Color");
		}

		public void writeToNBT(NBTTagCompound tag) {
			tag.setByte("Alpha", alpha);
			tag.setByteArray("Height", heightMap);
			tag.setByteArray("Color", colorMap);
		}

		public void readFromStream(DataInput input) throws IOException {
			alpha = input.readByte();
			input.readFully(heightMap);
			input.readFully(colorMap);
		}

		public void writeToStream(DataOutput output) throws IOException {
			output.writeByte(alpha);
			output.write(heightMap);
			output.write(colorMap);
		}
	}

	public final static HeightMapData INVALID = new HeightMapData(-1, false) {
		@Override
		public boolean isValid() {
			return false;
		}
	};

	public final static HeightMapData EMPTY = new HeightMapData(-1, false) {
		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean isValid() {
			return false;
		}
	};

	public LayerData[] layers = new LayerData[0];
	public int dimension;
	public int centerX;
	public int centerZ;
	public byte scale;

	private final boolean isStub;

	public HeightMapData(String name, boolean stub) {
		super(name);
		this.isStub = stub;
	}

	public HeightMapData(int mapId, boolean stub) {
		this(getMapName(mapId), stub);
	}

	public HeightMapData(String name) {
		this(name, false);
	}

	public static String getMapName(int mapId) {
		return "height_map_" + mapId;
	}

	public boolean isValid() {
		return !isStub;
	}

	public boolean isEmpty() {
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		dimension = tag.getInteger("Dimension");

		centerX = tag.getInteger("CenterX");
		centerZ = tag.getInteger("CenterZ");

		scale = tag.getByte("Scale");

		NBTTagList layersData = tag.getTagList("Layers");
		int length = layersData.tagCount();
		layers = new LayerData[length];
		for (int i = 0; i < length; i++) {
			NBTTagCompound layerData = (NBTTagCompound)layersData.tagAt(i);
			LayerData layer = new LayerData();
			layer.readFromNBT(layerData);
			layers[i] = layer;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("Dimension", dimension);

		tag.setInteger("CenterX", centerX);
		tag.setInteger("CenterZ", centerZ);

		tag.setByte("Scale", scale);
		NBTTagList result = new NBTTagList();
		for (LayerData data : layers) {
			NBTTagCompound layerData = new NBTTagCompound();
			data.writeToNBT(layerData);
			result.appendTag(layerData);
		}
		tag.setTag("Layers", result);
	}

	public void readFromStream(DataInput input) throws IOException {
		dimension = input.readInt();
		centerX = input.readInt();
		centerZ = input.readInt();
		scale = input.readByte();
		int length = ByteUtils.readVLI(input);
		layers = new LayerData[length];
		for (int i = 0; i < length; i++) {
			LayerData layer = new LayerData();
			layer.readFromStream(input);
			layers[i] = layer;
		}
	}

	public void writeToStream(DataOutput output) throws IOException {
		output.writeInt(dimension);
		output.writeInt(centerX);
		output.writeInt(centerZ);
		output.writeByte(scale);
		ByteUtils.writeVLI(output, layers.length);
		for (LayerData data : layers)
			data.writeToStream(output);
	}
}