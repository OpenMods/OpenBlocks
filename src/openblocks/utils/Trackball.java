package openblocks.utils;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Preconditions;

public class Trackball {
	public static class TrackballWrapper {
		private final Trackball target = new Trackball();
		private final float radius;
		private final int mouseButton;
		private boolean isDraging;

		public TrackballWrapper(int mouseButton, int radiusPx) {
			this.mouseButton = mouseButton;
			this.radius = radiusPx;
		}

		public void update(int mouseX, int mouseY) {
			float mx = mouseX / radius;
			float my = mouseY / radius;

			boolean buttonState = Mouse.isButtonDown(mouseButton);
			if (!isDraging && buttonState) {
				isDraging = true;
				target.startDrag(mx, my);
			} else if (isDraging && !buttonState) {
				isDraging = false;
				target.endDrag(mx, my);
			}

			target.applyTransform(mx, my, isDraging);
		}
	}

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	private Vector3f dragStart;
	private Matrix4f lastTransform;

	public Trackball() {
		lastTransform = new Matrix4f();
	}

	private static Vector3f calculateSpherePoint(float x, float y) {
		Vector3f result = new Vector3f(x, y, 0);

		float sqrZ = 1 - Vector3f.dot(result, result);

		if (sqrZ > 0) result.z = (float)Math.sqrt(sqrZ);
		else result.normalise();

		return result;
	}

	private Matrix4f getTransform(float mouseX, float mouseY) {
		Preconditions.checkNotNull(dragStart, "Draging not started");
		Vector3f current = calculateSpherePoint(mouseX, mouseY);

		float dot = Vector3f.dot(dragStart, current);
		if (Math.abs(dot - 1) < 0.0001) return lastTransform;

		Vector3f axis = Vector3f.cross(dragStart, current, null);
		axis.normalise();

		float angle = 2 * (float)(Math.acos(dot));

		Matrix4f rotation = new Matrix4f();
		rotation.rotate(angle, axis);
		return Matrix4f.mul(rotation, lastTransform, null);

	}

	public void applyTransform(float mouseX, float mouseY, boolean isDraging) {
		Matrix4f transform;
		if (isDraging) transform = getTransform(mouseX, mouseY);
		else transform = lastTransform;

		matrixBuffer.clear();
		transform.store(matrixBuffer);
		matrixBuffer.flip();

		GL11.glMultMatrix(matrixBuffer);
	}

	public void startDrag(float mouseX, float mouseY) {
		dragStart = calculateSpherePoint(mouseX, mouseY);
	}

	public void endDrag(float mouseX, float mouseY) {
		lastTransform = getTransform(mouseX, mouseY);
	}
}
