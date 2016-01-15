package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelPiggy;
import openblocks.common.tileentity.TileEntityDonationStation;
import openmods.utils.BlockUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityDonationStationRenderer extends TileEntitySpecialRenderer<TileEntityDonationStation> {

	private ModelPiggy model = new ModelPiggy();
	private static final ResourceLocation texture = OpenBlocks.location("textures/models/piggy.png");

	@Override
	public void renderTileEntityAt(TileEntityDonationStation station, double x, double y, double z, float partialTick, int destroyProgress) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(-BlockUtils.getRotationFromOrientation(station.getOrientation()), 0, 1, 0);
		bindTexture(texture);
		model.render(station, partialTick);
		GL11.glPopMatrix();
	}

}
