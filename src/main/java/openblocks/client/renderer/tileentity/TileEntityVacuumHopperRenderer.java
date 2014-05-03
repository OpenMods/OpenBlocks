package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelVacuumHopper;
import openblocks.common.tileentity.TileEntityVacuumHopper;

import org.lwjgl.opengl.GL11;

public class TileEntityVacuumHopperRenderer extends TileEntitySpecialRenderer {

	private ModelVacuumHopper model = new ModelVacuumHopper();

	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/vacuumhopper.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		bindTexture(texture);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1f, (float)z + 0.5F);
		GL11.glRotatef(180, 1, 0, 0);
		TileEntityVacuumHopper hopper = (TileEntityVacuumHopper)tileentity;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		model.render(hopper, f);
		GL11.glPopMatrix();
	}

}
