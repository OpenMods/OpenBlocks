package openblocks.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import openblocks.client.model.ModelOreCrusher;
import openblocks.common.tileentity.TileEntityOreCrusher;
import openblocks.utils.BlockUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityOreCrusherRenderer extends
		TileEntitySpecialRenderer {

	private ModelOreCrusher model = new ModelOreCrusher();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/ore_crusher.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityOreCrusher crusher = (TileEntityOreCrusher)tileentity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.5f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		GL11.glRotatef(-BlockUtils.getRotationFromDirection(crusher.getRotation()), 0, 1, 0);
		bindTexture(texture);
		model.render(crusher, f);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
