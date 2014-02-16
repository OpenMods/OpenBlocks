package openblocks.client.renderer;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.block.BlockRadio;
import openmods.renderer.IBlockRenderer;
import openmods.renderer.RotatedTessellator;

public class BlockRadioRenderer implements IBlockRenderer<BlockRadio> {

	private static final double UNIT = 1.0 / 16.0;

	@Override
	public void renderInventoryBlock(BlockRadio block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator tes = new Tessellator();
		tes.startDrawingQuads();
		tes.setTranslation(-0.5, -0.5, -0.5);
		renderRadio(block, tes, false);
		tes.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockRadio block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = ForgeDirection.getOrientation(meta);

		Tessellator tes = Tessellator.instance;
		double tx = tes.xOffset;
		double ty = tes.yOffset;
		double tz = tes.zOffset;
		tes.setTranslation(x + tx, y + ty, z + tz);

		Tessellator rotated = RotatedTessellator.wrap(tes, dir);
		renderRadio(block, rotated, true);

		tes.setTranslation(tx, ty, tz);
		return true;
	}

	private static void renderWalls(BlockRadio b, Tessellator tes, double dx, double dy, double dz) {
		final double mx = 1 - dx;
		final double my = 1 - dy;
		final double mz = 1 - dz;

		tes.addVertexWithUV(mx, 0, dz, b.iconFront.getInterpolatedU(dx * 16), b.iconFront.getMaxV());
		tes.addVertexWithUV(dx, 0, dz, b.iconFront.getInterpolatedU(mx * 16), b.iconFront.getMaxV());
		tes.addVertexWithUV(dx, dy, dz, b.iconFront.getInterpolatedU(mx * 16), b.iconFront.getInterpolatedV(my * 16));
		tes.addVertexWithUV(mx, dy, dz, b.iconFront.getInterpolatedU(dx * 16), b.iconFront.getInterpolatedV(my * 16));

		tes.addVertexWithUV(mx, 0, mz, b.iconBack.getInterpolatedU(dx * 16), b.iconBack.getMaxV());
		tes.addVertexWithUV(mx, dy, mz, b.iconBack.getInterpolatedU(dx * 16), b.iconBack.getInterpolatedV(my * 16));
		tes.addVertexWithUV(dx, dy, mz, b.iconBack.getInterpolatedU(mx * 16), b.iconBack.getInterpolatedV(my * 16));
		tes.addVertexWithUV(dx, 0, mz, b.iconBack.getInterpolatedU(mx * 16), b.iconBack.getMaxV());

		tes.addVertexWithUV(dx, 0, dz, b.iconSide.getInterpolatedU(dz * 16), b.iconSide.getMaxV());
		tes.addVertexWithUV(dx, 0, mz, b.iconSide.getInterpolatedU(mz * 16), b.iconSide.getMaxV());
		tes.addVertexWithUV(dx, dy, mz, b.iconSide.getInterpolatedU(mz * 16), b.iconSide.getInterpolatedV(my * 16));
		tes.addVertexWithUV(dx, dy, dz, b.iconSide.getInterpolatedU(dz * 16), b.iconSide.getInterpolatedV(my * 16));

		tes.addVertexWithUV(mx, 0, dz, b.iconSide.getInterpolatedU(dz * 16), b.iconSide.getMaxV());
		tes.addVertexWithUV(mx, dy, dz, b.iconSide.getInterpolatedU(dz * 16), b.iconSide.getInterpolatedV(my * 16));
		tes.addVertexWithUV(mx, dy, mz, b.iconSide.getInterpolatedU(mz * 16), b.iconSide.getInterpolatedV(my * 16));
		tes.addVertexWithUV(mx, 0, mz, b.iconSide.getInterpolatedU(mz * 16), b.iconSide.getMaxV());

		tes.addVertexWithUV(dx, dy, dz, b.iconTop.getInterpolatedU(dx * 16), b.iconTop.getInterpolatedV(mz * 16));
		tes.addVertexWithUV(dx, dy, mz, b.iconTop.getInterpolatedU(dx * 16), b.iconTop.getInterpolatedV(dz * 16));
		tes.addVertexWithUV(mx, dy, mz, b.iconTop.getInterpolatedU(mx * 16), b.iconTop.getInterpolatedV(dz * 16));
		tes.addVertexWithUV(mx, dy, dz, b.iconTop.getInterpolatedU(mx * 16), b.iconTop.getInterpolatedV(mz * 16));
	}

	private static void renderBottom(BlockRadio b, Tessellator tes) {
		tes.addVertexWithUV(0, 0, 0, b.iconBottom.getMinU(), b.iconBottom.getMaxV());
		tes.addVertexWithUV(1, 0, 0, b.iconBottom.getMaxU(), b.iconBottom.getMaxV());
		tes.addVertexWithUV(1, 0, 1, b.iconBottom.getMaxU(), b.iconBottom.getMinV());
		tes.addVertexWithUV(0, 0, 1, b.iconBottom.getMinU(), b.iconBottom.getMinV());
	}

	private static void renderInside(BlockRadio b, Tessellator tes) {
		final double lx = 6 * UNIT;
		final double rx = 1 - lx;

		final double by = 1 * UNIT;
		final double ty = 7 * UNIT;

		final double fz = 1 * UNIT;
		final double bz = 5 * UNIT;

		tes.addVertexWithUV(lx, by, fz, b.iconInside.getInterpolatedU(0), b.iconInside.getInterpolatedV(15));
		tes.addVertexWithUV(lx, by, bz, b.iconInside.getInterpolatedU(6), b.iconInside.getInterpolatedV(15));
		tes.addVertexWithUV(rx, by, bz, b.iconInside.getInterpolatedU(6), b.iconInside.getInterpolatedV(11));
		tes.addVertexWithUV(rx, by, fz, b.iconInside.getInterpolatedU(0), b.iconInside.getInterpolatedV(11));

		tes.addVertexWithUV(lx, ty, fz, b.iconInside.getInterpolatedU(16), b.iconInside.getInterpolatedV(5));
		tes.addVertexWithUV(rx, ty, fz, b.iconInside.getInterpolatedU(16), b.iconInside.getInterpolatedV(1));
		tes.addVertexWithUV(rx, ty, bz, b.iconInside.getInterpolatedU(10), b.iconInside.getInterpolatedV(1));
		tes.addVertexWithUV(lx, ty, bz, b.iconInside.getInterpolatedU(10), b.iconInside.getInterpolatedV(5));

		tes.addVertexWithUV(lx, ty, fz, b.iconInside.getInterpolatedU(16), b.iconInside.getInterpolatedV(5));
		tes.addVertexWithUV(lx, ty, bz, b.iconInside.getInterpolatedU(10), b.iconInside.getInterpolatedV(5));
		tes.addVertexWithUV(lx, by, bz, b.iconInside.getInterpolatedU(10), b.iconInside.getInterpolatedV(11));
		tes.addVertexWithUV(lx, by, fz, b.iconInside.getInterpolatedU(16), b.iconInside.getInterpolatedV(11));

		tes.addVertexWithUV(rx, ty, fz, b.iconInside.getInterpolatedU(0), b.iconInside.getInterpolatedV(5));
		tes.addVertexWithUV(rx, by, fz, b.iconInside.getInterpolatedU(0), b.iconInside.getInterpolatedV(11));
		tes.addVertexWithUV(rx, by, bz, b.iconInside.getInterpolatedU(6), b.iconInside.getInterpolatedV(11));
		tes.addVertexWithUV(rx, ty, bz, b.iconInside.getInterpolatedU(6), b.iconInside.getInterpolatedV(5));

		tes.addVertexWithUV(lx, ty, bz, b.iconInside.getInterpolatedU(10), b.iconInside.getInterpolatedV(5));
		tes.addVertexWithUV(rx, ty, bz, b.iconInside.getInterpolatedU(6), b.iconInside.getInterpolatedV(5));
		tes.addVertexWithUV(rx, by, bz, b.iconInside.getInterpolatedU(6), b.iconInside.getInterpolatedV(11));
		tes.addVertexWithUV(lx, by, bz, b.iconInside.getInterpolatedU(10), b.iconInside.getInterpolatedV(11));
	}

	private static void renderRadio(BlockRadio b, Tessellator tes, boolean state) {
		tes.setColorOpaque(255, 255, 255);
		renderWalls(b, tes, 5 * UNIT, 15 * UNIT, 1 * UNIT);
		renderWalls(b, tes, 2 * UNIT, 12 * UNIT, 3 * UNIT);
		renderWalls(b, tes, 0 * UNIT, 10 * UNIT, 5 * UNIT);
		renderBottom(b, tes);
		renderInside(b, tes);

	}
}
