package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import openblocks.client.model.ModelBearTrap;
import openblocks.common.tileentity.TileEntityBearTrap;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityBearTrapRenderer extends TileEntitySpecialRenderer {

	private ModelBearTrap model = new ModelBearTrap();

	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/beartrap.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		bindTexture(texture);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		TileEntityBearTrap tile = (TileEntityBearTrap)tileentity;
		GL11.glRotatef(-BlockUtils.getRotationFromDirection(tile.getRotation()), 0, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (tile.isRenderedInInventory()) model.renderAll(false, 15);
		else model.renderAll(tile.isShut(), tile.ticksSinceOpened());
		GL11.glPopMatrix();
	}

}
