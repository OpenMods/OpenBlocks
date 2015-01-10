package openblocks.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityTank.IFluidHeightCalculator;
import openmods.utils.TextureUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		TileEntityTank tankTile = (TileEntityTank)tileentity;
		final IFluidHeightCalculator context = tankTile.getRenderFluidHeights();

		IFluidTank internalTank = tankTile.getTank();

		FluidStack fluidStack = internalTank.getFluid();

		if (fluidStack != null) {
			bindTexture(TextureMap.locationBlocksTexture);
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			renderFluid(context, fluidStack);
			GL11.glPopMatrix();
		}
	}

	public static void renderFluid(IFluidHeightCalculator height, FluidStack fluidStack) {
		GL11.glDisable(GL11.GL_LIGHTING);

		final Fluid fluid = fluidStack.getFluid();

		IIcon texture = fluid.getStillIcon();
		final int color;

		if (texture != null) {
			TextureUtils.bindTextureToClient(getFluidSheet(fluid));
			color = fluid.getColor(fluidStack);
		} else {
			TextureUtils.bindDefaultTerrainTexture();
			texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("missingno");
			color = 0xFFFFFFFF;
		}

		Tessellator t = Tessellator.instance;

		final double se = height.calculateHeight(ForgeDirection.SOUTH, ForgeDirection.EAST);
		final double ne = height.calculateHeight(ForgeDirection.NORTH, ForgeDirection.EAST);
		final double sw = height.calculateHeight(ForgeDirection.SOUTH, ForgeDirection.WEST);
		final double nw = height.calculateHeight(ForgeDirection.NORTH, ForgeDirection.WEST);

		final double uMin = texture.getMinU();
		final double uMax = texture.getMaxU();
		final double vMin = texture.getMinV();
		final double vMax = texture.getMaxV();

		final double vHeight = vMax - vMin;

		final float r = (color >> 16 & 0xFF) / 255.0F;
		final float g = (color >> 8 & 0xFF) / 255.0F;
		final float b = (color & 0xFF) / 255.0F;

		t.startDrawingQuads();
		t.setColorOpaque_F(r, g, b);
		// north
		t.addVertexWithUV(1, 0, 0, uMax, vMin);
		t.addVertexWithUV(0, 0, 0, uMin, vMin);
		t.addVertexWithUV(0, nw, 0, uMin, vMin + (vHeight * nw));
		t.addVertexWithUV(1, ne, 0, uMax, vMin + (vHeight * ne));

		// south
		t.addVertexWithUV(1, 0, 1, uMin, vMin);
		t.addVertexWithUV(1, se, 1, uMin, vMin + (vHeight * se));
		t.addVertexWithUV(0, sw, 1, uMax, vMin + (vHeight * sw));
		t.addVertexWithUV(0, 0, 1, uMax, vMin);

		// east
		t.addVertexWithUV(1, 0, 0, uMin, vMin);
		t.addVertexWithUV(1, ne, 0, uMin, vMin + (vHeight * ne));
		t.addVertexWithUV(1, se, 1, uMax, vMin + (vHeight * se));
		t.addVertexWithUV(1, 0, 1, uMax, vMin);

		// west
		t.addVertexWithUV(0, 0, 1, uMin, vMin);
		t.addVertexWithUV(0, sw, 1, uMin, vMin + (vHeight * sw));
		t.addVertexWithUV(0, nw, 0, uMax, vMin + (vHeight * nw));
		t.addVertexWithUV(0, 0, 0, uMax, vMin);

		// top
		t.addVertexWithUV(1, se, 1, uMax, vMin);
		t.addVertexWithUV(1, ne, 0, uMin, vMin);
		t.addVertexWithUV(0, nw, 0, uMin, vMax);
		t.addVertexWithUV(0, sw, 1, uMax, vMax);

		// bottom
		t.addVertexWithUV(1, 0, 0, uMax, vMin);
		t.addVertexWithUV(1, 0, 1, uMin, vMin);
		t.addVertexWithUV(0, 0, 1, uMin, vMax);
		t.addVertexWithUV(0, 0, 0, uMax, vMax);
		t.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
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
