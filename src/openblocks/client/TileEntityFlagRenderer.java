package openblocks.client;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
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
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0f, (float) z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		GL11.glRotatef(flag.getRotation(), 0, 1, 0);
		this.bindTextureByName("/mods/openblocks/textures/models/flag.png");
		model.render(tileentity, f);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glRotatef(flag.getRotation(), 0, 1, 0);
		renderFlag(flag.getIcon());
		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

	public void renderFlag(Icon icon) {
		Tessellator tessellator = Tessellator.instance;
		float f4 = icon.getMinU();
		float f5 = icon.getMaxU();
		float f6 = icon.getMinV();
		float f7 = icon.getMaxV();
		GL11.glPushMatrix();
		float f12 = 0.0625F;
		GL11.glRotatef(90, 0, 1, 0);
		GL11.glScalef(0.5f,0.5f,0.5f);
		this.bindTextureByName("/mods/openblocks/textures/blocks/flag.png");
		ItemRenderer.renderItemIn2D(tessellator, f5, f6, f4, f7,
				icon.getSheetWidth(), icon.getSheetHeight(), f12);
		GL11.glPopMatrix();
	}

}
