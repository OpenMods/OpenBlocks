package openblocks.client.renderer.tileentity.guide;

import java.nio.ByteBuffer;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import openblocks.common.tileentity.TileEntityBuilderGuide;

public class TileEntityBuilderGuideRenderer extends TileEntityGuideRenderer<TileEntityBuilderGuide> {

	private static final float RADIUS = 0.4f;

	private static final double HALF_SIZE = 0.03;

	private static final float OMEGA_11 = 0.0413f;
	private static final float DELTA_11 = 0.5423f;

	private static final float OMEGA_12 = 0.0765f;
	private static final float DELTA_12 = 0.4241f;

	private static final float OMEGA_21 = 0.0543f;
	private static final float DELTA_21 = 0.1295f;

	private static final float OMEGA_22 = 0.0914f;
	private static final float DELTA_22 = 0.6532f;

	private static final float OMEGA_31 = 0.0624f;
	private static final float DELTA_31 = 0.6243f;

	private static final float OMEGA_32 = 0.0351f;
	private static final float DELTA_32 = 0.7635f;

	public TileEntityBuilderGuideRenderer(GuideModelHolder holder) {
		super(holder);
	}

	private static final Vector3f R = new Vector3f(RADIUS - 0.5f, -0.5f, -0.5f);

	@Override
	public void renderTileEntityFast(TileEntityBuilderGuide te, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer) {
		super.renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, buffer);

		buffer.setTranslation(x + 0.5, y + 0.5, z + 0.5);
		renderCubes(buffer, (te.getTicks() + partialTicks) / 2);
	}

	private void renderCubes(BufferBuilder buffer, float time) {
		final Matrix4f trans = new Matrix4f();

		createTransformation(trans, MathHelper.sin(OMEGA_11 * time + DELTA_11), MathHelper.sin(OMEGA_12 * time + DELTA_12));
		drawCube(buffer, trans);

		createTransformation(trans, MathHelper.sin(OMEGA_21 * time + DELTA_21), MathHelper.sin(OMEGA_22 * time + DELTA_22));
		drawCube(buffer, trans);

		createTransformation(trans, MathHelper.sin(OMEGA_31 * time + DELTA_31), MathHelper.sin(OMEGA_32 * time + DELTA_32));
		drawCube(buffer, trans);

	}

	private void createTransformation(Matrix4f result, float theta, float phi) {
		result.setIdentity();

		final Matrix4f tmp = new Matrix4f();
		tmp.rotY(theta * (float)Math.PI * 2);
		result.mul(tmp);

		tmp.rotZ(phi * (float)Math.PI * 2);
		result.mul(tmp);

		tmp.set(R);
		result.mul(tmp);
	}

	private void drawCube(BufferBuilder buffer, Matrix4f trans) {
		final ByteBuffer quads = holder.getBitQuads();
		while (quads.hasRemaining()) {
			final float x = quads.getFloat();
			final float y = quads.getFloat();
			final float z = quads.getFloat();

			final int color = quads.getInt();
			final int red = (color >> 16) & 0xFF;
			final int green = (color >> 8) & 0xFF;
			final int blue = (color >> 0) & 0xFF;

			final float u = quads.getFloat();
			final float v = quads.getFloat();

			quads.getInt(); // brightness

			final Vector4f pos = new Vector4f(x, y, z, 1.0f);
			trans.transform(pos);
			buffer.pos(pos.x, pos.y, pos.z).color(red, green, blue, 0xFF).tex(u, v).lightmap(0xf0, 0xf0).endVertex();
		}
	}
}
