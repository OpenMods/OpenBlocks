package openblocks.client.renderer.tileentity.guide;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.util.ResourceLocation;

public class MarkerRenderer {

	private static final ResourceLocation vertexSource = new ResourceLocation("openblocks:shaders/shader.vert");
	private static final ResourceLocation fragmentSource = new ResourceLocation("openblocks:shaders/shader.frag");
	private ShaderProgram shader;

	private static int nativeBufferSize = 0x200000;
	private static ByteBuffer byteBuffer = GLAllocation.createDirectByteBuffer(nativeBufferSize * 4);
    private static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    
    FutureTesselator model;
	
	private boolean initialized = false;
	private int vertexCount, vao;
	
	public MarkerRenderer(FutureTesselator model) throws Exception {
		shader = new ShaderProgram();
		shader.addShader(vertexSource, ShaderHelper.GL_VERTEX_SHADER);
		shader.addShader(fragmentSource, ShaderHelper.GL_FRAGMENT_SHADER);
		shader.compile();
			
		this.model = model;
	}

	public void createVAO()
	{
		model.render();
		TesselatorVertexState state = Tessellator.instance.getVertexState(0,0,0);
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
		
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		byteBuffer.position(0);
		
		int vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);
		
		{
			shader.attributePointer("aVertex", 3, GL11.GL_FLOAT, false, 32, 0);
			shader.attributePointer("aTexCoord", 2, GL11.GL_FLOAT, false, 32, 12);
			shader.attributePointer("aColor", 4, GL11.GL_UNSIGNED_BYTE, false, 32, 20);
			shader.attributePointer("aBrightnessCoord", 2, GL11.GL_SHORT, false, 32, 28);
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		shader.uniform1f("uHasTexture", state.getHasTexture() ? 1f : 0f); // no conversions on the GPU this way.
		shader.uniform1f("uHasColor", state.getHasColor() ? 1f : 0f);
		shader.uniform1f("uHasBrightness", state.getHasBrightness() ? 1f : 0f);
		shader.uniform1i("uDefaultTexture", OpenGlHelper.defaultTexUnit - GL13.GL_TEXTURE0); // should be 0
		shader.uniform1i("uLightmapTexture", OpenGlHelper.lightmapTexUnit - GL13.GL_TEXTURE0); // should be 1

		byteBuffer.position(0);
		GL30.glBindVertexArray(0);
		
		initialized = true;
	}
	
	public void draw() {
		shader.bind();
		if (!initialized)
			createVAO();
		GL30.glBindVertexArray(vao);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, vertexCount);
		GL30.glBindVertexArray(0);
		shader.release();
	}
	
	public void drawInstanced(CoordShape shape, int color, float scale) {
		shader.bind();
		if (!initialized)
			createVAO();
		GL30.glBindVertexArray(vao);
		
		shape.bindVBO();
		shader.instanceAttributePointer("aPosition", 3, GL11.GL_INT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		shader.uniform3f("uColor", ((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
		shader.uniform1f("uScale", scale);
		
		GL31.glDrawArraysInstanced(GL11.GL_QUADS, 0, vertexCount, shape.size());
		
		GL30.glBindVertexArray(0);
		shader.release();
	}
	
	public void deleteShape(CoordShape shape)
	{
		if (initialized)
		{
			GL30.glBindVertexArray(vao);
			shape.destroy();
			GL30.glBindVertexArray(0);
		}
	}
}
