package openblocks.client.renderer.tileentity.guide;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

public class ShaderHelper {
	/**
	 * Class for shader functions, calling ARB methods if GL20 is not supported
	 * A lot of this is already implemented in OpenGlHelper, but it misses some
	 * methods and is confusing in general.
	 */

	static {
		initialize();
	}

	public static IShaderMethods methods;

	static void initialize() {
		ContextCapabilities caps = GLContext.getCapabilities();

		if (GL20ShaderMethods.isSupported(caps))
			methods = new GL20ShaderMethods();
		else if (ARBShaderMethods.isSupported(caps))
			methods = new ARBShaderMethods();
	}

	public static boolean isSupported() {
		return methods != null;
	}

	public static IShaderMethods methods() {
		return methods;
	}

	public static interface IShaderMethods {
		public int glCreateProgram();

		public int glCreateShader(int type);

		public void glAttachShader(int program, int shader);

		public void glDetachShader(int program, int shader);

		public void glLinkProgram(int program);

		public void glValidateProgram(int program);

		public void glDeleteProgram(int program);

		public void glDeleteShader(int shader);

		public int glGetProgrami(int shader, int parameter);

		public int glGetShaderi(int program, int parameter);

		public String getProgramLogInfo(int program);

		public String getShaderLogInfo(int shader);

		public void glUseProgram(int program);

		public void glShaderSource(int shader, String shaderSource);

		public void glCompileShader(int shader);

		public int glGetUniformLocation(int program, String uniform);

		public void glUniform1i(int loc, int val);

		public void glUniform1f(int loc, float val);

		public void glUniform3f(int loc, float x, float y, float z);

		public int glGetAttribLocation(int program, String attrib);

		public void glEnableVertexAttribArray(int index);

		public void glDisableVertexAttribArray(int index);

		public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset);
	}

	private static class GL20ShaderMethods implements IShaderMethods {
		public static boolean isSupported(ContextCapabilities caps) {
			return caps.OpenGL20;
		}

		@Override
		public int glCreateProgram() {
			return GL20.glCreateProgram();
		}

		@Override
		public int glCreateShader(int type) {
			return GL20.glCreateShader(type);
		}

		@Override
		public void glAttachShader(int program, int shader) {
			GL20.glAttachShader(program, shader);
		}

		@Override
		public void glDetachShader(int program, int shader) {
			GL20.glDetachShader(program, shader);
		}

		@Override
		public void glLinkProgram(int program) {
			GL20.glLinkProgram(program);
		}

		@Override
		public void glValidateProgram(int program) {
			GL20.glValidateProgram(program);
		}

		@Override
		public void glDeleteProgram(int program) {
			GL20.glDeleteProgram(program);
		}

		@Override
		public void glDeleteShader(int shader) {
			GL20.glDeleteShader(shader);
		}

		@Override
		public int glGetProgrami(int program, int parameter) {
			return GL20.glGetProgrami(program, parameter);
		}

		@Override
		public int glGetShaderi(int shader, int parameter) {
			return GL20.glGetShaderi(shader, parameter);
		}

		@Override
		public String getProgramLogInfo(int program) {
			return GL20.glGetProgramInfoLog(program, glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH));
		}

		@Override
		public String getShaderLogInfo(int shader) {
			return GL20.glGetShaderInfoLog(shader, glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
		}

		@Override
		public void glUseProgram(int program) {
			GL20.glUseProgram(program);
		}

		@Override
		public void glShaderSource(int shader, String shaderSource) {
			GL20.glShaderSource(shader, shaderSource);
		}

		@Override
		public void glCompileShader(int shader) {
			GL20.glCompileShader(shader);
		}

		@Override
		public int glGetUniformLocation(int program, String uniform) {
			return GL20.glGetUniformLocation(program, uniform);
		}

		@Override
		public void glUniform1i(int loc, int val) {
			GL20.glUniform1i(loc, val);
		}

		@Override
		public void glUniform1f(int loc, float val) {
			GL20.glUniform1f(loc, val);
		}

		@Override
		public void glUniform3f(int loc, float x, float y, float z) {
			GL20.glUniform3f(loc, x, y, z);
		}

		@Override
		public int glGetAttribLocation(int program, String attrib) {
			return GL20.glGetAttribLocation(program, attrib);
		}

		@Override
		public void glEnableVertexAttribArray(int index) {
			GL20.glEnableVertexAttribArray(index);
		}

		@Override
		public void glDisableVertexAttribArray(int index) {
			GL20.glDisableVertexAttribArray(index);
		}

		@Override
		public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
			GL20.glVertexAttribPointer(index, size, type, normalized, stride, offset);
		}
	}

	private static class ARBShaderMethods implements IShaderMethods {
		public static boolean isSupported(ContextCapabilities caps) {
			return caps.GL_ARB_shader_objects && caps.GL_ARB_vertex_shader && caps.GL_ARB_fragment_shader;
		}

		@Override
		public int glCreateProgram() {
			return ARBShaderObjects.glCreateProgramObjectARB();
		}

		@Override
		public int glCreateShader(int type) {
			return ARBShaderObjects.glCreateShaderObjectARB(type);
		}

		@Override
		public void glAttachShader(int program, int shader) {
			ARBShaderObjects.glAttachObjectARB(program, shader);
		}

		@Override
		public void glDetachShader(int program, int shader) {
			ARBShaderObjects.glDetachObjectARB(program, shader);
		}

		@Override
		public void glLinkProgram(int program) {
			ARBShaderObjects.glLinkProgramARB(program);
		}

		@Override
		public void glValidateProgram(int program) {
			ARBShaderObjects.glValidateProgramARB(program);
		}

		@Override
		public void glDeleteProgram(int program) {
			ARBShaderObjects.glDeleteObjectARB(program);
		}

		@Override
		public void glDeleteShader(int shader) {
			ARBShaderObjects.glDeleteObjectARB(shader);
		}

		@Override
		public int glGetProgrami(int program, int parameter) {
			return ARBShaderObjects.glGetObjectParameteriARB(program, parameter);
		}

		@Override
		public int glGetShaderi(int shader, int parameter) {
			return ARBShaderObjects.glGetObjectParameteriARB(shader, parameter);
		}

		@Override
		public String getProgramLogInfo(int program) {
			return ARBShaderObjects.glGetInfoLogARB(program, glGetProgrami(program, GL20.GL_INFO_LOG_LENGTH));
		}

		@Override
		public String getShaderLogInfo(int shader) {
			return ARBShaderObjects.glGetInfoLogARB(shader, glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
		}

		@Override
		public void glUseProgram(int program) {
			ARBShaderObjects.glUseProgramObjectARB(program);
		}

		@Override
		public void glShaderSource(int shader, String shaderSource) {
			ARBShaderObjects.glShaderSourceARB(shader, shaderSource);
		}

		@Override
		public void glCompileShader(int shader) {
			ARBShaderObjects.glCompileShaderARB(shader);
		}

		@Override
		public int glGetUniformLocation(int program, String uniform) {
			return ARBShaderObjects.glGetUniformLocationARB(program, uniform);
		}

		@Override
		public void glUniform1i(int loc, int val) {
			ARBShaderObjects.glUniform1iARB(loc, val);
		}

		@Override
		public void glUniform1f(int loc, float val) {
			ARBShaderObjects.glUniform1fARB(loc, val);
		}

		@Override
		public void glUniform3f(int loc, float x, float y, float z) {
			ARBShaderObjects.glUniform3fARB(loc, x, y, z);
		}

		@Override
		public int glGetAttribLocation(int program, String attrib) {
			return ARBVertexShader.glGetAttribLocationARB(program, attrib);
		}

		@Override
		public void glEnableVertexAttribArray(int index) {
			ARBVertexShader.glEnableVertexAttribArrayARB(index);
		}

		@Override
		public void glDisableVertexAttribArray(int index) {
			ARBVertexShader.glDisableVertexAttribArrayARB(index);
		}

		@Override
		public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
			ARBVertexShader.glVertexAttribPointerARB(index, size, type, normalized, stride, offset);
		}
	}
}
