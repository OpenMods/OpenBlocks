package openblocks.client.renderer.tileentity.guide;

import java.nio.ByteBuffer;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.BlockPos;
import openblocks.common.tileentity.TileEntityGuide;
import openblocks.shapes.CoordShape;

public class TileEntityGuideRenderer<T extends TileEntityGuide> extends TileEntityRenderer<T> {

	protected final GuideModelHolder holder;

	public TileEntityGuideRenderer(GuideModelHolder holder) {
		this.holder = holder;
	}

	@Override
	public boolean isGlobalRenderer(T te) {
		return te.shouldRender();
		/// using that option causes glitches due to MC-112730
	}


	@Override
	public void renderTileEntityFast(T guide, double x, double y, double z, float partialTicks, int destroyStage, BufferBuilder buffer) {
		if (!guide.shouldRender()) {
			return;
		}
		buffer.setTranslation(x, y, z);
		final float scaleDelta = guide.getTimeSinceChange();
		final Vector3f pos = new Vector3f((float)x, (float)y, (float)z);
		renderShape(buffer, guide.getShape(), guide.getColor(), scaleDelta, pos);
		if (scaleDelta < 1.0) {
			renderShape(buffer, guide.getPreviousShape(), guide.getColor(), 1.0f - scaleDelta, pos);
		}
	}

	private static float viewDist(final Vector3f base, final BlockPos pos) {
		final float x = base.getX() + pos.getX();
		final float y = base.getY() + pos.getY();
		final float z = base.getZ() + pos.getZ();

		return x * x + y * y + z * z;
	}

	private void renderShape(BufferBuilder bufferBuilder, @Nullable CoordShape shape, int color, float scale, final Vector3f pos) {
		if (shape == null) {
			return;
		}

		final int red = (color >> 16) & 0xFF;
		final int green = (color >> 8) & 0xFF;
		final int blue = (color >> 0) & 0xFF;

		final ByteBuffer slice = holder.getMarkerQuads();

		// Forge removed TE buffer sorting, do it ourselves...
		shape.getCoords().stream().sorted(Comparator.comparing((BlockPos b) -> viewDist(pos, b)).reversed()).forEach(coord -> {
			slice.rewind();
			while (slice.hasRemaining()) {
				final float x = coord.getX() + scale * slice.getFloat();
				final float y = coord.getY() + scale * slice.getFloat();
				final float z = coord.getZ() + scale * slice.getFloat();

				slice.getInt(); // ignored self tint

				final float u = slice.getFloat();
				final float v = slice.getFloat();

				slice.getInt(); // brightness
				bufferBuilder.pos(x, y, z).color(red, green, blue, 0xFF).tex(u, v).lightmap(0xf0, 0xf0).endVertex();
			}
		});
	}
}
