package openblocks.client.renderer.tileentity.guide;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.MinecraftForgeClient;
import openblocks.common.tileentity.TileEntityBuilderGuide;
import openmods.renderer.DisplayListWrapper;
import org.lwjgl.opengl.GL11;

public class TileEntityBuilderGuideRenderer extends TileEntityGuideRenderer {

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

	private DisplayListWrapper cube = new DisplayListWrapper() {

		@Override
		public void compile() {
			final Tessellator tes = new Tessellator();
			tes.startDrawingQuads();

			tes.addVertex(-HALF_SIZE, -HALF_SIZE, -HALF_SIZE);
			tes.addVertex(-HALF_SIZE, +HALF_SIZE, -HALF_SIZE);
			tes.addVertex(+HALF_SIZE, +HALF_SIZE, -HALF_SIZE);
			tes.addVertex(+HALF_SIZE, -HALF_SIZE, -HALF_SIZE);

			tes.addVertex(-HALF_SIZE, -HALF_SIZE, +HALF_SIZE);
			tes.addVertex(+HALF_SIZE, -HALF_SIZE, +HALF_SIZE);
			tes.addVertex(+HALF_SIZE, +HALF_SIZE, +HALF_SIZE);
			tes.addVertex(-HALF_SIZE, +HALF_SIZE, +HALF_SIZE);

			tes.addVertex(-HALF_SIZE, -HALF_SIZE, -HALF_SIZE);
			tes.addVertex(-HALF_SIZE, -HALF_SIZE, +HALF_SIZE);
			tes.addVertex(-HALF_SIZE, +HALF_SIZE, +HALF_SIZE);
			tes.addVertex(-HALF_SIZE, +HALF_SIZE, -HALF_SIZE);

			tes.addVertex(+HALF_SIZE, -HALF_SIZE, -HALF_SIZE);
			tes.addVertex(+HALF_SIZE, +HALF_SIZE, -HALF_SIZE);
			tes.addVertex(+HALF_SIZE, +HALF_SIZE, +HALF_SIZE);
			tes.addVertex(+HALF_SIZE, -HALF_SIZE, +HALF_SIZE);

			tes.addVertex(-HALF_SIZE, -HALF_SIZE, -HALF_SIZE);
			tes.addVertex(+HALF_SIZE, -HALF_SIZE, -HALF_SIZE);
			tes.addVertex(+HALF_SIZE, -HALF_SIZE, +HALF_SIZE);
			tes.addVertex(-HALF_SIZE, -HALF_SIZE, +HALF_SIZE);

			tes.addVertex(-HALF_SIZE, +HALF_SIZE, -HALF_SIZE);
			tes.addVertex(-HALF_SIZE, +HALF_SIZE, +HALF_SIZE);
			tes.addVertex(+HALF_SIZE, +HALF_SIZE, +HALF_SIZE);
			tes.addVertex(+HALF_SIZE, +HALF_SIZE, -HALF_SIZE);

			tes.draw();
		}
	};

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTickTime) {
		if (MinecraftForgeClient.getRenderPass() == 0) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
			renderCubes(((TileEntityBuilderGuide)te).getTicks() + partialTickTime);
			GL11.glPopMatrix();
		} else {
			super.renderTileEntityAt(te, x, y, z, partialTickTime);
		}
	}

	private void renderCubes(float time) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3ub((byte)0x06, (byte)0x23, (byte)0x20);

		GL11.glPushMatrix();

		{
			float theta = 360 * MathHelper.sin(OMEGA_11 * time + DELTA_11);
			float phi = 360 * MathHelper.sin(OMEGA_12 * time + DELTA_12);
			drawCube(theta, phi);
		}

		GL11.glPopMatrix();
		GL11.glPushMatrix();

		{
			float theta = 360 * MathHelper.sin(OMEGA_21 * time + DELTA_21);
			float phi = 360 * MathHelper.sin(OMEGA_22 * time + DELTA_22);
			drawCube(theta, phi);
		}

		GL11.glPopMatrix();

		{
			float theta = 360 * MathHelper.sin(OMEGA_31 * time + DELTA_31);
			float phi = 360 * MathHelper.sin(OMEGA_32 * time + DELTA_32);
			drawCube(theta, phi);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	protected void drawCube(float theta, float phi) {
		GL11.glRotatef(theta, 0, 1, 0);
		GL11.glRotatef(phi, 0, 0, 1);
		GL11.glTranslatef(RADIUS, 0, 0);

		cube.render();
	}
}
