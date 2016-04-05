package openblocks.client.renderer.tileentity.guide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.OpenGLException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import openmods.utils.io.FileLineReader;
import openmods.utils.io.ILineReadMethod;

public class ShaderProgram {
	protected ArrayList<Integer> shaders;
	protected int program;
	protected boolean compiled;

	protected HashMap<String, Integer> uniforms;

	public ShaderProgram() {
		shaders = new ArrayList<Integer>();
		compiled = false;
		uniforms = new HashMap<String, Integer>();
	}

	public void bind() {
		if (compiled)
			ShaderHelper.methods().glUseProgram(program);
	}

	public void release() {
		ShaderHelper.methods().glUseProgram(0);
	}

	public void addShader(ResourceLocation source, int type) throws Exception {
		if (compiled)
			throw new UnsupportedOperationException("Shader already compiled");

		int shader = createShader(source, type);
		if (shader != 0)
			shaders.add(shader);
	}

	public void compile() {
		if (compiled)
			throw new UnsupportedOperationException("Shader already compiled");

		program = ShaderHelper.methods().glCreateProgram();
		if (program == 0)
			throw new OpenGLException("Error creating program object");

		for (Integer shader : shaders)
			ShaderHelper.methods().glAttachShader(program, shader);
		ShaderHelper.methods().glLinkProgram(program);
		if (ShaderHelper.methods().glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE)
			throw new OpenGLException("Shader link error: " + ShaderHelper.methods().getProgramLogInfo(program));

		for (Integer shader : shaders)
			ShaderHelper.methods().glDetachShader(program, shader);
		ShaderHelper.methods().glValidateProgram(program);
		if (ShaderHelper.methods().glGetProgrami(program, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE)
			throw new OpenGLException("Shader validate error: " + ShaderHelper.methods().getProgramLogInfo(program));

		compiled = true;
	}

	public void destroy() {
		for (Integer shader : shaders)
			ShaderHelper.methods().glDeleteShader(shader);
		ShaderHelper.methods().glUseProgram(0);
		ShaderHelper.methods().glDeleteProgram(program);
	}

	private int createShader(ResourceLocation source, int type) throws Exception {
		int shader = 0;
		try {
			shader = ShaderHelper.methods().glCreateShader(type);

			if (shader == 0)
				throw new OpenGLException("Error creating shader object");

			ShaderHelper.methods().glShaderSource(shader, readShaderSource(source));
			ShaderHelper.methods().glCompileShader(shader);
			if (ShaderHelper.methods().glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE)
				throw new OpenGLException("Shader compile error: " + ShaderHelper.methods().getShaderLogInfo(shader));

			return shader;
		} catch (Exception exc) {
			ShaderHelper.methods().glDeleteShader(shader);
			throw exc;
		}
	}

	private String readShaderSource(ResourceLocation source) throws IOException {
		InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(source).getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		final StringBuilder out = new StringBuilder();

		FileLineReader.readLineByLine(reader, new ILineReadMethod() {
			@Override
			public void read(String line) {
				out.append(line).append('\n');
			}
		});

		return out.toString();
	}

	public int getUniformLocation(String uniform) {
		Integer loc = uniforms.get(uniform);
		if (loc == null) {
			loc = ShaderHelper.methods().glGetUniformLocation(program, uniform);
			uniforms.put(uniform, loc);
		}
		return loc;
	}

	public void uniform1i(String name, int val) {
		ShaderHelper.methods().glUniform1i(getUniformLocation(name), val);
	}

	public void uniform1f(String name, float val) {
		ShaderHelper.methods().glUniform1f(getUniformLocation(name), val);
	}

	public void uniform3f(String name, float x, float y, float z) {
		ShaderHelper.methods().glUniform3f(getUniformLocation(name), x, y, z);
	}

	public int getProgram() {
		return program;
	}

	public void instanceAttributePointer(String attrib, int size, int type, boolean normalized, int stride, long offset) {
		instanceAttributePointer(ShaderHelper.methods().glGetAttribLocation(program, attrib), size, type, normalized, stride, offset);
	}

	public void instanceAttributePointer(int index, int size, int type, boolean normalized, int stride, long offset) {
		attributePointer(index, size, type, normalized, stride, offset);
		ArraysHelper.methods().glVertexAttribDivisor(index, 1);
	}

	public void attributePointer(String attrib, int size, int type, boolean normalized, int stride, long offset) {
		attributePointer(ShaderHelper.methods().glGetAttribLocation(program, attrib), size, type, normalized, stride, offset);
	}

	public void attributePointer(int index, int size, int type, boolean normalized, int stride, long offset) {
		ShaderHelper.methods().glVertexAttribPointer(index, size, type, normalized, stride, offset);
		ShaderHelper.methods().glEnableVertexAttribArray(index);
	}
}
