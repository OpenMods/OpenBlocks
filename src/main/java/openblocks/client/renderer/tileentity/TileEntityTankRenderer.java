package openblocks.client.renderer.tileentity;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityTank.RenderContext;
import openmods.tileentity.renderer.OpenRenderHelper;

import org.lwjgl.opengl.GL11;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		bindTexture(TextureMap.locationBlocksTexture);
		TileEntityTank tankTile = (TileEntityTank)tileentity;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

		RenderContext context = tankTile.createRenderContext();
		boolean tankInEast = context.hasNeighbor(ForgeDirection.EAST);
		boolean tankInWest = context.hasNeighbor(ForgeDirection.WEST);
		boolean tankInNorth = context.hasNeighbor(ForgeDirection.NORTH);
		boolean tankInSouth = context.hasNeighbor(ForgeDirection.SOUTH);
		boolean tankUp = context.hasNeighbor(ForgeDirection.UP);
		boolean tankDown = context.hasNeighbor(ForgeDirection.DOWN);

		if (!tankInEast) {
			if (!tankInNorth) OpenRenderHelper.renderCube(0.475, -0.501, -0.501, 0.501, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			if (!tankInSouth) OpenRenderHelper.renderCube(0.475, -0.501, 0.475, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			if (!tankDown) OpenRenderHelper.renderCube(0.475, -0.501, -0.501, 0.501, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
			if (!tankUp) OpenRenderHelper.renderCube(0.475, 0.475, -0.501, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
		}
		if (!tankInWest) {
			if (!tankInNorth) OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, -0.475, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			if (!tankInSouth) OpenRenderHelper.renderCube(-0.501, -0.501, 0.475, -0.475, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			if (!tankDown) OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, -0.475, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
			if (!tankUp) OpenRenderHelper.renderCube(-0.501, 0.475, -0.501, -0.475, 0.501, 0.501, OpenBlocks.Blocks.tank, null);

		}
		if (!tankInNorth) {
			if (!tankUp) OpenRenderHelper.renderCube(-0.501, 0.475, -0.501, 0.501, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			if (!tankDown) OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, 0.501, -0.475, -0.475, OpenBlocks.Blocks.tank, null);
		}

		if (!tankInSouth) {
			if (!tankUp) OpenRenderHelper.renderCube(-0.501, 0.475, 0.475, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			if (!tankDown) OpenRenderHelper.renderCube(-0.501, -0.501, 0.475, 0.501, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
		}

		IFluidTank internalTank = tankTile.getTank();

		FluidStack fluidStack = internalTank.getFluid();

		if (fluidStack != null && tankTile.getHeightForRender() > 0.05) {
			GL11.glDisable(GL11.GL_LIGHTING);

			final Fluid fluid = fluidStack.getFluid();

			final Icon texture = fluid.getStillIcon();
			final int color = fluid.getColor(fluidStack);

			bindTexture(getFluidSheet(fluid));

			Tessellator t = Tessellator.instance;

			final double ySouthEast = context.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.EAST);
			final double yNorthEast = context.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.EAST);
			final double ySouthWest = context.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.WEST);
			final double yNorthWest = context.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.WEST);

			final double uMin = texture.getInterpolatedU(0.0);
			final double uMax = texture.getInterpolatedU(16.0);
			final double vMin = texture.getInterpolatedV(0.0);
			final double vMax = texture.getInterpolatedV(16.0);

			final double vHeight = vMax - vMin;

			final float r = (color >> 16 & 0xFF) / 255.0F;
			final float g = (color >> 8 & 0xFF) / 255.0F;
			final float b = (color & 0xFF) / 255.0F;

			// north side
			t.startDrawingQuads();
			t.setColorOpaque_F(r, g, b);
			t.addVertexWithUV(0.5, -0.5, -0.5, uMax, vMin); // bottom
			t.addVertexWithUV(-0.5, -0.5, -0.5, uMin, vMin); // bottom
			// top north/west
			t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMin, vMin + (vHeight * yNorthWest));
			// top north/east
			t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMax, vMin + (vHeight * yNorthEast));

			// south side
			t.addVertexWithUV(0.5, -0.5, 0.5, uMin, vMin);
			// top south east
			t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMin, vMin + (vHeight * ySouthEast));
			// top south west
			t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMax, vMin + (vHeight * ySouthWest));
			t.addVertexWithUV(-0.5, -0.5, 0.5, uMax, vMin);

			// east side
			t.addVertexWithUV(0.5, -0.5, -0.5, uMin, vMin);
			// top north/east
			t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMin, vMin + (vHeight * yNorthEast));
			// top south/east
			t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMax, vMin + (vHeight * ySouthEast));
			t.addVertexWithUV(0.5, -0.5, 0.5, uMax, vMin);

			// west side
			t.addVertexWithUV(-0.5, -0.5, 0.5, uMin, vMin);
			// top south/west
			t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMin, vMin + (vHeight * ySouthWest));
			// top north/west
			t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMax, vMin + (vHeight * yNorthWest));
			t.addVertexWithUV(-0.5, -0.5, -0.5, uMax, vMin);

			// top
			// south east
			t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMax, vMin);
			// north east
			t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMin, vMin);
			// north west
			t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMin, vMax);
			// south west
			t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMax, vMax);

			// bottom
			t.addVertexWithUV(0.5, -0.5, -0.5, uMax, vMin);
			t.addVertexWithUV(0.5, -0.5, 0.5, uMin, vMin);
			t.addVertexWithUV(-0.5, -0.5, 0.5, uMin, vMax);
			t.addVertexWithUV(-0.5, -0.5, -0.5, uMax, vMax);
			t.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
		}

		// may be disabled by other procedures
		GL11.glPopMatrix();

	}

	public static ResourceLocation getFluidSheet(FluidStack liquid) {
		if (liquid == null) return TextureMap.locationBlocksTexture;
		return getFluidSheet(liquid.getFluid());
	}

	/**
	 * @param liquid
	 */
	public static ResourceLocation getFluidSheet(Fluid liquid) {
		return TextureMap.locationBlocksTexture;
	}
}
