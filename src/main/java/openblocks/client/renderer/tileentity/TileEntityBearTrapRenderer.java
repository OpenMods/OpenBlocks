package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelBearTrap;
import openblocks.common.tileentity.TileEntityBearTrap;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityBearTrapRenderer extends TileEntitySpecialRenderer<TileEntityBearTrap> {

	private ModelBearTrap model = new ModelBearTrap();

	private static final ResourceLocation texture = OpenBlocks.location("textures/models/beartrap.png");

	@Override
	public void renderTileEntityAt(TileEntityBearTrap tile, double x, double y, double z, float partialTick, int damageProgress) {
		GL11.glPushMatrix();
		bindTexture(texture);
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-BlockUtils.getRotationFromOrientation(tile.getOrientation()), 0, 1, 0);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		model.renderAll(tile.isShut(), tile.ticksSinceOpened());
		GL11.glPopMatrix();
	}

}
