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
import openblocks.common.tileentity.TileEntityTank;
import openblocks.common.tileentity.TileEntityTank.ITankRenderData;
import openmods.utils.Diagonal;
import openmods.utils.TextureUtils;

import org.lwjgl.opengl.GL11;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer {

	RenderBlocks renderBlocks = new RenderBlocks();

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f) {
		TileEntityTank tankTile = (TileEntityTank)te;

		ITankRenderData data = tankTile.getRenderData();

		if (data.hasFluid()) {
			bindTexture(TextureMap.locationBlocksTexture);
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			// it just looks broken with blending
			// GL11.glEnable(GL11.GL_BLEND);
			// OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			float time = te.getWorldObj().getTotalWorldTime() + f;
			renderFluid(data, time);
			// GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
	}

	public static void renderFluid(ITankRenderData data, float time) {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor4f(1, 1, 1, 1);
		FluidStack fluidStack = data.getFluid();
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

		final double se = data.getCornerFluidLevel(Diagonal.SE, time);
		final double ne = data.getCornerFluidLevel(Diagonal.NE, time);
		final double sw = data.getCornerFluidLevel(Diagonal.SW, time);
		final double nw = data.getCornerFluidLevel(Diagonal.NW, time);

		final double center = data.getCenterFluidLevel(time);

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

		if (data.shouldRenderFluidWall(ForgeDirection.NORTH) && (nw > 0 || ne > 0)) {
			t.addVertexWithUV(1, 0, 0, uMax, vMin);
			t.addVertexWithUV(0, 0, 0, uMin, vMin);
			t.addVertexWithUV(0, nw, 0, uMin, vMin + (vHeight * nw));
			t.addVertexWithUV(1, ne, 0, uMax, vMin + (vHeight * ne));
		}

		if (data.shouldRenderFluidWall(ForgeDirection.SOUTH) && (se > 0 || sw > 0)) {
			t.addVertexWithUV(1, 0, 1, uMin, vMin);
			t.addVertexWithUV(1, se, 1, uMin, vMin + (vHeight * se));
			t.addVertexWithUV(0, sw, 1, uMax, vMin + (vHeight * sw));
			t.addVertexWithUV(0, 0, 1, uMax, vMin);
		}

		if (data.shouldRenderFluidWall(ForgeDirection.EAST) && (ne > 0 || se > 0)) {
			t.addVertexWithUV(1, 0, 0, uMin, vMin);
			t.addVertexWithUV(1, ne, 0, uMin, vMin + (vHeight * ne));
			t.addVertexWithUV(1, se, 1, uMax, vMin + (vHeight * se));
			t.addVertexWithUV(1, 0, 1, uMax, vMin);
		}

		if (data.shouldRenderFluidWall(ForgeDirection.WEST) && (sw > 0 || nw > 0)) {
			t.addVertexWithUV(0, 0, 1, uMin, vMin);
			t.addVertexWithUV(0, sw, 1, uMin, vMin + (vHeight * sw));
			t.addVertexWithUV(0, nw, 0, uMax, vMin + (vHeight * nw));
			t.addVertexWithUV(0, 0, 0, uMax, vMin);
		}

		if (data.shouldRenderFluidWall(ForgeDirection.UP)) {
			final double uMid = (uMax + uMin) / 2;
			final double vMid = (vMax + vMin) / 2;

			t.addVertexWithUV(0.5, center, 0.5, uMid, vMid);
			t.addVertexWithUV(1, se, 1, uMax, vMin);
			t.addVertexWithUV(1, ne, 0, uMin, vMin);
			t.addVertexWithUV(0, nw, 0, uMin, vMax);

			t.addVertexWithUV(0, sw, 1, uMax, vMax);
			t.addVertexWithUV(1, se, 1, uMax, vMin);
			t.addVertexWithUV(0.5, center, 0.5, uMid, vMid);
			t.addVertexWithUV(0, nw, 0, uMin, vMax);

		}

		if (data.shouldRenderFluidWall(ForgeDirection.DOWN)) {
			t.addVertexWithUV(1, 0, 0, uMax, vMin);
			t.addVertexWithUV(1, 0, 1, uMin, vMin);
			t.addVertexWithUV(0, 0, 1, uMin, vMax);
			t.addVertexWithUV(0, 0, 0, uMax, vMax);
		}
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
