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

	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {

		Minecraft mc = FMLClientHandler.instance().getClient();
		if (type.contains(TickType.RENDER) && isBeingDragged && mc.theWorld != null && mc.currentScreen == null) {

			ARBShaderObjects.glUseProgramObjectARB(program);

		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

		Minecraft mc = FMLClientHandler.instance().getClient();
		if (type.contains(TickType.RENDER) && isBeingDragged && mc.theWorld != null && mc.currentScreen == null) {
			
			ARBShaderObjects.glUseProgramObjectARB(0);
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
