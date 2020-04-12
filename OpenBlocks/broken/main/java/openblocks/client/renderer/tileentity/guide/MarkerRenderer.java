package openblocks.client.renderer.tileentity.guide;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.shapes.CoordShape;
import openmods.renderer.shaders.ArraysHelper;
import openmods.renderer.shaders.BufferHelper;
import openmods.renderer.shaders.ShaderProgram;
import openmods.renderer.shaders.ShaderProgramBuilder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class MarkerRenderer {

	private static final ResourceLocation vertexSource = OpenBlocks.location("shaders/shader.vert");
	private static final ResourceLocation fragmentSource = OpenBlocks.location("shaders/shader.frag");
	private final ShaderProgram shader;

	private static final int nativeBufferSize = 0x200000;
	private final ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);

	private boolean initialized;

	private boolean shouldRefresh;
	private Supplier<BufferBuilder> modelSupplier;

	private int vertexCount;

	private int vao;
	private int vbo;

	public MarkerRenderer() {
		final ShaderProgramBuilder shaderProgramBuilder = new ShaderProgramBuilder();
		shaderProgramBuilder.addShader(vertexSource, GL20.GL_VERTEX_SHADER);
		shaderProgramBuilder.addShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);
		this.shader = shaderProgramBuilder.build();
	}

	public void setModel(Supplier<BufferBuilder> modelSupplier) {
		this.modelSupplier = modelSupplier;
		this.shouldRefresh = true;
	}

	private void copyBuffer(ByteBuffer buffer) {
		byteBuffer.clear();
		byteBuffer.put(buffer);
		byteBuffer.flip();
	}

	private void attributePointer(String name, VertexFormat format, int index, VertexFormatElement el) {
		shader.attributePointer(name, el.getElementCount(), el.getType().getGlConstant(), true, format.getIntegerSize() * 4, format.getOffset(index));
	}

	private void setupAttributes(final VertexFormat format) {
		boolean hasTexture = false;
		boolean hasColor = false;

		final List<VertexFormatElement> elements = format.getElements();
		for (int i = 0; i < elements.size(); i++) {
			final VertexFormatElement el = elements.get(i);
			switch (el.getUsage()) {
				case COLOR:
					attributePointer("aColor", format, i, el);
					hasColor = true;
					break;
				case POSITION:
					attributePointer("aVertex", format, i, el);
					break;
				case UV:
					attributePointer("aTexCoord", format, i, el);
					hasTexture = true;
					break;
				default:
					break;
			}
		}

		shader.uniform1f("uHasTexture", hasTexture? 1f : 0f);
		shader.uniform1f("uHasColor", hasColor? 1f : 0f);
		shader.uniform1i("uDefaultTexture", 0);
	}

	private void createVAO() {
		if (initialized) {
			Preconditions.checkNotNull(modelSupplier, "Marker model not loaded");

			final BufferBuilder vertexBuffer = modelSupplier.get();

			final ByteBuffer modelByteBuffer = vertexBuffer.getByteBuffer();
			if (modelByteBuffer.limit() > nativeBufferSize) throw new UnsupportedOperationException("Big buffers not supported!");

			vertexCount = vertexBuffer.getVertexCount();

			copyBuffer(modelByteBuffer);

			if (vao == 0) vao = ArraysHelper.methods().glGenVertexArrays();
			ArraysHelper.methods().glBindVertexArray(vao);

			if (vbo == 0) vbo = BufferHelper.methods().glGenBuffers();
			BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			BufferHelper.methods().glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);

			setupAttributes(vertexBuffer.getVertexFormat());

			BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

			ArraysHelper.methods().glBindVertexArray(0);
			shouldRefresh = false;
		}
	}

	public void drawInstanced(CoordShape shape, int color, float scale) {
		shader.bind();
		initialized = true;
		if (shouldRefresh) createVAO();
		ArraysHelper.methods().glBindVertexArray(vao);
		shape.bindVBO();
		shader.instanceAttributePointer("aPosition", 3, GL11.GL_FLOAT, false, 0, 0);
		BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		shader.uniform3f("uColor", ((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
		shader.uniform1f("uScale", scale);
		ArraysHelper.methods().glDrawArraysInstanced(GL11.GL_QUADS, 0, vertexCount, shape.size());
		ArraysHelper.methods().glBindVertexArray(0);
		shader.release();
	}

	public void deleteShape(CoordShape shape) {
		if (initialized) {
			ArraysHelper.methods().glBindVertexArray(vao);
			shape.destroy();
			ArraysHelper.methods().glBindVertexArray(0);
		}
	}
}
