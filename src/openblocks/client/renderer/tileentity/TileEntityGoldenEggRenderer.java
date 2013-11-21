package openblocks.client.renderer.tileentity;

import org.lwjgl.opengl.GL11;

import openblocks.client.model.ModelEgg;
import openblocks.common.tileentity.TileEntityAutoAnvil;
import openblocks.common.tileentity.TileEntityGoldenEgg;
import openmods.utils.BlockUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

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
		bindTexture(texture);
		model.render(egg, f);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
