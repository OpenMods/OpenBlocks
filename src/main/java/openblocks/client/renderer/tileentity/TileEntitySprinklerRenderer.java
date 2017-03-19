package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelSprinkler;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.utils.BlockUtils;
import org.lwjgl.opengl.GL11;

public class TileEntitySprinklerRenderer extends TileEntitySpecialRenderer<TileEntitySprinkler> {

	private ModelSprinkler model = new ModelSprinkler();
	private static final ResourceLocation texture = OpenBlocks.location("textures/models/sprinkler.png");

	@Override
	public void renderTileEntityAt(TileEntitySprinkler sprinkler, double x, double y, double z, float partialTick, int destroyProcess) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-BlockUtils.getRotationFromOrientation(sprinkler.getOrientation()), 0, 1, 0);
		bindTexture(texture);
		model.render(sprinkler, partialTick);
		GL11.glPopMatrix();
	}

}
