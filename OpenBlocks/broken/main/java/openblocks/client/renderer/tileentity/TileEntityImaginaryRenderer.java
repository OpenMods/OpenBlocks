package openblocks.client.renderer.tileentity;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.Usage;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.Config;
import openblocks.common.block.BlockImaginary;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;

public class TileEntityImaginaryRenderer extends FastTESR<TileEntityImaginary> {

	private static BlockRendererDispatcher blockRenderer;

	private static final int BYTES_PER_INT = 4;
	private static final int VERTICES_PER_QUAD = 4;

	private static class RenderInfo {
		public final double x;
		public final double y;
		public final double z;

		public final float r;
		public final float g;
		public final float b;
		public final float a;

		public RenderInfo(double x, double y, double z, float r, float g, float b, float a) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
		}

	}

	private interface IVertexElementWriter {
		void write(ByteBuffer output, RenderInfo info);
	}

	private static class VertexWriter {
		private final IVertexElementWriter[] writers;

		public VertexWriter(IVertexElementWriter[] writers) {
			this.writers = writers;
		}

		public void write(BufferBuilder buffer, RenderInfo info) {
			final ByteBuffer outputBuffer = buffer.getByteBuffer();
			for (IVertexElementWriter writer : writers)
				writer.write(outputBuffer, info);

			buffer.endVertex();
		}
	}

	private static Map<VertexFormatElement, Integer> createElementOffsetMap(VertexFormat format) {
		ImmutableMap.Builder<VertexFormatElement, Integer> builder = ImmutableMap.builder();

		for (int i = 0; i < format.getElementCount(); i++) {
			VertexFormatElement vfe = format.getElement(i);
			if (vfe.getUsage() != Usage.PADDING)
				builder.put(vfe, format.getOffset(i));
		}

		return builder.build();
	}

	private static void convertQuad(BakedQuad quad, List<VertexWriter> output) {
		final VertexFormat inputFormat = quad.getFormat();
		final Map<VertexFormatElement, Integer> inputFormatMap = createElementOffsetMap(inputFormat);

		final int[] vertexData = quad.getVertexData();
		ByteBuffer inputBuffer = ByteBuffer.allocate(vertexData.length * BYTES_PER_INT);
		inputBuffer.asIntBuffer().put(vertexData);
		inputBuffer.clear();

		final Integer positionOffset = inputFormatMap.get(DefaultVertexFormats.POSITION_3F);
		if (positionOffset == null) throw new AssertionError("Invalid format: " + inputFormat);

		final Integer colorOffset = inputFormatMap.get(DefaultVertexFormats.COLOR_4UB);

		final Integer textureOffset = inputFormatMap.get(DefaultVertexFormats.TEX_2F);
		if (textureOffset == null) throw new AssertionError("Invalid format: " + inputFormat);

		final int vertexSize = inputFormat.getIntegerSize() * BYTES_PER_INT;
		int vertexOffset = 0;
		for (int i = 0; i < VERTICES_PER_QUAD; i++) {

			IVertexElementWriter[] vertexWriters = new IVertexElementWriter[4];
			// BLOCK.addElement(POSITION_3F);
			vertexWriters[0] = createPositionWriter(inputBuffer, vertexOffset + positionOffset);

			// BLOCK.addElement(COLOR_4UB);
			vertexWriters[1] = colorOffset != null
					? createColorWriter(inputBuffer, vertexOffset + colorOffset)
					: FIXED_COLOR_WRITER;

			// BLOCK.addElement(TEX_2F);
			vertexWriters[2] = createTextureWriter(inputBuffer, vertexOffset + textureOffset);

			// BLOCK.addElement(TEX_2S);
			vertexWriters[3] = FIXED_BRIGHTNESS_WRITER;

			output.add(new VertexWriter(vertexWriters));

			vertexOffset += vertexSize;
		}
	}

	private static IVertexElementWriter createPositionWriter(ByteBuffer inputBuffer, int position) {
		inputBuffer.position(position);

		final float x = inputBuffer.getFloat();
		final float y = inputBuffer.getFloat();
		final float z = inputBuffer.getFloat();

		return (output, info) -> {
			output.putFloat((float)(x + info.x));
			output.putFloat((float)(y + info.y));
			output.putFloat((float)(z + info.z));
		};
	}

	private static IVertexElementWriter createColorWriter(ByteBuffer inputBuffer, int position) {
		inputBuffer.position(position);

		final float inputA = inputBuffer.get() & 0xFF;
		final float inputB = inputBuffer.get() & 0xFF;
		final float inputG = inputBuffer.get() & 0xFF;
		final float inputR = inputBuffer.get() & 0xFF;

		return (output, info) -> {
			final int alpha = Math.min(0xFF, (int)(inputA * info.a));
			final int red = Math.min(0xFF, (int)(inputR * info.r));
			final int green = Math.min(0xFF, (int)(inputG * info.g));
			final int blue = Math.min(0xFF, (int)(inputB * info.b));

			output.put((byte)red);
			output.put((byte)green);
			output.put((byte)blue);
			output.put((byte)alpha);
		};
	}

	private static IVertexElementWriter createTextureWriter(ByteBuffer inputBuffer, int position) {
		inputBuffer.position(position);

		final float u = inputBuffer.getFloat();
		final float v = inputBuffer.getFloat();

		return (output, info) -> {
			output.putFloat(u);
			output.putFloat(v);
		};
	}

	private static final IVertexElementWriter FIXED_COLOR_WRITER = (output, info) -> {
		output.put((byte)Math.min(0xFF, 0xFF * info.a));
		output.put((byte)Math.min(0xFF, 0xFF * info.b));
		output.put((byte)Math.min(0xFF, 0xFF * info.g));
		output.put((byte)Math.min(0xFF, 0xFF * info.r));
	};

	private static final IVertexElementWriter FIXED_BRIGHTNESS_WRITER = (output, info) -> output.putInt(0x00F000F0);

	private static void addQuads(List<BakedQuad> quads, List<VertexWriter> output) {
		for (BakedQuad quad : quads) {
			convertQuad(quad, output);
		}
	}

	private static final LoadingCache<BlockState, List<VertexWriter>> MODEL_CACHE = CacheBuilder.newBuilder()
			.expireAfterAccess(1, TimeUnit.SECONDS)
			.build(new CacheLoader<BlockState, List<VertexWriter>>() {
				@Override
				public List<VertexWriter> load(BlockState state) {
					if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
					IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(state);

					final List<VertexWriter> vertexWriters = Lists.newArrayList();

					for (Direction side : Direction.VALUES)
						addQuads(model.getQuads(state, side, 0), vertexWriters);

					addQuads(model.getQuads(state, null, 0), vertexWriters);

					return ImmutableList.copyOf(vertexWriters);
				}
			});

	public static class CacheFlushListener {
		@SubscribeEvent
		public void onModelBake(ModelBakeEvent evt) {
			MODEL_CACHE.invalidateAll();
		}
	}

	@Override
	public void renderTileEntityFast(TileEntityImaginary te, double x, double y, double z, float partialTicks, int destroyStage, float alpha, BufferBuilder buffer) {
		if (te == null) return;

		boolean isVisible = te.is(Property.VISIBLE);

		if (isVisible && te.visibility < 1) te.visibility = Math.min(te.visibility + Config.imaginaryFadingSpeed, 1);
		else if (!isVisible && te.visibility > 0) te.visibility = Math.max(te.visibility - Config.imaginaryFadingSpeed, 0);

		if (te.visibility <= 0) return;

		final float r;
		final float g;
		final float b;

		int color = te.getColor();
		r = ((color >> 16) & 0xFF) / 255.0f;
		g = ((color >> 8) & 0xFF) / 255.0f;
		b = ((color >> 0) & 0xFF) / 255.0f;

		final float a = te.visibility;
		final RenderInfo info = new RenderInfo(x, y, z, r, g, b, a);

		BlockPos pos = te.getPos();
		IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
		BlockState state = world.getBlockState(pos).getActualState(world, pos);

		if (state.getBlock() instanceof BlockImaginary) {
			final List<VertexWriter> vertexWriters = MODEL_CACHE.getUnchecked(state);
			for (VertexWriter writer : vertexWriters)
				writer.write(buffer, info);
		}
	}

}
