package openblocks.client.renderer.tileentity.guide;

import java.nio.FloatBuffer;
import java.util.function.Supplier;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.math.BlockPos;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.shapes.CoordShape;
import openmods.utils.OptionalInt;
import openmods.utils.TextureUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class GuideLegacyRenderer implements IGuideRenderer {

	private OptionalInt markerDisplayList = OptionalInt.absent();

	@Override
	public void onModelBake(Supplier<BufferBuilder> model) {
		if (markerDisplayList.isPresent()) {
			GL11.glDeleteLists(markerDisplayList.get(), 1);
		}

		final BufferBuilder vb = model.get();

		final int newList = GL11.glGenLists(1);

		GL11.glNewList(newList, GL11.GL_COMPILE);
		new WorldVertexBufferUploader().draw(vb);
		GL11.glEndList();

		markerDisplayList = OptionalInt.of(newList);
	}

	@Override
	public void renderShape(TileEntityGuide guide) {
		float scaleDelta = guide.getTimeSinceChange();
		renderShape(guide.getShape(), guide.getColor(), scaleDelta);
		if (scaleDelta < 1.0) {
			renderShape(guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta);
		}
	}

	private static float byteToFloat(int value) {
		return (value & 0xFF) / 255.0f;
	}

	private final FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);

	private void renderShape(CoordShape shape, int color, float scale) {
		if (shape == null || !markerDisplayList.isPresent()) return;

		final int displayList = markerDisplayList.get();
		TextureUtils.bindTextureToClient(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.disableLighting();

		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);

		brightnessBuffer.position(0);
		brightnessBuffer.put(byteToFloat(color >> 16));
		brightnessBuffer.put(byteToFloat(color >> 8));
		brightnessBuffer.put(byteToFloat(color >> 0));
		brightnessBuffer.put(1.0f);
		brightnessBuffer.flip();

		GlStateManager.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, brightnessBuffer);

		// TODO blend GL_PRIMARY_COLOR, for feature parity with shader. Using second texture unit doesn't seem to work
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, GL11.GL_TEXTURE);
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, GL13.GL_CONSTANT);
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, GL11.GL_TEXTURE);
		GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);

		for (BlockPos coord : shape.getCoords())
			renderMarkerAt(displayList, coord.getX(), coord.getY(), coord.getZ(), scale);

		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.disableOutlineMode();

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
	}

	private static void renderMarkerAt(int markerDisplayList, double x, double y, double z, float scale) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glScalef(scale, scale, scale);
		GL11.glCallList(markerDisplayList);
		GL11.glPopMatrix();
	}
}
