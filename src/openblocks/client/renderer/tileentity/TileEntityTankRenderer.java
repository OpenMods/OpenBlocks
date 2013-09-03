package openblocks.client.renderer.tileentity;

import net.minecraft.block.Block;
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
import net.minecraftforge.fluids.FluidTank;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityTank;

import org.lwjgl.opengl.GL11;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();
	private static final ResourceLocation texture = new ResourceLocation("openblocks", "textures/models/sprinkler.png");
	
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

		func_110628_a(TextureMap.field_110575_b);
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

		FluidTank internalTank = tankTile.getInternalTank();

		FluidStack fluidStack = internalTank.getFluid();

		if (fluidStack != null && tankTile.getHeightForRender() > 0.05) {

			Block block = null;
			Icon texture = null;

			GL11.glPushMatrix();
			GL11.glDisable(2896);
			try {

				Fluid fluid = fluidStack.getFluid();
				
				if (fluid.getBlockID() > 0) {
					block = Block.blocksList[fluid.getBlockID()];
					texture = fluidStack.getFluid().getStillIcon();
				} else {
					block = Block.waterStill;
					texture = fluidStack.getFluid().getStillIcon();
				}
				func_110628_a(getFluidSheet(fluid));

				Tessellator t = Tessellator.instance;

				double ySouthEast = tankTile.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.EAST);
				double yNorthEast = tankTile.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.EAST);
				double ySouthWest = tankTile.getLiquidHeightForSide(ForgeDirection.SOUTH, ForgeDirection.WEST);
				double yNorthWest = tankTile.getLiquidHeightForSide(ForgeDirection.NORTH, ForgeDirection.WEST);

				double uMin = (double)texture.getInterpolatedU(0.0);
				double uMax = (double)texture.getInterpolatedU(16.0);
				double vMin = (double)texture.getInterpolatedV(0.0);
				double vMax = (double)texture.getInterpolatedV(16.0);

				double vHeight = vMax - vMin;

				// north side
				t.startDrawingQuads();
				t.addVertexWithUV(0.5, -0.5, -0.5, uMax, vMin); // bottom
				t.addVertexWithUV(-0.5, -0.5, -0.5, uMin, vMin); // bottom
				t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMin, vMin
						+ (vHeight * yNorthWest)); // top north/west
				t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMax, vMin
						+ (vHeight * yNorthEast)); // top north/east
				t.draw();

				// south side
				t.startDrawingQuads();
				t.addVertexWithUV(0.5, -0.5, 0.5, uMin, vMin);
				t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMin, vMin
						+ (vHeight * ySouthEast)); // top south east
				t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMax, vMin
						+ (vHeight * ySouthWest)); // top south west
				t.addVertexWithUV(-0.5, -0.5, 0.5, uMax, vMin);
				t.draw();

				// east side
				t.startDrawingQuads();
				t.addVertexWithUV(0.5, -0.5, -0.5, uMin, vMin);
				t.addVertexWithUV(0.5, -0.5 + yNorthEast, -0.5, uMin, vMin
						+ (vHeight * yNorthEast)); // top north/east
				t.addVertexWithUV(0.5, -0.5 + ySouthEast, 0.5, uMax, vMin
						+ (vHeight * ySouthEast)); // top south/east
				t.addVertexWithUV(0.5, -0.5, 0.5, uMax, vMin);
				t.draw();

				// west side
				t.startDrawingQuads();
				t.addVertexWithUV(-0.5, -0.5, 0.5, uMin, vMin);
				t.addVertexWithUV(-0.5, -0.5 + ySouthWest, 0.5, uMin, vMin
						+ (vHeight * ySouthWest)); // top south/west
				t.addVertexWithUV(-0.5, -0.5 + yNorthWest, -0.5, uMax, vMin
						+ (vHeight * yNorthWest)); // top north/west
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

			} catch (Exception e) {}
			GL11.glEnable(2896);
			GL11.glPopMatrix();

		}

		GL11.glPopMatrix();
		GL11.glPopMatrix();

	}
	
	public static ResourceLocation getFluidSheet(FluidStack liquid) {
		if (liquid == null)
			return TextureMap.field_110575_b;
		return getFluidSheet(liquid.getFluid());
	}

	public static ResourceLocation getFluidSheet(Fluid liquid) {
		return TextureMap.field_110575_b;
	}
}
