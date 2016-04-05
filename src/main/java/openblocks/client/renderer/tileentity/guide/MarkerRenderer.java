package openblocks.client.renderer.tileentity.guide;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.util.ResourceLocation;
import openblocks.shapes.CoordShape;

public class MarkerRenderer {

	private static final ResourceLocation vertexSource = new ResourceLocation("openblocks:shaders/shader.vert");
	private static final ResourceLocation fragmentSource = new ResourceLocation("openblocks:shaders/shader.frag");
	private ShaderProgram shader;

	private static int nativeBufferSize = 0x200000;
	private static ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);
	private static IntBuffer intBuffer = byteBuffer.asIntBuffer();

	Runnable model;

	private boolean initialized = false;
	private int vertexCount, vao;

	public MarkerRenderer(Runnable model) throws Exception {
		shader = new ShaderProgram();
		shader.addShader(vertexSource, GL20.GL_VERTEX_SHADER);
		shader.addShader(fragmentSource, GL20.GL_FRAGMENT_SHADER);
		shader.compile();

		this.model = model;
	}

	public void createVAO() {
		model.run();
		TesselatorVertexState state = Tessellator.instance.getVertexState(0, 0, 0);
		Tessellator.instance.draw(); // just discard the state this way.

		if (state.getRawBuffer().length > nativeBufferSize)
			throw new UnsupportedOperationException("Big buffers not supported!");

		vertexCount = state.getVertexCount();

		{
			intBuffer.clear();
			intBuffer.put(state.getRawBuffer(), 0, vertexCount * 8);
			byteBuffer.position(0);
			byteBuffer.limit(vertexCount * 32);
		}

		vao = ArraysHelper.methods().glGenVertexArrays();
		ArraysHelper.methods().glBindVertexArray(vao);

		byteBuffer.position(0);

		int vbo = BufferHelper.methods().glGenBuffers();
		BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		BufferHelper.methods().glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);

		{
			shader.attributePointer("aVertex", 3, GL11.GL_FLOAT, false, 32, 0);
			shader.attributePointer("aTexCoord", 2, GL11.GL_FLOAT, false, 32, 12);
			shader.attributePointer("aColor", 4, GL11.GL_UNSIGNED_BYTE, false, 32, 20);
			shader.attributePointer("aBrightnessCoord", 2, GL11.GL_SHORT, false, 32, 28);
		}

		BufferHelper.methods().glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		shader.uniform1f("uHasTexture", state.getHasTexture() ? 1f : 0f);
		shader.uniform1f("uHasColor", state.getHasColor() ? 1f : 0f);
		shader.uniform1f("uHasBrightness", state.getHasBrightness() ? 1f : 0f);
		shader.uniform1i("uDefaultTexture", OpenGlHelper.defaultTexUnit - GL13.GL_TEXTURE0);
		shader.uniform1i("uLightmapTexture", OpenGlHelper.lightmapTexUnit - GL13.GL_TEXTURE0);

		byteBuffer.position(0);
		ArraysHelper.methods().glBindVertexArray(0);

		initialized = true;
	}

	public void draw() {
		shader.bind();
		if (!initialized)
			createVAO();
		ArraysHelper.methods().glBindVertexArray(vao);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, vertexCount);
		ArraysHelper.methods().glBindVertexArray(0);
		shader.release();
	}

	public void drawInstanced(CoordShape shape, int color, float scale) {
		shader.bind();
		if (!initialized)
			createVAO();
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
