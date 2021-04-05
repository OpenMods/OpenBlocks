package openblocks.client.renderer.tileentity.tank;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.tileentity.TileEntityTank;
import openmods.utils.Diagonal;

public class TileEntityTankRenderer extends TileEntityRenderer<TileEntityTank> {
	public TileEntityTankRenderer(TileEntityRendererDispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void render(TileEntityTank tankTile, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLightIn, int combinedOverlayIn) {
		if (tankTile.isRemoved()) {
			return;
		}

		final ITankRenderFluidData data = tankTile.getRenderFluidData();

		if (data != null && data.hasFluid()) {
			final World world = tankTile.getWorld();
			final float time = world.getGameTime() + partialTicks;
			IVertexBuilder output = buffer.getBuffer(RenderType.getTranslucent());
			renderFluid(output, matrixStack.getLast().getMatrix(), data, time, combinedLightIn, combinedOverlayIn);
		}
	}

	private static void addVertex(IVertexBuilder wr, Matrix4f matrix, float x, float y, float z, float u, float v, int r, int g, int b, int a, int lightmap, int overlay, Vector3f normal) {
		wr.pos(matrix, x, y, z).color(r, g, b, a).tex(u, v).overlay(overlay).lightmap(lightmap).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
	}

	private static void renderFluid(final IVertexBuilder wr, final Matrix4f matrix, final ITankRenderFluidData data, float time, int combinedLights, int combinedOverlay) {
		final float se = data.getCornerFluidLevel(Diagonal.SE, time);
		final float ne = data.getCornerFluidLevel(Diagonal.NE, time);
		final float sw = data.getCornerFluidLevel(Diagonal.SW, time);
		final float nw = data.getCornerFluidLevel(Diagonal.NW, time);

		final float center = data.getCenterFluidLevel(time);

		final FluidStack fluid = data.getFluid();
		final FluidAttributes attributes = fluid.getFluid().getAttributes();
		final TextureAtlasSprite texture = Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(attributes.getStillTexture(fluid));
		final int color = attributes.getColor(fluid);
		final int r = ((color >> 16) & 0xFF);
		final int g = ((color >> 8) & 0xFF);
		final int b = ((color >> 0) & 0xFF);
		final int a = ((color >> 24) & 0xFF);

		final float uMin = texture.getMinU();
		final float uMax = texture.getMaxU();
		final float vMin = texture.getMinV();
		final float vMax = texture.getMaxV();

		final float vHeight = vMax - vMin;

		if (data.shouldRenderFluidWall(Direction.NORTH) && (nw > 0 || ne > 0)) {
			addVertex(wr, matrix, 1, 0, 0, uMax, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.ZN);
			addVertex(wr, matrix, 0, 0, 0, uMin, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.ZN);
			addVertex(wr, matrix, 0, nw, 0, uMin, vMin + (vHeight * nw), r, g, b, a, combinedLights, combinedOverlay, Vector3f.ZN);
			addVertex(wr, matrix, 1, ne, 0, uMax, vMin + (vHeight * ne), r, g, b, a, combinedLights, combinedOverlay, Vector3f.ZN);
		}

		if (data.shouldRenderFluidWall(Direction.SOUTH) && (se > 0 || sw > 0)) {
			addVertex(wr, matrix, 1, 0, 1, uMin, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.ZP);
			addVertex(wr, matrix, 1, se, 1, uMin, vMin + (vHeight * se), r, g, b, a, combinedLights, combinedOverlay, Vector3f.ZP);
			addVertex(wr, matrix, 0, sw, 1, uMax, vMin + (vHeight * sw), r, g, b, a, combinedLights, combinedOverlay, Vector3f.ZP);
			addVertex(wr, matrix, 0, 0, 1, uMax, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.ZP);
		}

		if (data.shouldRenderFluidWall(Direction.EAST) && (ne > 0 || se > 0)) {
			addVertex(wr, matrix, 1, 0, 0, uMin, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.XP);
			addVertex(wr, matrix, 1, ne, 0, uMin, vMin + (vHeight * ne), r, g, b, a, combinedLights, combinedOverlay, Vector3f.XP);
			addVertex(wr, matrix, 1, se, 1, uMax, vMin + (vHeight * se), r, g, b, a, combinedLights, combinedOverlay, Vector3f.XP);
			addVertex(wr, matrix, 1, 0, 1, uMax, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.XP);
		}

		if (data.shouldRenderFluidWall(Direction.WEST) && (sw > 0 || nw > 0)) {
			addVertex(wr, matrix, 0, 0, 1, uMin, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.XN);
			addVertex(wr, matrix, 0, sw, 1, uMin, vMin + (vHeight * sw), r, g, b, a, combinedLights, combinedOverlay, Vector3f.XN);
			addVertex(wr, matrix, 0, nw, 0, uMax, vMin + (vHeight * nw), r, g, b, a, combinedLights, combinedOverlay, Vector3f.XN);
			addVertex(wr, matrix, 0, 0, 0, uMax, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.XN);
		}

		if (data.shouldRenderFluidWall(Direction.UP)) {
			final float uMid = (uMax + uMin) / 2;
			final float vMid = (vMax + vMin) / 2;

			// Normals are approximate
			addVertex(wr, matrix, 0.5f, center, 0.5f, uMid, vMid, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YP);
			addVertex(wr, matrix, 1, se, 1, uMax, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YP);
			addVertex(wr, matrix, 1, ne, 0, uMin, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YP);
			addVertex(wr, matrix, 0, nw, 0, uMin, vMax, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YP);

			addVertex(wr, matrix, 0, sw, 1, uMax, vMax, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YP);
			addVertex(wr, matrix, 1, se, 1, uMax, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YP);
			addVertex(wr, matrix, 0.5f, center, 0.5f, uMid, vMid, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YP);
			addVertex(wr, matrix, 0, nw, 0, uMin, vMax, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YP);
		}

		if (data.shouldRenderFluidWall(Direction.DOWN)) {
			addVertex(wr, matrix, 1, 0, 0, uMax, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YN);
			addVertex(wr, matrix, 1, 0, 1, uMin, vMin, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YN);
			addVertex(wr, matrix, 0, 0, 1, uMin, vMax, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YN);
			addVertex(wr, matrix, 0, 0, 0, uMax, vMax, r, g, b, a, combinedLights, combinedOverlay, Vector3f.YN);
		}
	}
}
