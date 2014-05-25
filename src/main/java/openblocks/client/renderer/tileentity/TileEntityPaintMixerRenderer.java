package openblocks.client.renderer.tileentity;

import java.util.EnumSet;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.OpenBlocks;
import openblocks.client.model.ModelPaintMixer;
import openblocks.common.tileentity.TileEntityPaintMixer;
import openmods.utils.BlockUtils;
import openmods.utils.ColorUtils;
import openmods.utils.render.RenderUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityPaintMixerRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderer = new RenderBlocks();
	private ModelPaintMixer model = new ModelPaintMixer();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/paintmixer.png");
	private static final ColorUtils.RGB start = new ColorUtils.RGB(), end = new ColorUtils.RGB();
	private static final EnumSet<ForgeDirection> TOP_FACE = EnumSet.of(ForgeDirection.UP);
	private static final EnumSet<ForgeDirection> SIDES = EnumSet.complementOf(TOP_FACE);

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityPaintMixer mixer = (TileEntityPaintMixer)tileentity;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0f, (float)z + 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glPushMatrix();
		GL11.glRotatef(-BlockUtils.getRotationFromDirection(mixer.getRotation()), 0, 1, 0);
		bindTexture(texture);
		model.render(mixer, f);
		GL11.glPushMatrix();
		// The top of the paint can is rendering longer than the can. WHYY :(
		// Fixed this, rotating the matrix and not using the stack :)
		GL11.glTranslated(0.05, 0.5, 0);
		GL11.glRotated(150, 0, 0, -1);
		GL11.glRotated(90, 0, 1, 0);
		GL11.glScaled(0.8, 0.8, 0.8);
		bindTexture(TextureMap.locationBlocksTexture);
		if (mixer.hasPaint()) {
			GL11.glPushMatrix();
			if (mixer.isEnabled()) {
				GL11.glTranslated(0, Math.random() * 0.2, 0);
			}
			int secondPass = mixer.getCanColor();
			if (mixer.isEnabled()) secondPass = calculateColorFade(secondPass, mixer.getColor().getValue(), mixer.getProgress().getPercent());
			// Render first pass
			RenderUtils.renderInventoryBlock(renderer, OpenBlocks.Blocks.paintCan, ForgeDirection.EAST, 0xFFFFFF, SIDES);
			RenderUtils.renderInventoryBlock(renderer, OpenBlocks.Blocks.paintCan, ForgeDirection.EAST, secondPass, TOP_FACE);
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

	private static int calculateColorFade(int a, int b, double magnitude) {
		start.setColor(a);
		end.setColor(b);
		return start.interpolate(end, magnitude).getColor();
	}

}
