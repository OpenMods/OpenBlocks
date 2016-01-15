package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelXPShower;
import openblocks.common.tileentity.TileEntityXPShower;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityXPShowerRenderer extends TileEntitySpecialRenderer<TileEntityXPShower> {

	private ModelXPShower model = new ModelXPShower();
	private static final ResourceLocation texture = OpenBlocks.location("textures/models/xpshower.png");

	@Override
	public void renderTileEntityAt(TileEntityXPShower shower, double x, double y, double z, float partialTick, int destroyProgress) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-BlockUtils.getRotationFromOrientation(shower.getOrientation()), 0, 1, 0);
		bindTexture(texture);
		model.render(shower, partialTick);
		GL11.glPopMatrix();
	}

}
