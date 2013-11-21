package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelTarget;
import openblocks.common.tileentity.TileEntityTarget;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityTargetRenderer extends TileEntitySpecialRenderer {

	private ModelTarget model = new ModelTarget();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/target.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		TileEntityTarget target = (TileEntityTarget)tileentity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		GL11.glRotatef(-BlockUtils.getRotationFromDirection(target.getRotation()), 0, 1, 0);
		bindTexture(texture);
		model.render(tileentity, f);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
