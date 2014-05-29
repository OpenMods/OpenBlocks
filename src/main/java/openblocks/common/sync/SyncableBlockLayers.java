package openblocks.common.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import openblocks.common.Stencil;
import openmods.sync.SyncableObjectBase;

import com.google.common.collect.Lists;

public class SyncableBlockLayers extends SyncableObjectBase {

	public static class Layer {

		private int color;
		private Stencil stencil;
		private byte rotation;
		private boolean hasStencilCover = false;

		public Layer() {}

		public Layer(int col) {
			this.color = col;
		}

		public int getColor() {
			return color;
		}

		/***
		 * If the layer has a cover on it, return white.
		 * Otherwise we render on the stored color
		 * 
		 * @return
		 */
		public int getColorForRender() {
			return hasStencilCover()? 0xFFFFFF : getColor();
		}

		public Stencil getStencil() {
			return stencil;
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

		public void setStencil(Stencil st) {
			this.stencil = st;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public static Layer createFromStream(DataInput stream) {
			Layer layer = new Layer();
			try {
				layer.setColor(stream.readInt());
				layer.setRotation(stream.readByte());
				byte b = stream.readByte();
				if (b > -1) {
					layer.setStencil(Stencil.values()[b]);
				}
				layer.setHasStencilCover(stream.readBoolean());
			} catch (Exception e) {

			}
			return layer;
		}

		public void rotate() {
			rotation++;
			if (rotation > 3) {
				rotation = 0;
			}
		}

		public NBTTagCompound getNBT() {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setByte("rotation", rotation);
			nbt.setString("stencil", stencil.name());
			nbt.setBoolean("hasStencilCover", hasStencilCover);
			nbt.setInteger("color", color);
			return nbt;
		}

		public static Layer createFromNBT(NBTTagCompound compoundTag) {
			Layer layer = new Layer();
			layer.setColor(compoundTag.getInteger("color"));
			layer.setHasStencilCover(compoundTag.getBoolean("hasStencilCover"));
			layer.setRotation(compoundTag.getByte("rotation"));
			layer.setStencil(Stencil.valueOf(compoundTag.getString("stencil")));
			return layer;
		}
	}

	public final LinkedList<Layer> layers = Lists.newLinkedList();

	public SyncableBlockLayers() {}

	@Override
	public void readFromStream(DataInput stream) throws IOException {
		int size = stream.readByte();
		layers.clear();
		for (byte i = 0; i < size; i++) {
			layers.add(Layer.createFromStream(stream));
		}
	}

	@Override
	public void writeToStream(DataOutput stream, boolean fullData)
			throws IOException {
		stream.writeByte(layers.size());
		for (Layer layer : layers) {
			stream.writeInt(layer.getColor());
			stream.writeByte(layer.getRotation());
			if (layer.getStencil() != null) {
				stream.writeByte(layer.getStencil().ordinal());
			} else {
				stream.writeByte(-1);
			}
			stream.writeBoolean(layer.hasStencilCover());
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, String name) {
		NBTTagCompound subTag = new NBTTagCompound();
		subTag.setInteger("size", layers.size());
		int i = 0;
		for (Layer layer : layers) {
			subTag.setTag("layer_" + i, layer.getNBT());
			i++;
		}
		nbt.setTag(name, subTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String name) {
		NBTTagCompound subTag = nbt.getCompoundTag(name);
		int size = subTag.getInteger("size");
		layers.clear();
		for (int i = 0; i < size; i++) {
			layers.add(Layer.createFromNBT(subTag.getCompoundTag("layer_" + i)));
		}
	}

	public List<Layer> getAllLayers() {
		return layers;
	}

	public boolean isLastLayerStencil() {
		Layer last = layers.peekLast();
		return last != null && last.hasStencilCover && last.stencil != null;
	}

	public void setLastLayerColor(int color) {
		Layer last = getOrCreateLastLayer();
		last.setColor(color);
		markDirty();
	}

	public void setLastLayerStencil(Stencil stencil) {
		Layer last = getOrCreateLastLayer();
		last.hasStencilCover = true;
		last.stencil = stencil;
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
		newLayer.setStencil(prevTop.getStencil());
		newLayer.setHasStencilCover(true);
		newLayer.setRotation(prevTop.getRotation());
		layers.addLast(newLayer);
		markDirty();
	}

	public void pushNewStencil(Stencil stencil) {
		Layer newLayer = new Layer();
		newLayer.setStencil(stencil);
		newLayer.setHasStencilCover(true);
		layers.addLast(newLayer);
		markDirty();
	}

	public Layer getLayer(int i) {
		if (i < layers.size()) { return layers.get(i); }
		return null;
	}

	public void removeCover() {
		Layer last = layers.peekLast();
		if (last != null && last.hasStencilCover()) {
			layers.removeLast();
			markDirty();
		}
	}

	public Stencil getTopStencil() {
		Layer top = layers.peekLast();
		return top != null? top.stencil : null;
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