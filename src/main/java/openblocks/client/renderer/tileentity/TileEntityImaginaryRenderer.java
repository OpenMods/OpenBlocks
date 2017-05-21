package openblocks.client.renderer.tileentity;

import com.google.common.collect.ImmutableMap;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.Config;
import openblocks.common.tileentity.TileEntityImaginary;
import openblocks.common.tileentity.TileEntityImaginary.Property;

public class TileEntityImaginaryRenderer extends TileEntitySpecialRenderer<TileEntityImaginary> {

	private static BlockRendererDispatcher blockRenderer;

	@Override
	public void renderTileEntityFast(TileEntityImaginary te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer) {
		if (te == null) return;

		boolean isVisible = te.is(Property.VISIBLE);

		if (isVisible && te.visibility < 1) te.visibility = Math.min(te.visibility + Config.imaginaryFadingSpeed, 1);
		else if (!isVisible && te.visibility > 0) te.visibility = Math.max(te.visibility - Config.imaginaryFadingSpeed, 0);

		if (te.visibility <= 0) return;

		BlockPos pos = te.getPos();
		IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
		IBlockState state = world.getBlockState(pos).getActualState(world, pos);
		long rand = MathHelper.getPositionRandom(pos);

		if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
		IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(state);

		final float r;
		final float g;
		final float b;

		if (te.isPencil()) {
			r = 1.0f;
			g = 1.0f;
			b = 1.0f;
		} else {
			int color = te.color;
			r = ((color >> 16) & 0xFF) / 255.0f;
			g = ((color >> 8) & 0xFF) / 255.0f;
			b = ((color >> 0) & 0xFF) / 255.0f;
		}

		final float a = te.visibility;

		for (EnumFacing side : EnumFacing.VALUES)
			addQuads(model.getQuads(state, side, rand), x, y, z, r, g, b, a, buffer);

		addQuads(model.getQuads(state, null, rand), x, y, z, r, g, b, a, buffer);
	}

	private static void addQuads(List<BakedQuad> quads, double x, double y, double z, float r, float g, float b, float a, VertexBuffer buffer) {
		for (BakedQuad quad : quads) {
			buffer.addVertexData(repackQuad(quad, x, y, z, r, g, b, a));
		}
	}

	private static final Map<VertexFormatElement, Integer> blockFormatOffsets = createElementOffsetMap(DefaultVertexFormats.BLOCK);

	private static Map<VertexFormatElement, Integer> createElementOffsetMap(VertexFormat format) {
		ImmutableMap.Builder<VertexFormatElement, Integer> builder = ImmutableMap.builder();

		for (int i = 0; i < format.getElementCount(); i++) {
			VertexFormatElement vfe = format.getElement(i);
			builder.put(vfe, format.getOffset(i));
		}

		return builder.build();
	}

	private static final int BYTES_PER_INT = 4;
	private static final int VERTICES_PER_QUAD = 4;

	private static int[] repackQuad(BakedQuad quad, double offsetX, double offsetY, double offsetZ, float r, float g, float b, float a) {
		int[] inputData = quad.getVertexData();

		final VertexFormat inputFormat = quad.getFormat();
		if (inputFormat.equals(DefaultVertexFormats.BLOCK)) return inputData;

		ByteBuffer inputBuffer = ByteBuffer.allocate(inputData.length * BYTES_PER_INT);
		inputBuffer.asIntBuffer().put(inputData);
		inputBuffer.flip();

		int outputSize = DefaultVertexFormats.BLOCK.getIntegerSize() * VERTICES_PER_QUAD * BYTES_PER_INT;
		ByteBuffer outputBuffer = ByteBuffer.allocate(outputSize);
		outputBuffer.limit(outputSize);

		final int outputVertexDelta = DefaultVertexFormats.BLOCK.getIntegerSize() * BYTES_PER_INT;
		final int inputVertexDelta = inputFormat.getIntegerSize() * BYTES_PER_INT;

		for (int elementIndex = 0; elementIndex < inputFormat.getElementCount(); elementIndex++) {
			VertexFormatElement vfe = inputFormat.getElement(elementIndex);

			Integer blockElementOffset = blockFormatOffsets.get(vfe);
			if (blockElementOffset != null) {
				int inputOffset = inputFormat.getOffset(elementIndex);

				if (vfe.equals(DefaultVertexFormats.COLOR_4UB)) {
					copyColor(r, g, b, a, inputBuffer, inputOffset, inputVertexDelta, outputBuffer, blockElementOffset, outputVertexDelta);
				} else if (vfe.equals(DefaultVertexFormats.POSITION_3F)) {
					copyPosition(offsetX, offsetY, offsetZ, inputBuffer, inputOffset, inputVertexDelta, outputBuffer, blockElementOffset, outputVertexDelta);
				} else {
					copyElement(vfe, inputBuffer, inputOffset, inputVertexDelta, outputBuffer, blockElementOffset, outputVertexDelta);
				}
			}
		}

		fixBrightness(outputBuffer, outputVertexDelta);

		outputBuffer.limit(outputSize);
		outputBuffer.position(0);

		int[] output = new int[DefaultVertexFormats.BLOCK.getIntegerSize() * VERTICES_PER_QUAD];
		outputBuffer.asIntBuffer().get(output);

		return output;
	}

	private static void fixBrightness(ByteBuffer outputBuffer, final int outputVertexDelta) {
		final int brightnessOffset = DefaultVertexFormats.BLOCK.getUvOffsetById(1);
		int outputQuadOffset = 0;
		for (int quadIndex = 0; quadIndex < VERTICES_PER_QUAD; quadIndex++) {
			final int outputStart = outputQuadOffset + brightnessOffset;
			outputBuffer.limit(outputStart + DefaultVertexFormats.TEX_2S.getSize());
			outputBuffer.position(outputStart);

			// maximum brightness, because imagination!
			outputBuffer.putInt(0x00F000F0);

			outputQuadOffset += outputVertexDelta;
		}
	}

	private static void copyPosition(double offsetX, double offsetY, double offsetZ, ByteBuffer inputBuffer, int inputOffset, int inputVertexDelta, ByteBuffer outputBuffer, int outputOffset, int outputVertexDelta) {
		final int elementSize = DefaultVertexFormats.POSITION_3F.getSize();
		int outputQuadOffset = 0;
		int inputQuadOffset = 0;
		for (int quadIndex = 0; quadIndex < VERTICES_PER_QUAD; quadIndex++) {
			final int inputStart = inputQuadOffset + inputOffset;

			inputBuffer.limit(inputStart + elementSize);
			inputBuffer.position(inputStart);

			final FloatBuffer inputFloatBuffer = inputBuffer.asFloatBuffer();
			float x = inputFloatBuffer.get();
			float y = inputFloatBuffer.get();
			float z = inputFloatBuffer.get();

			final int outputStart = outputQuadOffset + outputOffset;
			outputBuffer.limit(outputStart + elementSize);
			outputBuffer.position(outputStart);

			final FloatBuffer outputFloatBuffer = outputBuffer.asFloatBuffer();
			outputFloatBuffer.put((float)(x + offsetX));
			outputFloatBuffer.put((float)(y + offsetY));
			outputFloatBuffer.put((float)(z + offsetZ));

			outputQuadOffset += outputVertexDelta;
			inputQuadOffset += inputVertexDelta;
		}
	}

	private static void copyColor(float r, float g, float b, float a, ByteBuffer inputBuffer, int inputOffset, int inputVertexDelta, ByteBuffer outputBuffer, int outputOffset, int outputVertexDelta) {
		final int elementSize = DefaultVertexFormats.COLOR_4UB.getSize();
		int outputQuadOffset = 0;
		int inputQuadOffset = 0;
		for (int quadIndex = 0; quadIndex < VERTICES_PER_QUAD; quadIndex++) {
			final int inputStart = inputQuadOffset + inputOffset;

			inputBuffer.limit(inputStart + elementSize);
			inputBuffer.position(inputStart);

			float inputA = inputBuffer.get() & 0xFF;
			float inputB = inputBuffer.get() & 0xFF;
			float inputG = inputBuffer.get() & 0xFF;
			float inputR = inputBuffer.get() & 0xFF;

			final int outputStart = outputQuadOffset + outputOffset;
			outputBuffer.limit(outputStart + elementSize);
			outputBuffer.position(outputStart);

			outputBuffer.put((byte)Math.min(0xFF, a * inputA));
			outputBuffer.put((byte)Math.min(0xFF, b * inputB));
			outputBuffer.put((byte)Math.min(0xFF, g * inputG));
			outputBuffer.put((byte)Math.min(0xFF, r * inputR));

			// outputBuffer.put((byte)0);
			// outputBuffer.put((byte)0);
			// outputBuffer.put((byte)0);

			outputQuadOffset += outputVertexDelta;
			inputQuadOffset += inputVertexDelta;
		}

	}

	private static void copyElement(VertexFormatElement element, ByteBuffer inputBuffer, int inputOffset, int inputVertexDelta, ByteBuffer outputBuffer, int outputOffset, int outputVertexDelta) {
		final int elementSize = element.getSize();
		int outputQuadOffset = 0;
		int inputQuadOffset = 0;

		for (int quadIndex = 0; quadIndex < VERTICES_PER_QUAD; quadIndex++) {
			final int inputStart = inputQuadOffset + inputOffset;
			inputBuffer.limit(inputStart + elementSize);
			inputBuffer.position(inputStart);

			final int outputStart = outputQuadOffset + outputOffset;
			outputBuffer.limit(outputStart + elementSize);
			outputBuffer.position(outputStart);

			outputBuffer.put(inputBuffer);

			outputQuadOffset += outputVertexDelta;
			inputQuadOffset += inputVertexDelta;
		}
	}

}
