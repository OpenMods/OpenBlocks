package openblocks.common.sync;

import com.google.common.base.Optional;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants;
import openblocks.client.renderer.block.canvas.CanvasSideState;
import openblocks.client.renderer.block.canvas.TextureOrientation;
import openblocks.common.StencilPattern;
import openmods.Log;
import openmods.sync.SyncableObjectBase;
import openmods.utils.Stack;

public class SyncableBlockLayers extends SyncableObjectBase {

	private static final String TAG_LAYERS = "Layers";
	private static final String TAG_BACKGROUND = "Background";
	private static final String TAG_COLOR = "Color";
	private static final String TAG_STENCIL = "Stencil";
	private static final String TAG_ROTATION = "Rotation";
	private static final String TAG_COVER = "Cover";

	private static class Pattern {
		public StencilPattern stencil;
		public TextureOrientation rotation;

		public void readFromStream(PacketBuffer stream) {
			rotation = TextureOrientation.values()[stream.readByte()];
			stencil = (StencilPattern.values()[stream.readByte()]);
		}

		public void writeToStream(PacketBuffer stream) {
			stream.writeByte(rotation.ordinal());
			stream.writeByte(stencil.ordinal());
		}

		public void readFromNBT(CompoundNBT tag) {
			rotation = TextureOrientation.values()[tag.getByte(TAG_ROTATION)];
			stencil = StencilPattern.valueOf(tag.getString(TAG_STENCIL));
		}

		public void writeToNBT(CompoundNBT tag) {
			tag.setByte(TAG_ROTATION, (byte)rotation.ordinal());
			tag.setString(TAG_STENCIL, stencil.name());
		}

		public boolean hasSamePatternAndRotation(Pattern other) {
			return this.rotation == other.rotation &&
					this.stencil == other.stencil;
		}
	}

	private static class Layer extends Pattern {

		public int color;

		public static Layer createFromStream(PacketBuffer stream) {
			final Layer layer = new Layer();
			try {
				layer.color = stream.readInt();
				layer.readFromStream(stream);
			} catch (Exception e) {
				Log.warn(e, "Failed to read stencil layer");
			}

			return layer;
		}

		@Override
		public void writeToStream(PacketBuffer stream) {
			stream.writeInt(color);
			super.writeToStream(stream);
		}

		public CompoundNBT writeToNBT() {
			final CompoundNBT nbt = new CompoundNBT();
			nbt.setInteger(TAG_COLOR, color);
			writeToNBT(nbt);
			return nbt;
		}

		public static Layer createFromNBT(CompoundNBT tag) {
			final Layer layer = new Layer();
			layer.color = tag.getInteger(TAG_COLOR);
			layer.readFromNBT(tag);
			return layer;
		}
	}

	private static class Cover extends Pattern {

		public Layer paint(int color) {
			final Layer result = new Layer();
			result.color = color;
			result.rotation = rotation;
			result.stencil = stencil;
			return result;
		}

		public static Optional<Cover> createFromStream(PacketBuffer stream) {
			if (!stream.readBoolean()) return Optional.absent();

			final Cover cover = new Cover();
			try {
				cover.readFromStream(stream);
			} catch (Exception e) {
				Log.warn(e, "Failed to read stencil cover");
			}

			return Optional.of(cover);
		}

		public CompoundNBT writeToNBT() {
			final CompoundNBT nbt = new CompoundNBT();
			writeToNBT(nbt);
			return nbt;
		}

		public static Cover createFromNBT(CompoundNBT tag) {
			final Cover cover = new Cover();
			cover.readFromNBT(tag);
			return cover;
		}
	}

	private final Stack<Layer> layers = Stack.create();

	private Optional<Cover> cover = Optional.absent();

	private int backgroundColor;

	@Override
	public void readFromStream(PacketBuffer stream) {
		backgroundColor = stream.readInt();
		int size = stream.readByte();
		layers.clear();
		for (byte i = 0; i < size; i++)
			layers.push(Layer.createFromStream(stream));

		cover = Cover.createFromStream(stream);
	}

	@Override
	public void writeToStream(PacketBuffer stream) {
		stream.writeInt(backgroundColor);
		stream.writeByte(layers.size());
		for (Layer layer : layers)
			layer.writeToStream(stream);

		if (cover.isPresent()) {
			stream.writeBoolean(true);
			cover.get().writeToStream(stream);
		} else {
			stream.writeBoolean(false);
		}
	}

	@Override
	public void writeToNBT(CompoundNBT nbt, String name) {
		final CompoundNBT subTag = new CompoundNBT();
		subTag.setInteger(TAG_BACKGROUND, backgroundColor);

		final ListNBT layersTag = new ListNBT();

		for (Layer layer : layers)
			layersTag.appendTag(layer.writeToNBT());

		subTag.setTag(TAG_LAYERS, layersTag);

		if (cover.isPresent())
			subTag.setTag(TAG_COVER, cover.get().writeToNBT());

		nbt.setTag(name, subTag);
	}

	@Override
	public void readFromNBT(CompoundNBT nbt, String name) {
		final CompoundNBT subTag = nbt.getCompoundTag(name);

		this.backgroundColor = subTag.getInteger(TAG_BACKGROUND);
		final ListNBT layersTag = subTag.getTagList(TAG_LAYERS, Constants.NBT.TAG_COMPOUND);
		layers.clear();
		for (int i = 0; i < layersTag.tagCount(); i++)
			layers.push(Layer.createFromNBT(layersTag.getCompoundTagAt(i)));

		if (subTag.hasKey(TAG_COVER, Constants.NBT.TAG_COMPOUND)) {
			cover = Optional.of(Cover.createFromNBT(subTag.getCompoundTag(TAG_COVER)));
		} else {
			cover = Optional.absent();
		}

	}

	public void applyPaint(int color) {
		if (cover.isPresent()) {
			paintOverCover(cover.get(), color);
		} else {
			// no stencil, covering all
			layers.clear();
			backgroundColor = color;
		}
		markDirty();
	}

	private void paintOverCover(Cover cover, int color) {
		if (!layers.isEmpty()) {
			final Layer top = layers.peek(0);
			if (cover.hasSamePatternAndRotation(top)) {
				// painting over last layer -> just changing color
				top.color = color;
				return;
			}
		}

		layers.push(cover.paint(color));
	}

	public boolean rotateCover() {
		if (cover.isPresent()) {
			final Cover c = cover.get();
			c.rotation = c.rotation.increment();
			markDirty();
			return true;
		}

		return false;
	}

	public Optional<StencilPattern> clearAll() {
		layers.clear();
		backgroundColor = 0;
		markDirty();

		final Optional<Cover> cover = this.cover;
		this.cover = Optional.absent();
		return cover.transform(input -> input.stencil);
	}

	public boolean putStencil(StencilPattern stencil) {
		if (cover.isPresent()) return false;

		final Cover cover = new Cover();
		cover.rotation = TextureOrientation.R0;
		cover.stencil = stencil;
		this.cover = Optional.of(cover);

		markDirty();
		return true;
	}

	public Optional<StencilPattern> popStencil() {
		final Optional<Cover> cover = this.cover;
		this.cover = Optional.absent();
		markDirty();
		return cover.transform(input -> input.stencil);
	}

	public Optional<StencilPattern> peekStencil() {
		return cover.transform(input -> input.stencil);
	}

	public boolean isEmpty() {
		return backgroundColor == 0 && layers.isEmpty();
	}

	public CanvasSideState convertToState() {
		final CanvasSideState.Builder builder = CanvasSideState.builder().withBackground(backgroundColor);

		for (Layer layer : layers)
			builder.addLayer(layer.stencil, layer.color, layer.rotation);

		if (cover.isPresent()) {
			final Cover c = cover.get();
			return builder.withCover(c.stencil, c.rotation);
		} else {
			return builder.withoutCover();
		}
	}
}