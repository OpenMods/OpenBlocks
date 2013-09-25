package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import openblocks.client.model.ModelFlag;
import openblocks.common.tileentity.TileEntityFlag;

import org.lwjgl.opengl.GL11;

public class TileEntityFlagRenderer extends TileEntitySpecialRenderer {

	ModelFlag model = new ModelFlag();

	private static final ResourceLocation textureFlagpole = new ResourceLocation(
			"openblocks", "textures/models/flagpole.png");
	private static final ResourceLocation textureFlag = new ResourceLocation(
			"openblocks", "textures/models/flag.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {

		TileEntityFlag flag = (TileEntityFlag) tileentity;
		if (flag == null)
			return;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
		GL11.glPushMatrix();
		GL11.glRotatef(-flag.getAngle(), 0, 1, 0);
		if (flag.getSurfaceDirection() != ForgeDirection.DOWN) {
			GL11.glRotatef(45, 1f, 0f, 0f);
			GL11.glTranslatef(0f, -0.2f, -0.7f);
		}
		// GL11.glTranslatef(0, 0, -0.3F);
		bindTexture(textureFlagpole);
		model.render(tileentity, f);
		GL11.glPushMatrix();
		GL11.glRotatef(-90, 0, 1, 0);
		renderFlag(flag);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

	public void renderFlag(TileEntityFlag flag) {
		Tessellator tessellator = Tessellator.instance;
		bindTexture(textureFlag);
		int color = flag.getColor();

		float r = (float) ((color >> 16) & 0xFF) / 255;
		float g = (float) ((color >> 8) & 0xFF) / 255;
		float b = (float) (color & 0xFF) / 255;
		float par1 = 0;
		float par2 = 0;
		float par3 = 1f;
		float par4 = 1f;
		float par7 = 0.001f;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(r, g, b, 1);
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, par1, par4);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, par3, par4);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, par3, par2);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, par1, par2);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_F(r, g, b);
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0F - par7, par1, par2);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0F - par7, par3, par2);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0F - par7, par3, par4);
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0F - par7, par1, par4);
		tessellator.draw();
	}

}
