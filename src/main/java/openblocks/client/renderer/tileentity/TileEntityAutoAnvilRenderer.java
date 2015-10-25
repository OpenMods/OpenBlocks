package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelAutoAnvil;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityAutoAnvilRenderer extends TileEntitySpecialRenderer {

	private ModelAutoAnvil model = new ModelAutoAnvil();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/autoanvil.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		TileEntityAutoAnvil autoanvil = (TileEntityAutoAnvil)tileentity;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-BlockUtils.getRotationFromOrientation(autoanvil.getOrientation()), 0, 1, 0);
		GL11.glPushMatrix();
		bindTexture(texture);
		model.render();
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}
}
