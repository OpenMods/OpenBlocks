package openblocks.common.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import openblocks.client.stencils.Stencil;
import openmods.sync.SyncableObjectBase;
import openmods.utils.ByteUtils;

import com.google.common.collect.Lists;

public class SyncableBlockLayers extends SyncableObjectBase {

	public static class Layer {

		private int color;
		private BigInteger stencilBits = BigInteger.ZERO;
		private byte rotation;
		private boolean hasStencilCover = false;

		public Layer() {}

		public Layer(int col) {
			this.color = col;
		}

		public int getColor() {
			return color;
		}

		public int getColorForRender() {
			return hasStencilCover()? 0xFFFFFF : getColor();
		}

		public BigInteger getBits() {
			return stencilBits;
		}

		public void setBits(BigInteger stencilBits) {
			this.stencilBits = stencilBits;
		}

		public byte getRotation() {
			return rotation;
		}

		public boolean hasStencilCover() {
			return hasStencilCover;
		}

		public void setHasStencilCover(boolean cover) {
			this.hasStencilCover = cover;
		}

		public void setRotation(byte rotation) {
			this.rotation = rotation;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public static Layer createFromStream(DataInput stream) throws IOException {
			Layer layer = new Layer();
			layer.setColor(stream.readInt());
			layer.setRotation(stream.readByte());

			int size = ByteUtils.readVLI(stream);
			byte[] bytes = new byte[size];
			stream.readFully(bytes);
			layer.setBits(new BigInteger(bytes));

			layer.setHasStencilCover(stream.readBoolean());
			return layer;
		}

		public void rotate() {
			rotation++;
			if (rotation > 3) {
				rotation = 0;
			}
		}

		public NBTTagCompound writeToNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("rotation", rotation);
			nbt.setByteArray("bits", stencilBits.toByteArray());
			nbt.setBoolean("hasStencilCover", hasStencilCover);
			nbt.setInteger("color", color);
			return nbt;
		}

		public static Layer createFromNBT(NBTTagCompound compoundTag) {
			Layer layer = new Layer();
			layer.setColor(compoundTag.getInteger("color"));
			layer.setHasStencilCover(compoundTag.getBoolean("hasStencilCover"));
			layer.setRotation(compoundTag.getByte("rotation"));
			byte[] bits = compoundTag.getByteArray("bits");
			if (bits.length == 0) {
				Stencil legacyStencil;
				try {
					legacyStencil = Stencil.valueOf(compoundTag.getString("stencil"));
				} catch (Exception e) {
					legacyStencil = Stencil.CREEPER_FACE;
				}
				layer.setBits(legacyStencil.bits);
			} else {
				layer.setBits(new BigInteger(bits));
			}

			return layer;
		}
	}

	public final LinkedList<Layer> layers = Lists.newLinkedList();

	public SyncableBlockLayers() {}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		layers.clear();
		int size = ByteUtils.readVLI(stream);
		for (int i = 0; i < size; i++) {
			layers.add(Layer.createFromStream(stream));
		}
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData) throws IOException {
		ByteUtils.writeVLI(stream, layers.size());
		for (Layer layer : layers) {
			stream.writeInt(layer.getColor());
			stream.writeByte(layer.getRotation());
			byte[] bits = layer.stencilBits.toByteArray();
			ByteUtils.writeVLI(stream, bits.length);
			stream.write(bits);
			stream.writeBoolean(layer.hasStencilCover());
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String name) {
		NBTTagList list = new NBTTagList();
		for (Layer layer : layers)
			list.appendTag(layer.writeToNBT());

		nbt.setTag(name, list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String name) {
		NBTBase tag = nbt.getTag(name);
		if (tag instanceof NBTTagCompound) {
			NBTTagCompound subTag = (NBTTagCompound)tag;
			int size = subTag.getInteger("size");
			layers.clear();
			for (int i = 0; i < size; i++) {
				Layer layer = Layer.createFromNBT(subTag.getCompoundTag("layer_" + i));
				layers.add(layer);
			}
		} else if (tag instanceof NBTTagList) {
			NBTTagList list = (NBTTagList)tag;
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound layerTag = (NBTTagCompound)list.tagAt(i);
				Layer layer = Layer.createFromNBT(layerTag);
				layers.add(layer);
			}
		}
	}

	public List<Layer> getAllLayers() {
		return layers;
	}

	public boolean isLastLayerCover() {
		Layer last = layers.peekLast();
		return last != null && last.hasStencilCover;
	}

	public void setLastLayerColor(int color) {
		Layer last = getOrCreateLastLayer();
		last.setColor(color);
		markDirty();
	}

	public void setLastLayerStencil(BigInteger bits) {
		Layer last = getOrCreateLastLayer();
		last.hasStencilCover = true;
		last.stencilBits = bits;
		markDirty();
	}

	private Layer getOrCreateLastLayer() {
		Layer last = layers.peekLast();
		if (last == null) {
			last = new Layer();
			layers.addLast(last);
		}
		return last;
	}

	public void moveStencilToNextLayer() {
		Layer prevTop = layers.getLast();
		prevTop.setHasStencilCover(false);

		Layer newLayer = new Layer();
		newLayer.stencilBits = prevTop.stencilBits;
		newLayer.setHasStencilCover(true);
		newLayer.setRotation(prevTop.getRotation());
		layers.addLast(newLayer);
		markDirty();
	}

	public void pushNewStencil(BigInteger bits) {
		Layer newLayer = new Layer();
		newLayer.stencilBits = bits;
		newLayer.setHasStencilCover(true);
		layers.addLast(newLayer);
		markDirty();
	}

	public Layer getLayer(int i) {
		if (i < layers.size()) { return layers.get(i); }
		return null;
	}

	public BigInteger removeCover() {
		Layer last = layers.peekLast();
		if (last != null && last.hasStencilCover()) {
			layers.removeLast();
			markDirty();
		}
		return last.stencilBits;
	}

	public BigInteger getTopStencil() {
		Layer top = layers.peekLast();
		return top != null? top.stencilBits : null;
	}

	public void rotateCover() {
		Layer lastLayer = layers.peekLast();
		if (lastLayer != null && lastLayer.hasStencilCover()) {
			lastLayer.rotate();
			markDirty();
		}
	}

	public void clear() {
		layers.clear();
		markDirty();
	}

	public boolean isEmpty() {
		return layers.isEmpty();
	}
}
