package openblocks.sync;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import openblocks.common.Stencil;
import net.minecraft.nbt.NBTTagCompound;

public class SyncableBlockLayers extends SyncableObjectBase {

	public static class Layer {
		
		private int color;
		private Stencil stencil;
		private byte rotation;
		private boolean hasStencilCover = false;

		public Layer() {
		}
		
		public Layer(int col) {
			this.color = col;
		}
		
		public int getColor() {
			return color;
		}
		
		/***
		 * If the layer has a cover on it, return white.
		 * Otherwise we render on the stored color
		 * @return
		 */
		public int getColorForRender() {
			return hasStencilCover() ? 0xFFFFFF : getColor();
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

	public ArrayList<Layer> layers;
	
	public ArrayList<Layer> getAllLayers() {
		return layers;
	}
	
	public boolean setColor(int color) {
		if (layers.size() > 0) {
			Layer layer = layers.get(layers.size() - 1);
			if (layer.hasStencilCover()) {
				layer.setColor(color);
				layer.setHasStencilCover(false);
				Layer newLayer = new Layer();
				newLayer.setStencil(layer.getStencil());
				newLayer.setHasStencilCover(true);
				newLayer.setRotation(layer.getRotation());
				layers.add(newLayer);
				markDirty();
				return true;
			}
		}
		return false;
	}
	
	public SyncableBlockLayers() {
		layers = new ArrayList<Layer>();
	}
	
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
			}else {
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
			subTag.setCompoundTag("layer_"+i, layer.getNBT());
			i++;
		}
		nbt.setCompoundTag(name, subTag);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, String name) {
		NBTTagCompound subTag = nbt.getCompoundTag(name);
		int size = subTag.getInteger("size");
		layers.clear();
		for (int i = 0; i < size; i++) {
			layers.add(Layer.createFromNBT(subTag.getCompoundTag("layer_"+i)));
		}
	}

	public Layer getLayer(int i) {
		if (i < layers.size()) {
			return layers.get(i);
		}
		return null;
	}

	public boolean hasStencilCover() {
		if (layers.size() > 0) {
			Layer layer = layers.get(layers.size() - 1);
			if (layer.hasStencilCover()) {
				return true;
			}
		}
		return false;
	}

	public void setStencilCover(Stencil stencil) {
		if (layers.size() > 0) {
			int lastIndex = layers.size() - 1;
			if (layers.get(lastIndex).hasStencilCover()) {
				layers.remove(lastIndex);
			}
		}
		Layer newLayer = new Layer();
		newLayer.setStencil(stencil);
		newLayer.setHasStencilCover(true);
		layers.add(newLayer);
		markDirty();
	}

	public void removeCover() {
		if (layers.size() > 0) {
			int lastIndex = layers.size() - 1;
			if (layers.get(lastIndex).hasStencilCover()) {
				layers.remove(lastIndex);
				markDirty();
			}
		}
	}
	
	public Stencil getTopStencil() {
		if (layers.size() > 0) {
			int lastIndex = layers.size() - 1;
			return layers.get(lastIndex).getStencil();
		}
		return null;
	}

	public void rotateCover() {
		if (layers.size() > 0) {
			int lastIndex = layers.size() - 1;
			Layer lastLayer = layers.get(lastIndex);
			if (lastLayer.hasStencilCover()) {
				lastLayer.rotate();
				markDirty();
			}
		}
	}

	public void clear() {
		layers.clear();
		markDirty();
	}

}
