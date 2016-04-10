package openblocks.client.renderer.tileentity.guide;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.util.ResourceLocation;
import openblocks.shapes.CoordShape;
import openmods.renderer.shaders.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class MarkerRenderer {

	private static final ResourceLocation vertexSource = new ResourceLocation("openblocks:shaders/shader.vert");
	private static final ResourceLocation fragmentSource = new ResourceLocation("openblocks:shaders/shader.frag");
	private final ShaderProgram shader;

	private static final int nativeBufferSize = 0x200000;
	private final ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);
	private final IntBuffer intBuffer = byteBuffer.asIntBuffer();

	private final Runnable model;

	private boolean initialized;
	private boolean hasTexture;
	private boolean hasColor;

	private boolean shouldRefresh = true;

	private int vertexCount;

	private int vao;
	private int vbo;

	public MarkerRenderer(Runnable model) {
		this.model = model;

		final ShaderProgramBuilder shaderProgramBuilder = new ShaderProgramBuilder();
		shaderProgramBuilder.addShader(vertexSource, GL20.GL_VERTEX_SHADER);
		shaderProgramBuilder.addShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);
		this.shader = shaderProgramBuilder.build();
	}

	public void reset() {
		shouldRefresh = true;
	}

	private void createModel() {
		model.run();
		TesselatorVertexState state = Tessellator.instance.getVertexState(0, 0, 0);
		Tessellator.instance.draw(); // just discard the state this way.

		if (state.getRawBuffer().length > nativeBufferSize) throw new UnsupportedOperationException("Big buffers not supported!");

		vertexCount = state.getVertexCount();

		byteBuffer.position(0);
		intBuffer.clear();
		intBuffer.put(state.getRawBuffer(), 0, vertexCount * 8);
		byteBuffer.position(0);
		byteBuffer.limit(vertexCount * 32);

		hasTexture = state.getHasTexture();
		hasColor = state.getHasColor();
	}

	private void createVAO() {
		if (initialized) {
			createModel();

			if (vao == 0) vao = ArraysHelper.methods().glGenVertexArrays();
			ArraysHelper.methods().glBindVertexArray(vao);

			if (vbo == 0) vbo = BufferHelper.methods().glGenBuffers();
			BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			BufferHelper.methods().glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);

			shader.attributePointer("aVertex", 3, GL11.GL_FLOAT, false, 32, 0);
			shader.attributePointer("aTexCoord", 2, GL11.GL_FLOAT, false, 32, 12);
			shader.attributePointer("aColor", 4, GL11.GL_UNSIGNED_BYTE, false, 32, 20);

			BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

			shader.uniform1f("uHasTexture", hasTexture? 1f : 0f);
			shader.uniform1f("uHasColor", hasColor? 1f : 0f);
			shader.uniform1i("uDefaultTexture", 0);

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
		shader.instanceAttributePointer("aPosition", 3, GL11.GL_INT, false, 0, 0);
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
