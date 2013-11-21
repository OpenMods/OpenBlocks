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
import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;
import openmods.client.renderer.tileentity.OpenRenderHelper;

import org.lwjgl.opengl.GL11;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		bindTexture(TextureMap.locationBlocksTexture);
		TileEntityTank tankTile = (TileEntityTank)tileentity;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GL11.glPushMatrix();

		boolean tankInEast = tankTile.getTankInDirection(ForgeDirection.EAST) != null;
		boolean tankInWest = tankTile.getTankInDirection(ForgeDirection.WEST) != null;
		boolean tankInNorth = tankTile.getTankInDirection(ForgeDirection.NORTH) != null;
		boolean tankInSouth = tankTile.getTankInDirection(ForgeDirection.SOUTH) != null;
		boolean tankUp = tankTile.getTankInDirection(ForgeDirection.UP) != null;
		boolean tankDown = tankTile.getTankInDirection(ForgeDirection.DOWN) != null;

		if (!tankInEast) {
			if (!tankInNorth) {
				// north east
				OpenRenderHelper.renderCube(0.475, -0.501, -0.501, 0.501, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			}
			if (!tankInSouth) {
				// south east
				OpenRenderHelper.renderCube(0.475, -0.501, 0.475, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}

			if (!tankDown) {
				// bottom east
				OpenRenderHelper.renderCube(0.475, -0.501, -0.501, 0.501, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
			}

			if (!tankUp) {
				// top east
				OpenRenderHelper.renderCube(0.475, 0.475, -0.501, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
		}
		if (!tankInWest) {
			if (!tankInNorth) {
				// north west
				OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, -0.475, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			}
			if (!tankInSouth) {
				// south west
				OpenRenderHelper.renderCube(-0.501, -0.501, 0.475, -0.475, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
			if (!tankDown) {
				// bottom west
				OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, -0.475, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
			}
			if (!tankUp) {
				// top west
				OpenRenderHelper.renderCube(-0.501, 0.475, -0.501, -0.475, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
		}
		if (!tankInNorth) {
			if (!tankUp) {
				// top north
				OpenRenderHelper.renderCube(-0.501, 0.475, -0.501, 0.501, 0.501, -0.475, OpenBlocks.Blocks.tank, null);
			}
			if (!tankDown) {
				// bottom north
				OpenRenderHelper.renderCube(-0.501, -0.501, -0.501, 0.501, -0.475, -0.475, OpenBlocks.Blocks.tank, null);
			}
		}

		if (!tankInSouth) {

			if (!tankUp) {
				// top south
				OpenRenderHelper.renderCube(-0.501, 0.475, 0.475, 0.501, 0.501, 0.501, OpenBlocks.Blocks.tank, null);
			}
			if (!tankDown) {
				// bottom south
				OpenRenderHelper.renderCube(-0.501, -0.501, 0.475, 0.501, -0.475, 0.501, OpenBlocks.Blocks.tank, null);
			}
		}

		GL11.glEnable(2896);

		IFluidTank internalTank = tankTile.getTank();

		FluidStack fluidStack = internalTank.getFluid();

		if (fluidStack != null && tankTile.getHeightForRender() > 0.05) {
			GL11.glPushMatrix();
			GL11.glDisable(2896);
			try {

				Fluid fluid = fluidStack.getFluid();

				Icon texture = fluid.getStillIcon();
				bindTexture(getFluidSheet(fluid));

				Tessellator t = Tessellator.instance;

				double ySouthEast = tankTile.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.EAST);
				double yNorthEast = tankTile.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.EAST);
				double ySouthWest = tankTile.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.WEST);
				double yNorthWest = tankTile.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.WEST);

				double uMin = texture.getInterpolatedU(0.0);
				double uMax = texture.getInterpolatedU(16.0);
				double vMin = texture.getInterpolatedV(0.0);
				double vMax = texture.getInterpolatedV(16.0);

				double vHeight = vMax - vMin;

				// north side
				t.startDrawingQuads();
				t.addVertexWithUV(0.5, -0.5, -0.5, uMax, vMin); // bottom
				t.addVertexWithUV(-0.5, -0.5, -0.5, uMin, vMin); // bottom
				t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMin, vMin
						+ (vHeight * yNorthWest)); // top
													// north/west
				t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMax, vMin
						+ (vHeight * yNorthEast)); // top
													// north/east
				t.draw();

				// south side
				t.startDrawingQuads();
				t.addVertexWithUV(0.5, -0.5, 0.5, uMin, vMin);
				t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMin, vMin
						+ (vHeight * ySouthEast)); // top
													// south
													// east
				t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMax, vMin
						+ (vHeight * ySouthWest)); // top
													// south
													// west
				t.addVertexWithUV(-0.5, -0.5, 0.5, uMax, vMin);
				t.draw();

				// east side
				t.startDrawingQuads();
				t.addVertexWithUV(0.5, -0.5, -0.5, uMin, vMin);
				t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMin, vMin
						+ (vHeight * yNorthEast)); // top
													// north/east
				t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMax, vMin
						+ (vHeight * ySouthEast)); // top
													// south/east
				t.addVertexWithUV(0.5, -0.5, 0.5, uMax, vMin);
				t.draw();

				// west side
				t.startDrawingQuads();
				t.addVertexWithUV(-0.5, -0.5, 0.5, uMin, vMin);
				t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMin, vMin
						+ (vHeight * ySouthWest)); // top
													// south/west
				t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMax, vMin
						+ (vHeight * yNorthWest)); // top
													// north/west
				t.addVertexWithUV(-0.5, -0.5, -0.5, uMax, vMin);
				t.draw();

				// top
				t.startDrawingQuads();
				t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMax, vMin); // south
																			// east
				t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMin, vMin); // north
																				// east
				t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMin, vMax); // north
																				// west
				t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMax, vMax); // south
																				// west
				t.draw();

				// bottom
				t.startDrawingQuads();
				t.addVertexWithUV(0.5, -0.5, -0.5, uMax, vMin);
				t.addVertexWithUV(0.5, -0.5, 0.5, uMin, vMin);
				t.addVertexWithUV(-0.5, -0.5, 0.5, uMin, vMax);
				t.addVertexWithUV(-0.5, -0.5, -0.5, uMax, vMax);
				t.draw();

			} catch (Exception e) {
				Log.warn(e, "Error during TileEntityTank rendering");
			}
			GL11.glEnable(2896);
			GL11.glPopMatrix();

		}

		GL11.glPopMatrix();
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
