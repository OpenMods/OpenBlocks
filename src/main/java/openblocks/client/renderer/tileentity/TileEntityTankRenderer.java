package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.animation.FastTESR;
import openblocks.Config;
import openblocks.client.renderer.tileentity.tank.ITankRenderFluidData;
import openblocks.common.tileentity.TileEntityTank;
import openmods.utils.Diagonal;
import openmods.utils.TextureUtils;

public class TileEntityTankRenderer extends FastTESR<TileEntityTank> {

	@Override
	public void renderTileEntityFast(TileEntityTank tankTile, double x, double y, double z, float partialTicks, int destroyStage, float alpha, BufferBuilder buffer) {
		if (tankTile.isInvalid()) return;

		final ITankRenderFluidData data = tankTile.getRenderFluidData();

		if (data != null && data.hasFluid()) {
			buffer.setTranslation(x, y, z);
			final World world = tankTile.getWorld();
			final float time = world.getTotalWorldTime() + partialTicks;
			final int selfLightLevel = Config.tanksEmitLight? tankTile.getFluidLightLevel() : 0;
			final int combinedLights = world.getCombinedLight(tankTile.getPos(), selfLightLevel);
			renderFluid(buffer, data, time, combinedLights);
			buffer.setTranslation(0, 0, 0);
		}
	}

	private static void addVertexWithUV(BufferBuilder wr, double x, double y, double z, double u, double v, int skyLight, int blockLight) {
		wr.pos(x, y, z).color(0xFF, 0xFF, 0xFF, 0xFF).tex(u, v).lightmap(skyLight, blockLight).endVertex();
	}

	private static void renderFluid(final BufferBuilder wr, final ITankRenderFluidData data, float time, int combinedLights) {
		final int skyLight = (combinedLights >> 16) & 0xFFFF;
		final int blockLight = (combinedLights >> 0) & 0xFFFF;

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
			addVertexWithUV(wr, 1, 0, 0, uMax, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 1, ne, 0, uMax, vMin + (vHeight * ne), skyLight, blockLight);
			addVertexWithUV(wr, 0, nw, 0, uMin, vMin + (vHeight * nw), skyLight, blockLight);
			addVertexWithUV(wr, 0, 0, 0, uMin, vMin, skyLight, blockLight);
		}

		if (data.shouldRenderFluidWall(EnumFacing.SOUTH) && (se > 0 || sw > 0)) {
			addVertexWithUV(wr, 1, 0, 1, uMin, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 1, se, 1, uMin, vMin + (vHeight * se), skyLight, blockLight);
			addVertexWithUV(wr, 0, sw, 1, uMax, vMin + (vHeight * sw), skyLight, blockLight);
			addVertexWithUV(wr, 0, 0, 1, uMax, vMin, skyLight, blockLight);
		}

		if (data.shouldRenderFluidWall(EnumFacing.EAST) && (ne > 0 || se > 0)) {
			addVertexWithUV(wr, 1, 0, 0, uMin, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 1, ne, 0, uMin, vMin + (vHeight * ne), skyLight, blockLight);
			addVertexWithUV(wr, 1, se, 1, uMax, vMin + (vHeight * se), skyLight, blockLight);
			addVertexWithUV(wr, 1, 0, 1, uMax, vMin, skyLight, blockLight);
		}

		if (data.shouldRenderFluidWall(EnumFacing.WEST) && (sw > 0 || nw > 0)) {
			addVertexWithUV(wr, 0, 0, 1, uMin, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 0, sw, 1, uMin, vMin + (vHeight * sw), skyLight, blockLight);
			addVertexWithUV(wr, 0, nw, 0, uMax, vMin + (vHeight * nw), skyLight, blockLight);
			addVertexWithUV(wr, 0, 0, 0, uMax, vMin, skyLight, blockLight);
		}

		if (data.shouldRenderFluidWall(EnumFacing.UP)) {
			final double uMid = (uMax + uMin) / 2;
			final double vMid = (vMax + vMin) / 2;

			addVertexWithUV(wr, 0.5, center, 0.5, uMid, vMid, skyLight, blockLight);
			addVertexWithUV(wr, 1, se, 1, uMax, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 1, ne, 0, uMin, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 0, nw, 0, uMin, vMax, skyLight, blockLight);

			addVertexWithUV(wr, 0, sw, 1, uMax, vMax, skyLight, blockLight);
			addVertexWithUV(wr, 1, se, 1, uMax, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 0.5, center, 0.5, uMid, vMid, skyLight, blockLight);
			addVertexWithUV(wr, 0, nw, 0, uMin, vMax, skyLight, blockLight);

		}

		if (data.shouldRenderFluidWall(EnumFacing.DOWN)) {
			addVertexWithUV(wr, 1, 0, 0, uMax, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 1, 0, 1, uMin, vMin, skyLight, blockLight);
			addVertexWithUV(wr, 0, 0, 1, uMin, vMax, skyLight, blockLight);
			addVertexWithUV(wr, 0, 0, 0, uMax, vMax, skyLight, blockLight);
		}
	}
}
