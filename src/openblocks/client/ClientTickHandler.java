package openblocks.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.IntBuffer;
import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import openblocks.OpenBlocks;
import openblocks.common.CommonProxy;
import openblocks.utils.FileLineReader;
import openblocks.utils.ILineReadMethod;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler, ILineReadMethod {

	private int renderTextureWidth = 128;
	private int renderTextureHeight = 128;

	private StringBuilder builder;
	public int framebufferID = -1;
	public int depthbufferID = -1;
	public int textureID = -1;
	private Minecraft mc;
	IntBuffer viewportInfo;
	int originalTexture;
	public static boolean isBeingDragged = false;
	int program = 0;
	
	public ClientTickHandler() {

		int vertShader = 0, fragShader = 0;

		try {
			vertShader = createShader("shaders/screen.vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
			fragShader = createShader("shaders/screen.frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
		} catch (Exception exc) {
			exc.printStackTrace();
			return;
		} finally {
			if (vertShader == 0 || fragShader == 0)
				return;
		}

		program = ARBShaderObjects.glCreateProgramObjectARB();

		if (program == 0)
			return;

		ARBShaderObjects.glAttachObjectARB(program, vertShader);
		ARBShaderObjects.glAttachObjectARB(program, fragShader);

		ARBShaderObjects.glLinkProgramARB(program);
		if (ARBShaderObjects.glGetObjectParameteriARB(program,
				ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
			System.err.println(getLogInfo(program));
			return;
		}

		ARBShaderObjects.glValidateProgramARB(program);
		if (ARBShaderObjects.glGetObjectParameteriARB(program,
				ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
			System.err.println(getLogInfo(program));
			return;
		}

		createFramebuffer();
	}
	
	private void createFramebuffer() {
		Minecraft mc = FMLClientHandler.instance().getClient();
		renderTextureWidth = mc.displayWidth;
		renderTextureHeight = mc.displayHeight;

		framebufferID = GL30.glGenFramebuffers();
		textureID = GL11.glGenTextures();
		int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferID);

		// Set our texture up, empty.
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, renderTextureWidth, renderTextureHeight, 0, GL12.GL_BGRA,
				GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);

		// Restore old texture
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);

		// Create depth buffer
		depthbufferID = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthbufferID);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, renderTextureWidth, renderTextureHeight);

		// Bind depth buffer to the framebuffer
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthbufferID);

		// Bind our texture to the framebuffer
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, textureID, 0);

		// Revert to default framebuffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

		Minecraft mc = FMLClientHandler.instance().getClient();
		if (type.contains(TickType.RENDER) && isBeingDragged && mc.theWorld != null && mc.currentScreen == null) {

			// Render to our texture
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferID);


			viewportInfo = GLAllocation.createDirectIntBuffer(16);
			GL11.glGetInteger(GL11.GL_VIEWPORT, viewportInfo);
			GL11.glViewport(0, 0, renderTextureWidth, renderTextureHeight);
			originalTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

		Minecraft mc = FMLClientHandler.instance().getClient();
		if (type.contains(TickType.RENDER) && isBeingDragged && mc.theWorld != null && mc.currentScreen == null) {
			// Revert to default viewport
			GL11.glViewport(viewportInfo.get(0), viewportInfo.get(1), viewportInfo.get(2), viewportInfo.get(3));

			// Revert to default framebuffer
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			
			GL11.glClearColor(0.2f, 0, 0, 0);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			// Bind framebuffer texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			ARBShaderObjects.glUseProgramObjectARB(program);
			int originalTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			GL11.glTranslatef((renderTextureWidth/2), (renderTextureHeight/2), 0);
			GL11.glRotatef(180f, 0, 0, 1);
			
			// Bind framebuffer texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
			int texCoord = GL20.glGetAttribLocation(program, "TexCoord");
			int col = GL20.glGetAttribLocation(program, "Color");
			GL11.glBegin(GL11.GL_QUADS);

			GL20.glVertexAttrib4f(col, 100, 0, 0, 0.7f);
			GL20.glVertexAttrib2f(texCoord, 1, 0);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2i(0, 0);

		
			GL20.glVertexAttrib4f(col, 100, 0, 0, 0.7f);
			GL20.glVertexAttrib2f(texCoord, 1, 1);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2i(0, renderTextureHeight/2);

			GL20.glVertexAttrib4f(col, 100, 0, 0, 0.7f);
			GL20.glVertexAttrib2f(texCoord, 0, 1);

			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2i(renderTextureWidth/2, renderTextureHeight/2);

			GL20.glVertexAttrib4f(col, 100, 0, 0, 0.7f);
			GL20.glVertexAttrib2f(texCoord, 0, 0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2i(renderTextureWidth/2, 0);

			GL11.glEnd();
			ARBShaderObjects.glUseProgramObjectARB(0);
			// Restore old texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, originalTexture);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.RENDER);
	}

	@Override
	public String getLabel() {
		return "OpenBlocksRender";
	}

	/*
	 * With the exception of syntax, setting up vertex and fragment shaders is
	 * the same.
	 * 
	 * @param the name and path to the vertex shader
	 */
	private int createShader(String filename, int shaderType) throws Exception {
		int shader = 0;
		try {
			shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

			if (shader == 0)
				return 0;

			ARBShaderObjects.glShaderSourceARB(shader,
					readFileAsString(filename));
			ARBShaderObjects.glCompileShaderARB(shader);

			if (ARBShaderObjects.glGetObjectParameteriARB(shader,
					ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
				throw new RuntimeException("Error creating shader: "
						+ getLogInfo(shader));

			return shader;
		} catch (Exception exc) {
			ARBShaderObjects.glDeleteObjectARB(shader);
			throw exc;
		}
	}

	private static String getLogInfo(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects
				.glGetObjectParameteriARB(obj,
						ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}

	private String readFileAsString(String filename) throws Exception {
		InputStream input = CommonProxy.class.getResourceAsStream(String
				.format("%s/%s", OpenBlocks.getResourcesPath(), filename));

		BufferedReader reader = new BufferedReader(new InputStreamReader(input,
				"UTF-8"));

		builder = new StringBuilder();
		FileLineReader.readLineByLine(reader, this);
		return builder.toString();
	}

	@Override
	public void read(String line) {
		builder.append(line);
		builder.append("\n");
	}
}
