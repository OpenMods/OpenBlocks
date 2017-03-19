package openblocks.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import openblocks.client.renderer.tileentity.tank.ITankRenderFluidData;
import openblocks.common.tileentity.TileEntityTank;
import openmods.renderer.TessellatorPool;
import openmods.renderer.TessellatorPool.WorldRendererUser;
import openmods.utils.Diagonal;
import openmods.utils.TextureUtils;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;

public class TileEntityTankRenderer extends TileEntitySpecialRenderer<TileEntityTank> {

	@Override
	public void renderTileEntityAt(TileEntityTank tankTile, double x, double y, double z, float partialTicks, int damageProgress) {
		if (tankTile.isInvalid()) return;

		final ITankRenderFluidData data = tankTile.getRenderFluidData();

		if (data != null && data.hasFluid()) {
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			// it just looks broken with blending
			// GlStateManager.enableBlend();
			// OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
			// I just can't get it right
			GlStateManager.disableBlend();
			final float time = tankTile.getWorld().getTotalWorldTime() + partialTicks;
			renderFluid(data, time);
			// GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
	}

	private static void addVertexWithUV(VertexBuffer wr, double x, double y, double z, double u, double v) {
		wr.pos(x, y, z).tex(u, v).endVertex();
	}

	public static void renderFluid(final ITankRenderFluidData data, float time) {
		GlStateManager.disableLighting();
		GlStateManager.color(1, 1, 1, 1);
		FluidStack fluidStack = data.getFluid();
		final Fluid fluid = fluidStack.getFluid();

		TextureAtlasSprite texture = TextureUtils.getFluidTexture(fluidStack);
		final int color;

		TextureUtils.bindTextureToClient(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (texture != null) {
			color = fluid.getColor(fluidStack);
		} else {
			texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("missingno");
			color = 0xFFFFFFFF;
		}

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

		RenderUtils.setColor(color);

		TessellatorPool.instance.startDrawing(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX, new WorldRendererUser() {

			@Override
			public void execute(VertexBuffer wr) {

				if (data.shouldRenderFluidWall(EnumFacing.NORTH) && (nw > 0 || ne > 0)) {
					addVertexWithUV(wr, 1, 0, 0, uMax, vMin);
					addVertexWithUV(wr, 0, 0, 0, uMin, vMin);
					addVertexWithUV(wr, 0, nw, 0, uMin, vMin + (vHeight * nw));
					addVertexWithUV(wr, 1, ne, 0, uMax, vMin + (vHeight * ne));
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
		});
		GlStateManager.enableLighting();
	}

	public static ResourceLocation getFluidSheet(FluidStack liquid) {
		if (liquid == null) return TextureMap.LOCATION_BLOCKS_TEXTURE;
		return getFluidSheet(liquid.getFluid());
	}

	/**
	 * @param liquid
	 */
	public static ResourceLocation getFluidSheet(Fluid liquid) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
}
