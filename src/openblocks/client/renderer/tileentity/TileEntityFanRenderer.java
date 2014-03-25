package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelFan;
import openblocks.common.tileentity.TileEntityFan;

import org.lwjgl.opengl.GL11;

public class TileEntityFanRenderer extends TileEntitySpecialRenderer {

	private ModelFan model = new ModelFan();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/fan.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float partialTickTime) {
		GL11.glPushMatrix();
		TileEntityFan fan = (TileEntityFan)tileentity;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

		GL11.glRotatef(fan.getAngle(), 0F, 1.0F, 0.0F);
		GL11.glPushMatrix();
		bindTexture(texture);
		model.render(tileentity, partialTickTime, fan.getBladeRotation(partialTickTime));
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
