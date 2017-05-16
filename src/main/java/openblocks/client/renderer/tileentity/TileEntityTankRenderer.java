package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import openblocks.client.renderer.tileentity.tank.ITankRenderFluidData;
import openblocks.common.tileentity.TileEntityTank;
import openmods.utils.Diagonal;
import openmods.utils.TextureUtils;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer<TileEntityTank> {

	@Override
	public void renderTileEntityFast(TileEntityTank tankTile, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer buffer) {
		if (tankTile.isInvalid()) return;

		final ITankRenderFluidData data = tankTile.getRenderFluidData();

		if (data != null && data.hasFluid()) {
			buffer.setTranslation(x, y, z);
			final float time = tankTile.getWorld().getTotalWorldTime() + partialTicks;
			renderFluid(buffer, data, time);
			buffer.setTranslation(0, 0, 0);
		}
	}

	private static void addVertexWithUV(VertexBuffer wr, double x, double y, double z, double u, double v) {
		wr.pos(x, y, z).color(0xFF, 0xFF, 0xFF, 0xFF).tex(u, v).lightmap(240, 240).endVertex();
	}

	private static void renderFluid(final VertexBuffer wr, final ITankRenderFluidData data, float time) {
		final double se = data.getCornerFluidLevel(Diagonal.SE, time);
		final double ne = data.getCornerFluidLevel(Diagonal.NE, time);
		final double sw = data.getCornerFluidLevel(Diagonal.SW, time);
		final double nw = data.getCornerFluidLevel(Diagonal.NW, time);

		final double center = data.getCenterFluidLevel(time);

		final TextureAtlasSprite texture = TextureUtils.getFluidTexture(data.getFluid());
		final double uMin = texture.getMinU();
		final double uMax = texture.getMaxU();
		final double vMin = texture.getMinV();
		final double vMax = texture.getMaxV();

		final double vHeight = vMax - vMin;

		if (data.shouldRenderFluidWall(EnumFacing.NORTH) && (nw > 0 || ne > 0)) {
			addVertexWithUV(wr, 1, 0, 0, uMax, vMin);
			addVertexWithUV(wr, 1, ne, 0, uMax, vMin + (vHeight * ne));
			addVertexWithUV(wr, 0, nw, 0, uMin, vMin + (vHeight * nw));
			addVertexWithUV(wr, 0, 0, 0, uMin, vMin);
		}

		if (data.shouldRenderFluidWall(EnumFacing.SOUTH) && (se > 0 || sw > 0)) {
			addVertexWithUV(wr, 1, 0, 1, uMin, vMin);
			addVertexWithUV(wr, 1, se, 1, uMin, vMin + (vHeight * se));
			addVertexWithUV(wr, 0, sw, 1, uMax, vMin + (vHeight * sw));
			addVertexWithUV(wr, 0, 0, 1, uMax, vMin);
		}

		if (data.shouldRenderFluidWall(EnumFacing.EAST) && (ne > 0 || se > 0)) {
			addVertexWithUV(wr, 1, 0, 0, uMin, vMin);
			addVertexWithUV(wr, 1, ne, 0, uMin, vMin + (vHeight * ne));
			addVertexWithUV(wr, 1, se, 1, uMax, vMin + (vHeight * se));
			addVertexWithUV(wr, 1, 0, 1, uMax, vMin);
		}

		if (data.shouldRenderFluidWall(EnumFacing.WEST) && (sw > 0 || nw > 0)) {
			addVertexWithUV(wr, 0, 0, 1, uMin, vMin);
			addVertexWithUV(wr, 0, sw, 1, uMin, vMin + (vHeight * sw));
			addVertexWithUV(wr, 0, nw, 0, uMax, vMin + (vHeight * nw));
			addVertexWithUV(wr, 0, 0, 0, uMax, vMin);
		}

		if (data.shouldRenderFluidWall(EnumFacing.UP)) {
			final double uMid = (uMax + uMin) / 2;
			final double vMid = (vMax + vMin) / 2;

			addVertexWithUV(wr, 0.5, center, 0.5, uMid, vMid);
			addVertexWithUV(wr, 1, se, 1, uMax, vMin);
			addVertexWithUV(wr, 1, ne, 0, uMin, vMin);
			addVertexWithUV(wr, 0, nw, 0, uMin, vMax);

			addVertexWithUV(wr, 0, sw, 1, uMax, vMax);
			addVertexWithUV(wr, 1, se, 1, uMax, vMin);
			addVertexWithUV(wr, 0.5, center, 0.5, uMid, vMid);
			addVertexWithUV(wr, 0, nw, 0, uMin, vMax);

		}

		if (data.shouldRenderFluidWall(EnumFacing.DOWN)) {
			addVertexWithUV(wr, 1, 0, 0, uMax, vMin);
			addVertexWithUV(wr, 1, 0, 1, uMin, vMin);
			addVertexWithUV(wr, 0, 0, 1, uMin, vMax);
			addVertexWithUV(wr, 0, 0, 0, uMax, vMax);
		}
	}
}
