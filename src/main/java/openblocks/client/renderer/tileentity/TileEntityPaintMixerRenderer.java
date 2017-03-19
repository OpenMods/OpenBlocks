package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openmods.colors.RGB;
import openmods.utils.BlockUtils;
import org.lwjgl.opengl.GL11;

public class TileEntityPaintMixerRenderer extends TileEntitySpecialRenderer<TileEntityPaintMixer> {

	private ModelPaintMixer model = new ModelPaintMixer();
	private static final ResourceLocation texture = OpenBlocks.location("textures/models/paintmixer.png");

	@Override
	public void renderTileEntityAt(TileEntityPaintMixer mixer, double x, double y, double z, float partialTick, int destroyProgress) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		if (mixer != null) GL11.glRotatef(-BlockUtils.getRotationFromOrientation(mixer.getOrientation()), 0, 1, 0);
		bindTexture(texture);
		model.render(mixer, partialTick);
		// The top of the paint can is rendering longer than the can. WHYY :(
		// Fixed this, rotating the matrix and not using the stack :)

		if (mixer != null) {
			GL11.glTranslated(0.05, 0.5, 0);
			GL11.glRotated(150, 0, 0, -1);
			GL11.glRotated(90, 0, 1, 0);
			GL11.glScaled(0.8, 0.8, 0.8);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			if (mixer.hasPaint()) {
				if (mixer.isEnabled()) {
					GL11.glTranslated(0, Math.random() * 0.2, 0);
				}
				int secondPass = mixer.getCanColor();
				if (mixer.isEnabled()) {
					double progress = (double)mixer.getProgress().getValue() / TileEntityPaintMixer.PROGRESS_TICKS;
					secondPass = calculateColorFade(secondPass, mixer.getColor().getValue(), progress);
				}
				// TODO 1.8.9 render can!
			}
		}
		GL11.glPopMatrix();
	}

	private static int calculateColorFade(int a, int b, double magnitude) {
		return new RGB(a).interpolate(new RGB(b), magnitude).getColor();
	}

}
