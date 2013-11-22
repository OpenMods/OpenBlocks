package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelEgg;
import openblocks.common.tileentity.TileEntityGoldenEgg;
import openmods.OpenMods;

import org.lwjgl.opengl.GL11;

public class TileEntityGoldenEggRenderer extends TileEntitySpecialRenderer {
	
	ModelEgg model = new ModelEgg();

	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/egg.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		TileEntityGoldenEgg egg = (TileEntityGoldenEgg)tileentity;
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		if (egg.worldObj != null && egg.getStage() > 0) {
			GL11.glRotatef(OpenMods.proxy.getTicks(tileentity.worldObj) % 360, 0, 1, 0);
		}
		bindTexture(texture);
		model.render(egg, f);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
