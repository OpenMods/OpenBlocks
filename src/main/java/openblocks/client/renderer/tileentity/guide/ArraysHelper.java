package openblocks.client.renderer.tileentity.guide;

import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.ARBVertexArrayObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;

public class ArraysHelper {
	static {
		initialize();
	}

	public static IArrayMethods methods;

	static void initialize() {
		ContextCapabilities caps = GLContext.getCapabilities();
		if (GLArrayMethods.isSupported(caps))
			methods = new GLArrayMethods();
		else if (ARBArrayMethods.isSupported(caps))
			methods = new ARBArrayMethods();
	}

	public static boolean isSupported() {
		return methods != null;
	}

	public static IArrayMethods methods() {
		return methods;
	}

	public static interface IArrayMethods {
		public int glGenVertexArrays();

		public void glBindVertexArray(int array);

		public void glVertexAttribDivisor(int index, int divisor);

		public void glDrawArraysInstanced(int mode, int first, int count, int primcount);
	}

	private static class GLArrayMethods implements IArrayMethods {

		public static boolean isSupported(ContextCapabilities caps) {
			return caps.OpenGL33;
		}

		@Override
		public int glGenVertexArrays() {
			return GL30.glGenVertexArrays();
		}

		@Override
		public void glBindVertexArray(int array) {
			GL30.glBindVertexArray(array);
		}

		@Override
		public void glVertexAttribDivisor(int index, int divisor) {
			GL33.glVertexAttribDivisor(index, divisor);
		}

		@Override
		public void glDrawArraysInstanced(int mode, int first, int count, int primcount) {
			GL31.glDrawArraysInstanced(mode, first, count, primcount);
		}
	}

	private static class ARBArrayMethods implements IArrayMethods {

		public static boolean isSupported(ContextCapabilities caps) {
			return caps.GL_ARB_instanced_arrays && caps.GL_ARB_vertex_array_object;
		}

		@Override
		public int glGenVertexArrays() {
			return ARBVertexArrayObject.glGenVertexArrays();
		}

		@Override
		public void glBindVertexArray(int array) {
			ARBVertexArrayObject.glBindVertexArray(array);
		}

		@Override
		public void glVertexAttribDivisor(int index, int divisor) {
			ARBInstancedArrays.glVertexAttribDivisorARB(index, divisor);
		}

		@Override
		public void glDrawArraysInstanced(int mode, int first, int count, int primcount) {
			ARBDrawInstanced.glDrawArraysInstancedARB(mode, first, count, primcount);
		}
	}
}
