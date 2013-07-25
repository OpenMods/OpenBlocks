package openblocks.client;

import openblocks.common.tileentity.TileEntityGrave;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGraveRenderer extends TileEntitySpecialRenderer {

	private ModelGrave model = new ModelGrave();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) {

		TileEntityGrave target = (TileEntityGrave) tileentity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.0f, (float) z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		GL11.glRotatef(
				-BlockUtils.getRotationFromDirection(target.getRotation()), 0,
				1, 0);
		this.bindTextureByName("/mods/openblocks/textures/models/grave.png");
		model.render(tileentity, f);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
