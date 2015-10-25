package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelXPShower;
import openblocks.common.tileentity.TileEntityXPShower;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityXPShowerRenderer extends TileEntitySpecialRenderer {

	private ModelXPShower model = new ModelXPShower();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/xpshower.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityXPShower shower = (TileEntityXPShower)tileentity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-BlockUtils.getRotationFromOrientation(shower.getOrientation()), 0, 1, 0);
		bindTexture(texture);
		model.render(shower, f);
		GL11.glPopMatrix();
	}

}
