package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.client.model.ModelFlag;
import openblocks.common.tileentity.TileEntityFlag;
import openmods.renderer.DisplayListWrapper;
import openmods.utils.ColorUtils.RGB;

import org.lwjgl.opengl.GL11;

public class TileEntityFlagRenderer extends TileEntitySpecialRenderer {

	private static final ModelFlag POLE = new ModelFlag();

	private static final DisplayListWrapper FLAG = new DisplayListWrapper() {
		@Override
		public void compile() {
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setNormal(0, 0, 1);
			tessellator.addVertexWithUV(0, 0, 0, 0, 1);
			tessellator.addVertexWithUV(1, 0, 0, 1, 1);
			tessellator.addVertexWithUV(1, 1, 0, 1, 0);
			tessellator.addVertexWithUV(0, 1, 0, 0, 0);

			tessellator.setNormal(0, 0, -1);
			tessellator.addVertexWithUV(0, 1, 0 - 0.001, 0, 0);
			tessellator.addVertexWithUV(1, 1, 0 - 0.001, 1, 0);
			tessellator.addVertexWithUV(1, 0, 0 - 0.001, 1, 1);
			tessellator.addVertexWithUV(0, 0, 0 - 0.001, 0, 1);
			tessellator.draw();
		}
	};

	private static final ResourceLocation textureFlagpole = new ResourceLocation("openblocks", "textures/models/flagpole.png");
	private static final ResourceLocation textureFlag = new ResourceLocation("openblocks", "textures/models/flag.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		if (!(tileentity instanceof TileEntityFlag)) return;
		TileEntityFlag flag = (TileEntityFlag)tileentity;

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y, z + 0.5);
		GL11.glRotatef(-flag.getAngle(), 0, 1, 0);
		if (flag.getOrientation().down() != ForgeDirection.DOWN) {
			GL11.glRotatef(45, 1f, 0f, 0f);
			GL11.glTranslatef(0f, -0.2f, -0.7f);
		}
		bindTexture(textureFlagpole);
		POLE.render(f);

		GL11.glRotatef(-90, 0, 1, 0);

		RGB color = flag.getColor();
		GL11.glColor3ub((byte)color.r, (byte)color.g, (byte)color.b);
		bindTexture(textureFlag);
		FLAG.render();

		GL11.glPopMatrix();
	}

}
