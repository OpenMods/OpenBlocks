package openblocks.client.renderer.tileentity.guide;

import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;

public class ArraysHelper {
	
	static {
		initialize();
	}

	private static boolean GL33Supported;
	private static boolean GL31Supported;
	private static boolean ARBInstancedArraysSupported;
	
	static void initialize() {
		ContextCapabilities caps = GLContext.getCapabilities();
		GL33Supported = caps.OpenGL33;
		GL31Supported = caps.OpenGL31;
		ARBInstancedArraysSupported = caps.GL_ARB_instanced_arrays;
	}
	
	public static boolean supported()
	{
		return (GL33Supported || ARBInstancedArraysSupported) && GL31Supported;
	}
	
	public static void glDrawArraysInstanced(int mode, int first, int count, int primcount)
	{
		if (GL31Supported)
			GL31.glDrawArraysInstanced(mode, first, count, primcount);
		else
			ARBDrawInstanced.glDrawArraysInstancedARB(mode, first, count, primcount);
	}
	
	public static void glVertexDivisor(int index, int divisor) {
		if (GL33Supported)
			GL33.glVertexAttribDivisor(index, divisor);
		else if(ARBInstancedArraysSupported)
			ARBInstancedArrays.glVertexAttribDivisorARB(index, divisor);
	}
}
