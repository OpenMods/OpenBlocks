package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelBearTrap;
import openblocks.common.tileentity.TileEntityBearTrap;
import openblocks.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityBearTrapRenderer extends TileEntitySpecialRenderer {

	private ModelBearTrap model = new ModelBearTrap();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		bindTextureByName(OpenBlocks.getTexturesPath("models/beartrap.png"));
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		TileEntityBearTrap tile = (TileEntityBearTrap)tileentity;
		GL11.glRotatef(-BlockUtils.getRotationFromDirection(tile.getRotation()), 0, 1, 0);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		model.renderAll(tile.isShut(), tile.tickSinceOpened());
		GL11.glPopMatrix();
	}

}
