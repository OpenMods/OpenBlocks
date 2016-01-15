package openblocks.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelGrave;
import openblocks.common.tileentity.TileEntityGrave;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityGraveRenderer extends TileEntitySpecialRenderer<TileEntityGrave> {

	private ModelGrave model = new ModelGrave();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/grave.png");

	@Override
	public void renderTileEntityAt(TileEntityGrave target, double x, double y, double z, float partialTicks, int destroyStage) {
		float stoneWidth = 8 / 16f;

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		GL11.glRotatef(-BlockUtils.getRotationFromOrientation(target.getOrientation()), 0, 1, 0);
		bindTexture(texture);
		model.render(target, partialTicks);
		String username = target.getUsername();

		final FontRenderer renderer = getFontRenderer();
		if (renderer != null) {
			int stringWidth = renderer.getStringWidth(username);
			float textScale = stoneWidth / stringWidth;
			textScale = Math.min(textScale, 0.02f);

			if (target.isOnSoil()) {
				GL11.glTranslatef(-(textScale * stringWidth) / 2, 0.3f, 0.36f);
			} else {
				GL11.glTranslatef(-(textScale * stringWidth) / 2, 0.35f, 0.34f);
			}
			GL11.glScalef(textScale, textScale, textScale);

			GL11.glRotatef(-5F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(2F, 0F, 0.0F, 1.0F);
			GL11.glDepthMask(false);
			renderer.drawString(username, 0, 0, 0);
			GL11.glDepthMask(true);
		}
		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}

}
