package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelFlag;
import openblocks.common.tileentity.TileEntityFlag;
import openmods.colors.RGB;
import openmods.renderer.DisplayListWrapper;

import org.lwjgl.opengl.GL11;

public class TileEntityFlagRenderer extends TileEntitySpecialRenderer<TileEntityFlag> {

	private static final ModelFlag POLE = new ModelFlag();

	private static final DisplayListWrapper FLAG = new DisplayListWrapper() {
		@Override
		public void compile() {
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer wr = tessellator.getWorldRenderer();
			wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

			wr.pos(0, 0, 0).tex(0, 1).normal(0, 0, 1);
			wr.pos(1, 0, 0).tex(1, 1).normal(0, 0, 1);
			wr.pos(1, 1, 0).tex(1, 0).normal(0, 0, 1);
			wr.pos(0, 1, 0).tex(0, 0).normal(0, 0, 1);

			wr.pos(0, 1, 0 - 0.001).tex(0, 0).normal(0, 0, -1);
			wr.pos(1, 1, 0 - 0.001).tex(1, 0).normal(0, 0, -1);
			wr.pos(1, 0, 0 - 0.001).tex(1, 1).normal(0, 0, -1);
			wr.pos(0, 0, 0 - 0.001).tex(0, 1).normal(0, 0, -1);
			tessellator.draw();
		}
	};

	private static final ResourceLocation textureFlagpole = new ResourceLocation("openblocks", "textures/models/flagpole.png");
	private static final ResourceLocation textureFlag = new ResourceLocation("openblocks", "textures/models/flag.png");

	@Override
	public void renderTileEntityAt(TileEntityFlag flag, double x, double y, double z, float partialTicks, int destroyStage) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glRotatef(-flag.getAngle(), 0, 1, 0);
		if (flag.getOrientation().down() != EnumFacing.DOWN) {
			GL11.glRotatef(45, 1f, 0f, 0f);
			GL11.glTranslatef(0f, -0.2f, -0.7f);
		}
		bindTexture(textureFlagpole);
		POLE.render();

		GL11.glRotatef(-90, 0, 1, 0);

		RGB color = flag.getColor();
		GL11.glColor3ub((byte)color.r, (byte)color.g, (byte)color.b);
		bindTexture(textureFlag);
		FLAG.render();

		GL11.glPopMatrix();
	}

}
