package openblocks.client;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import openblocks.common.tileentity.TileEntityFlag;

import org.lwjgl.opengl.GL11;

public class TileEntityFlagRenderer extends TileEntitySpecialRenderer {

	ModelFlag model = new ModelFlag();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {

		TileEntityFlag flag = (TileEntityFlag) tileentity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y, (float) z + 0.5F);
		GL11.glPushMatrix();
		GL11.glRotatef(-flag.getRotation(), 0, 1, 0);
		GL11.glTranslatef(0, 0, -0.3F);
		this.bindTextureByName("/mods/openblocks/textures/models/flagpole.png");
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
		this.bindTextureByName("/mods/openblocks/textures/models/flag.png");
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
		tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)par1, (double)par4);
		tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, (double)par3, (double)par4);
		tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, (double)par3, (double)par2);
		tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, (double)par1, (double)par2);
		tessellator.draw();
		tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(r, g, b);
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(0.0D, 1.0D, (double)(0.0F - par7), (double)par1, (double)par2);
		tessellator.addVertexWithUV(1.0D, 1.0D, (double)(0.0F - par7), (double)par3, (double)par2);
		tessellator.addVertexWithUV(1.0D, 0.0D, (double)(0.0F - par7), (double)par3, (double)par4);
		tessellator.addVertexWithUV(0.0D, 0.0D, (double)(0.0F - par7), (double)par1, (double)par4);
		tessellator.draw();
	}

}
