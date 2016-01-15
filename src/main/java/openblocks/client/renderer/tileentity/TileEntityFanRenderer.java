package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelFan;
import openblocks.common.tileentity.TileEntityFan;

import org.lwjgl.opengl.GL11;

public class TileEntityFanRenderer extends TileEntitySpecialRenderer<TileEntityFan> {

	private ModelFan model = new ModelFan();
	private static final ResourceLocation texture = OpenBlocks.location("textures/models/fan.png");

	@Override
	public void renderTileEntityAt(TileEntityFan fan, double x, double y, double z, float partialTick, int destroyProgress) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

		GL11.glRotatef(fan.getAngle(), 0F, 1.0F, 0.0F);
		bindTexture(texture);
		model.render(fan, partialTick, fan.getBladeRotation(partialTick));
		GL11.glPopMatrix();
	}

}
