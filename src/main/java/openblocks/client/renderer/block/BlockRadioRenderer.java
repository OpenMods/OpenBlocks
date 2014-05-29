package openblocks.client.renderer.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.block.BlockRadio;
import openblocks.common.tileentity.TileEntityRadio;
import openmods.block.OpenBlock;
import openmods.renderer.IBlockRenderer;
import openmods.renderer.RotatedTessellator;

import org.lwjgl.opengl.GL11;

public class BlockRadioRenderer implements IBlockRenderer<BlockRadio> {

	private static final double UNIT = 1.0 / 16.0;

	@Override
	public void renderInventoryBlock(BlockRadio block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator tes = new Tessellator();
		tes.startDrawingQuads();
		tes.setTranslation(-0.5, -0.5, -0.5);
		renderRadio(block, new RotatedTessellator.R90(tes), null);
		GL11.glDisable(GL11.GL_LIGHTING);
		tes.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockRadio block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		TileEntityRadio te = OpenBlock.getTileEntity(world, x, y, z, TileEntityRadio.class);
		ForgeDirection dir = ForgeDirection.getOrientation(meta);

		Tessellator tes = Tessellator.instance;
		double tx = tes.xOffset;
		double ty = tes.yOffset;
		double tz = tes.zOffset;
		tes.setTranslation(x + tx, y + ty, z + tz);

		Tessellator rotated = RotatedTessellator.wrap(tes, dir);
		tes.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		renderRadio(block, rotated, te != null? te.getCrystalColor() : null);

		tes.setTranslation(tx, ty, tz);
		return true;
	}

	private static void renderWalls(BlockRadio b, Tessellator tes, double dx, double dy, double dz) {
		final double mx = 1 - dx;
		final double my = 1 - dy;
		final double mz = 1 - dz;

		final IIcon front = b.iconFront;
		final IIcon back = b.iconBack;
		final IIcon side = b.iconSide;
		final IIcon top = b.iconTop;

		tes.addVertexWithUV(mx, 0, dz, front.getInterpolatedU(dx * 16), front.getMaxV());
		tes.addVertexWithUV(dx, 0, dz, front.getInterpolatedU(mx * 16), front.getMaxV());
		tes.addVertexWithUV(dx, dy, dz, front.getInterpolatedU(mx * 16), front.getInterpolatedV(my * 16));
		tes.addVertexWithUV(mx, dy, dz, front.getInterpolatedU(dx * 16), front.getInterpolatedV(my * 16));

		tes.addVertexWithUV(mx, 0, mz, back.getInterpolatedU(dx * 16), back.getMaxV());
		tes.addVertexWithUV(mx, dy, mz, back.getInterpolatedU(dx * 16), back.getInterpolatedV(my * 16));
		tes.addVertexWithUV(dx, dy, mz, back.getInterpolatedU(mx * 16), back.getInterpolatedV(my * 16));
		tes.addVertexWithUV(dx, 0, mz, back.getInterpolatedU(mx * 16), back.getMaxV());

		tes.addVertexWithUV(dx, 0, dz, side.getInterpolatedU(dz * 16), side.getMaxV());
		tes.addVertexWithUV(dx, 0, mz, side.getInterpolatedU(mz * 16), side.getMaxV());
		tes.addVertexWithUV(dx, dy, mz, side.getInterpolatedU(mz * 16), side.getInterpolatedV(my * 16));
		tes.addVertexWithUV(dx, dy, dz, side.getInterpolatedU(dz * 16), side.getInterpolatedV(my * 16));

		tes.addVertexWithUV(mx, 0, dz, side.getInterpolatedU(dz * 16), side.getMaxV());
		tes.addVertexWithUV(mx, dy, dz, side.getInterpolatedU(dz * 16), side.getInterpolatedV(my * 16));
		tes.addVertexWithUV(mx, dy, mz, side.getInterpolatedU(mz * 16), side.getInterpolatedV(my * 16));
		tes.addVertexWithUV(mx, 0, mz, side.getInterpolatedU(mz * 16), side.getMaxV());

		tes.addVertexWithUV(dx, dy, dz, top.getInterpolatedU(dx * 16), top.getInterpolatedV(mz * 16));
		tes.addVertexWithUV(dx, dy, mz, top.getInterpolatedU(dx * 16), top.getInterpolatedV(dz * 16));
		tes.addVertexWithUV(mx, dy, mz, top.getInterpolatedU(mx * 16), top.getInterpolatedV(dz * 16));
		tes.addVertexWithUV(mx, dy, dz, top.getInterpolatedU(mx * 16), top.getInterpolatedV(mz * 16));
	}

	private static void renderBottom(BlockRadio b, Tessellator tes) {
		final float minU = b.iconBottom.getMinU();
		final float maxV = b.iconBottom.getMaxV();
		final float maxU = b.iconBottom.getMaxU();
		final float minV = b.iconBottom.getMinV();

		tes.addVertexWithUV(0, 0, 0, minU, maxV);
		tes.addVertexWithUV(1, 0, 0, maxU, maxV);
		tes.addVertexWithUV(1, 0, 1, maxU, minV);
		tes.addVertexWithUV(0, 0, 1, minU, minV);
	}

	private static void renderInside(BlockRadio b, Tessellator tes) {
		final double lx = 6 * UNIT;
		final double rx = 1 - lx;

		final double by = 1 * UNIT;
		final double ty = 7 * UNIT;

		final double fz = 1 * UNIT;
		final double bz = 5 * UNIT;

		final IIcon inside = b.iconInside;

		final float u0 = inside.getInterpolatedU(0);
		final float u6 = inside.getInterpolatedU(6);
		final float u16 = inside.getInterpolatedU(16);

		final float v1 = inside.getInterpolatedV(1);
		final float v5 = inside.getInterpolatedV(5);
		final float v10 = inside.getInterpolatedU(10);
		final float v11 = inside.getInterpolatedV(11);
		final float v15 = inside.getInterpolatedV(15);

		tes.addVertexWithUV(lx, by, fz, u0, v15);
		tes.addVertexWithUV(lx, by, bz, u6, v15);
		tes.addVertexWithUV(rx, by, bz, u6, v11);
		tes.addVertexWithUV(rx, by, fz, u0, v11);

		tes.addVertexWithUV(lx, ty, fz, u16, v5);
		tes.addVertexWithUV(rx, ty, fz, u16, v1);
		tes.addVertexWithUV(rx, ty, bz, v10, v1);
		tes.addVertexWithUV(lx, ty, bz, v10, v5);

		tes.addVertexWithUV(lx, ty, fz, u16, v5);
		tes.addVertexWithUV(lx, ty, bz, v10, v5);
		tes.addVertexWithUV(lx, by, bz, v10, v11);
		tes.addVertexWithUV(lx, by, fz, u16, v11);

		tes.addVertexWithUV(rx, ty, fz, u0, v5);
		tes.addVertexWithUV(rx, by, fz, u0, v11);
		tes.addVertexWithUV(rx, by, bz, u6, v11);
		tes.addVertexWithUV(rx, ty, bz, u6, v5);

		tes.addVertexWithUV(lx, ty, bz, v10, v5);
		tes.addVertexWithUV(rx, ty, bz, u6, v5);
		tes.addVertexWithUV(rx, by, bz, u6, v11);
		tes.addVertexWithUV(lx, by, bz, v10, v11);
	}

	private static void renderCrystalCage(BlockRadio b, Tessellator tes) {
		final double lx = 7 * UNIT;
		final double rx = 1 - lx;

		final double by = 1 * UNIT;
		final double ty = 7 * UNIT;

		final double fz = 2.25 * UNIT;
		final double bz = 3.75 * UNIT;

		final IIcon tex = b.iconInside;

		final float u6 = tex.getInterpolatedU(6);
		final float u10 = tex.getInterpolatedU(10);
		final float u16 = tex.getInterpolatedU(16);

		final float v11 = tex.getInterpolatedV(11);
		final float v15 = tex.getInterpolatedV(15);

		// front
		tes.addVertexWithUV(lx, by, fz, u10, v15);
		tes.addVertexWithUV(lx, ty, fz, u16, v15);

		tes.addVertexWithUV(rx, ty, fz, u16, v11);
		tes.addVertexWithUV(rx, by, fz, u10, v11);

		// back
		tes.addVertexWithUV(lx, by, bz, u10, v15);
		tes.addVertexWithUV(lx, ty, bz, u16, v15);
		tes.addVertexWithUV(rx, ty, bz, u16, v11);
		tes.addVertexWithUV(rx, by, bz, u10, v11);

		// sides (duplicated, since we can't disable culling
		tes.addVertexWithUV(lx, by, bz, u10, v15);
		tes.addVertexWithUV(lx, by, fz, u10, v11);
		tes.addVertexWithUV(lx, ty, fz, u16, v11);
		tes.addVertexWithUV(lx, ty, bz, u16, v15);

		tes.addVertexWithUV(rx, by, bz, u10, v15);
		tes.addVertexWithUV(rx, by, fz, u10, v11);
		tes.addVertexWithUV(rx, ty, fz, u16, v11);
		tes.addVertexWithUV(rx, ty, bz, u16, v15);

		tes.addVertexWithUV(lx, by, bz, u10, v15);
		tes.addVertexWithUV(lx, ty, bz, u16, v15);
		tes.addVertexWithUV(lx, ty, fz, u16, v11);
		tes.addVertexWithUV(lx, by, fz, u10, v11);

		tes.addVertexWithUV(rx, by, bz, u10, v15);
		tes.addVertexWithUV(rx, ty, bz, u16, v15);
		tes.addVertexWithUV(rx, ty, fz, u16, v11);
		tes.addVertexWithUV(rx, by, fz, u10, v11);

		final double by2 = 2 * UNIT;
		final double ty2 = 6 * UNIT;

		// top
		tes.addVertexWithUV(rx, ty2, bz, u6, v15);
		tes.addVertexWithUV(lx, ty2, bz, u10, v15);
		tes.addVertexWithUV(lx, ty2, fz, u10, v11);
		tes.addVertexWithUV(rx, ty2, fz, u6, v11);

		// bottom
		tes.addVertexWithUV(rx, by2, bz, u6, v15);
		tes.addVertexWithUV(rx, by2, fz, u6, v11);
		tes.addVertexWithUV(lx, by2, fz, u10, v11);
		tes.addVertexWithUV(lx, by2, bz, u10, v15);
	}

	private static void renderCrystal(BlockRadio b, Tessellator tes, Integer crystal) {
		final double lx = 7.5 * UNIT;
		final double rx = 1 - lx;

		final double by = 3.5 * UNIT;
		final double ty = 4.5 * UNIT;

		final double fz = 2.5 * UNIT;
		final double bz = 3.5 * UNIT;

		final IIcon tex = b.iconInside;

		tes.addVertexWithUV(rx, by, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(4));
		tes.addVertexWithUV(lx, by, fz, tex.getInterpolatedU(3), tex.getInterpolatedV(4));
		tes.addVertexWithUV(lx, ty, fz, tex.getInterpolatedU(3), tex.getInterpolatedV(2));
		tes.addVertexWithUV(rx, ty, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(2));

		tes.addVertexWithUV(rx, by, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(4));
		tes.addVertexWithUV(rx, ty, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(2));
		tes.addVertexWithUV(rx, ty, bz, tex.getInterpolatedU(3), tex.getInterpolatedV(2));
		tes.addVertexWithUV(rx, by, bz, tex.getInterpolatedU(3), tex.getInterpolatedV(4));

		tes.addVertexWithUV(lx, by, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(4));
		tes.addVertexWithUV(lx, by, bz, tex.getInterpolatedU(3), tex.getInterpolatedV(4));
		tes.addVertexWithUV(lx, ty, bz, tex.getInterpolatedU(3), tex.getInterpolatedV(2));
		tes.addVertexWithUV(lx, ty, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(2));

		tes.addVertexWithUV(rx, ty, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(4));
		tes.addVertexWithUV(lx, ty, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(2));
		tes.addVertexWithUV(lx, ty, bz, tex.getInterpolatedU(3), tex.getInterpolatedV(2));
		tes.addVertexWithUV(rx, ty, bz, tex.getInterpolatedU(2), tex.getInterpolatedV(4));

		tes.addVertexWithUV(rx, by, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(4));
		tes.addVertexWithUV(rx, by, bz, tex.getInterpolatedU(3), tex.getInterpolatedV(4));
		tes.addVertexWithUV(lx, by, bz, tex.getInterpolatedU(3), tex.getInterpolatedV(2));
		tes.addVertexWithUV(lx, by, fz, tex.getInterpolatedU(1), tex.getInterpolatedV(2));
	}

	private static void renderRadio(BlockRadio b, Tessellator tes, Integer crystal) {
		tes.setColorOpaque(255, 255, 255);
		renderWalls(b, tes, 5 * UNIT, 15 * UNIT, 1 * UNIT);
		renderWalls(b, tes, 2 * UNIT, 12 * UNIT, 3 * UNIT);
		renderWalls(b, tes, 0 * UNIT, 10 * UNIT, 5 * UNIT);
		renderBottom(b, tes);
		renderInside(b, tes);

		if (crystal != null) {
			renderCrystalCage(b, tes);
			tes.setColorOpaque_I(crystal);
			renderCrystal(b, tes, crystal);
		}
	}

}
