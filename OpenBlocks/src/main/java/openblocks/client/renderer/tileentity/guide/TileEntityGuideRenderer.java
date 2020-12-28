package openblocks.client.renderer.tileentity.guide;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.shapes.CoordShape;

public class TileEntityGuideRenderer<T extends TileEntityGuide> extends TileEntityRenderer<T> {

	protected final GuideModelHolder holder;

	public TileEntityGuideRenderer(TileEntityRendererDispatcher dispatcher, GuideModelHolder holder) {
		super(dispatcher);
		this.holder = holder;
	}

	@Override
	public boolean isGlobalRenderer(T te) {
		return te.shouldRender();
		/// using that option causes glitches due to MC-112730
	}

	@Override
	public void render(T guide, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferGroup, int combinedLight, int combinedOverlay) {
		if (!guide.shouldRender()) {
			return;
		}
		IVertexBuilder buffer = bufferGroup.getBuffer(RenderType.getTranslucent());
		final float scaleDelta = guide.getTimeSinceChange();
		final Vector4f selfPose = new Vector4f(0, 0, 0, 1);
		selfPose.transform(matrixStack.getLast().getMatrix());
		Vector3f pos = new Vector3f(selfPose.getX(), selfPose.getY(), selfPose.getZ());
		renderShape(buffer, matrixStack, guide.getShape(), guide.getColor(), scaleDelta, pos);
		if (scaleDelta < 1.0) {
			renderShape(buffer, matrixStack, guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta, pos);
		}
	}

	private static float distanceFromOrigin(final Vector3f base, final BlockPos delta) {
		final float x = base.getX() + delta.getX();
		final float y = base.getY() + delta.getY();
		final float z = base.getZ() + delta.getZ();

		return x * x + y * y + z * z;
	}

	private void renderShape(IVertexBuilder bufferBuilder, final MatrixStack stack, @Nullable CoordShape shape, int color, float scale, final Vector3f pos) {
		if (shape == null) {
			return;
		}

		final float red = ((color >> 16) & 0xFF) / 255.0f;
		final float green = ((color >> 8) & 0xFF) / 255.0f;
		final float blue = ((color >> 0) & 0xFF) / 255.0f;
		float[] colorMuls = { 1.0f, 1.0f, 1.0f, 1.0F };
		int combinedLightIn = LightTexture.packLight(15, 15);
		int[] light = { combinedLightIn, combinedLightIn, combinedLightIn, combinedLightIn };


		// Forge removed TE buffer sorting, do it ourselves...
		shape.getCoords().stream().sorted(Comparator.comparing((BlockPos b) -> distanceFromOrigin(pos, b)).reversed()).forEach(coord -> {
			stack.push();
			stack.translate(coord.getX(), coord.getY(), coord.getZ());
			stack.scale(scale, scale, scale);
			for (BakedQuad markerQuad : holder.getMarkerQuads()) {
				bufferBuilder.addQuad(stack.getLast(), markerQuad, colorMuls, red, green, blue, light, OverlayTexture.NO_OVERLAY, false);
			}
			stack.pop();
		});
	}
}
