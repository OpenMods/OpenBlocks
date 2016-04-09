package openblocks.client.renderer.tileentity.guide;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBBufferObject;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GLContext;

public class BufferHelper {
	static {
		initialize();
	}

	public static IBufferMethods methods;

	static void initialize() {
		ContextCapabilities caps = GLContext.getCapabilities();
		if (GLBufferMethods.isSupported(caps))
			methods = new GLBufferMethods();
		else if (ARBBufferMethods.isSupported(caps))
			methods = new ARBBufferMethods();
	}

	public static boolean isSupported() {
		return methods != null;
	}

	public static IBufferMethods methods() {
		return methods;
	}

	public static interface IBufferMethods {
		public int glGenBuffers();

		public void glBindBuffer(int target, int buffer);

		public void glBufferData(int target, ByteBuffer data, int usage);

		public void glDeleteBuffers(int buffer);
	}

	private static class GLBufferMethods implements IBufferMethods {

		public static boolean isSupported(ContextCapabilities caps) {
			return caps.OpenGL15;
		}

		@Override
		public int glGenBuffers() {
			return GL15.glGenBuffers();
		}

		@Override
		public void glBindBuffer(int target, int buffer) {
			GL15.glBindBuffer(target, buffer);
		}

		@Override
		public void glBufferData(int target, ByteBuffer data, int usage) {
			GL15.glBufferData(target, data, usage);
		}

		@Override
		public void glDeleteBuffers(int buffer) {
			GL15.glDeleteBuffers(buffer);
		}
	}
	
	private static class ARBBufferMethods implements IBufferMethods {

		public static boolean isSupported(ContextCapabilities caps) {
			return caps.OpenGL15;
		}

		@Override
		public int glGenBuffers() {
			return ARBBufferObject.glGenBuffersARB();
		}

		@Override
		public void glBindBuffer(int target, int buffer) {
			ARBBufferObject.glBindBufferARB(target, buffer);
		}

		@Override
		public void glBufferData(int target, ByteBuffer data, int usage) {
			ARBBufferObject.glBufferDataARB(target, data, usage);
		}

		@Override
		public void glDeleteBuffers(int buffer) {
			ARBBufferObject.glDeleteBuffersARB(buffer);
		}
	}
}