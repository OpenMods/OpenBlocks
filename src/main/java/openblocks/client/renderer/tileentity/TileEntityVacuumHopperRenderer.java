package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelVacuumHopper;
import openblocks.common.tileentity.TileEntityVacuumHopper;

import org.lwjgl.opengl.GL11;

public class TileEntityVacuumHopperRenderer extends TileEntitySpecialRenderer<TileEntityVacuumHopper> {

	private ModelVacuumHopper model = new ModelVacuumHopper();

	private static final ResourceLocation texture = OpenBlocks.location("textures/models/vacuumhopper.png");

	@Override
	public void renderTileEntityAt(TileEntityVacuumHopper hopper, double x, double y, double z, float partialTick, int destroyProgress) {
		GL11.glPushMatrix();
		bindTexture(texture);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1f, (float)z + 0.5F);
		GL11.glRotatef(180, 1, 0, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		model.render(hopper, partialTick);
		GL11.glPopMatrix();
	}

}
