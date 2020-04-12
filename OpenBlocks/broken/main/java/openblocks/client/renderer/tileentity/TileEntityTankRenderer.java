package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.fluids.FluidStack;
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

	private static void addVertex(BufferBuilder wr, double x, double y, double z, double u, double v, int r, int g, int b, int a, int skyLight, int blockLight) {
		wr.pos(x, y, z).color(r, g, b, a).tex(u, v).lightmap(skyLight, blockLight).endVertex();
	}

	private static void renderFluid(final BufferBuilder wr, final ITankRenderFluidData data, float time, int combinedLights) {
		final int skyLight = (combinedLights >> 16) & 0xFFFF;
		final int blockLight = (combinedLights >> 0) & 0xFFFF;

		final double se = data.getCornerFluidLevel(Diagonal.SE, time);
		final double ne = data.getCornerFluidLevel(Diagonal.NE, time);
		final double sw = data.getCornerFluidLevel(Diagonal.SW, time);
		final double nw = data.getCornerFluidLevel(Diagonal.NW, time);

		final double center = data.getCenterFluidLevel(time);

		final FluidStack fluid = data.getFluid();
		final TextureAtlasSprite texture = TextureUtils.getFluidTexture(fluid);
		final int color = fluid.getFluid().getColor(fluid);
		final int r = ((color >> 16) & 0xFF);
		final int g = ((color >> 8) & 0xFF);
		final int b = ((color >> 0) & 0xFF);
		final int a = ((color >> 24) & 0xFF);

		final double uMin = texture.getMinU();
		final double uMax = texture.getMaxU();
		final double vMin = texture.getMinV();
		final double vMax = texture.getMaxV();

		final double vHeight = vMax - vMin;

		if (data.shouldRenderFluidWall(Direction.NORTH) && (nw > 0 || ne > 0)) {
			addVertex(wr, 1, 0, 0, uMax, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, ne, 0, uMax, vMin + (vHeight * ne), r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, nw, 0, uMin, vMin + (vHeight * nw), r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, 0, 0, uMin, vMin, r, g, b, a, skyLight, blockLight);
		}

		if (data.shouldRenderFluidWall(Direction.SOUTH) && (se > 0 || sw > 0)) {
			addVertex(wr, 1, 0, 1, uMin, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, se, 1, uMin, vMin + (vHeight * se), r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, sw, 1, uMax, vMin + (vHeight * sw), r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, 0, 1, uMax, vMin, r, g, b, a, skyLight, blockLight);
		}

		if (data.shouldRenderFluidWall(Direction.EAST) && (ne > 0 || se > 0)) {
			addVertex(wr, 1, 0, 0, uMin, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, ne, 0, uMin, vMin + (vHeight * ne), r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, se, 1, uMax, vMin + (vHeight * se), r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, 0, 1, uMax, vMin, r, g, b, a, skyLight, blockLight);
		}

		if (data.shouldRenderFluidWall(Direction.WEST) && (sw > 0 || nw > 0)) {
			addVertex(wr, 0, 0, 1, uMin, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, sw, 1, uMin, vMin + (vHeight * sw), r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, nw, 0, uMax, vMin + (vHeight * nw), r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, 0, 0, uMax, vMin, r, g, b, a, skyLight, blockLight);
		}

		if (data.shouldRenderFluidWall(Direction.UP)) {
			final double uMid = (uMax + uMin) / 2;
			final double vMid = (vMax + vMin) / 2;

			addVertex(wr, 0.5, center, 0.5, uMid, vMid, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, se, 1, uMax, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, ne, 0, uMin, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, nw, 0, uMin, vMax, r, g, b, a, skyLight, blockLight);

			addVertex(wr, 0, sw, 1, uMax, vMax, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, se, 1, uMax, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0.5, center, 0.5, uMid, vMid, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, nw, 0, uMin, vMax, r, g, b, a, skyLight, blockLight);

		}

		if (data.shouldRenderFluidWall(Direction.DOWN)) {
			addVertex(wr, 1, 0, 0, uMax, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 1, 0, 1, uMin, vMin, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, 0, 1, uMin, vMax, r, g, b, a, skyLight, blockLight);
			addVertex(wr, 0, 0, 0, uMax, vMax, r, g, b, a, skyLight, blockLight);
		}
	}
}
