package openblocks.common;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.storage.WorldSavedData;

public class HeightMapData extends WorldSavedData {

	public static class LayerData {
		public byte alpha;
		public byte[] heightMap = new byte[64 * 64];
		public byte[] colorMap = new byte[64 * 64];

		public void readFromNBT(CompoundNBT tag) {
			alpha = tag.getByte("Alpha");
			heightMap = tag.getByteArray("Height");
			colorMap = tag.getByteArray("Color");
		}

		public void writeToNBT(CompoundNBT tag) {
			tag.setByte("Alpha", alpha);
			tag.setByteArray("Height", heightMap);
			tag.setByteArray("Color", colorMap);
		}

		public void readFromStream(PacketBuffer input) {
			alpha = input.readByte();
			input.readBytes(heightMap);
			input.readBytes(colorMap);
		}

		public void writeToStream(PacketBuffer output) {
			output.writeByte(alpha);
			output.writeBytes(heightMap);
			output.writeBytes(colorMap);
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
	public void readFromNBT(CompoundNBT tag) {
		dimension = tag.getInteger("Dimension");

		centerX = tag.getInteger("CenterX");
		centerZ = tag.getInteger("CenterZ");

		scale = tag.getByte("Scale");

		ListNBT layersData = tag.getTagList("Layers", 10);
		int length = layersData.tagCount();
		layers = new LayerData[length];
		for (int i = 0; i < length; i++) {
			CompoundNBT layerData = layersData.getCompoundTagAt(i);
			LayerData layer = new LayerData();
			layer.readFromNBT(layerData);
			layers[i] = layer;
		}
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag.setInteger("Dimension", dimension);

		tag.setInteger("CenterX", centerX);
		tag.setInteger("CenterZ", centerZ);

		tag.setByte("Scale", scale);
		ListNBT result = new ListNBT();
		for (LayerData data : layers) {
			CompoundNBT layerData = new CompoundNBT();
			data.writeToNBT(layerData);
			result.appendTag(layerData);
		}
		tag.setTag("Layers", result);

		return tag;
	}

	public void readFromStream(PacketBuffer input) {
		dimension = input.readInt();
		centerX = input.readInt();
		centerZ = input.readInt();
		scale = input.readByte();
		final int length = input.readVarInt();
		layers = new LayerData[length];
		for (int i = 0; i < length; i++) {
			LayerData layer = new LayerData();
			layer.readFromStream(input);
			layers[i] = layer;
		}
	}

	public void writeToStream(PacketBuffer output) {
		output.writeInt(dimension);
		output.writeInt(centerX);
		output.writeInt(centerZ);
		output.writeByte(scale);
		output.writeVarInt(layers.length);
		for (LayerData data : layers)
			data.writeToStream(output);
	}
}