package openblocks.client.renderer.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import openblocks.Config;
import openblocks.common.block.BlockWorkingProjector;
import openmods.renderer.IBlockRenderer;

public class BlockWorkingProjectorRenderer implements IBlockRenderer<BlockWorkingProjector> {

	public static int renderPass = 0;

	private static final float TO_BLOCK_CENTRE = 0.5F;
	private static final int BRIGHTNESS_LEVEL_MAX = 255;
	private static final int BRIGHTNESS_LEVEL_MIN = 0;
	private static final int BRIGHTNESS_LEVEL_DEF = -1;

	private static final float CONE_BEGIN = Config.renderHoloGrid ? 0F : 0.25F;
	private static final float CONE_END = Config.renderHoloGrid ? 1F : 0.75F;

	@Override
	public void renderInventoryBlock(final BlockWorkingProjector block,
									 final int metadata,
									 final int modelID,
									 final RenderBlocks renderer) {
		// Do nothing: Working Projector cannot be obtained in inventory
	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world,
									final int x,
									final int y,
									final int z,
									final BlockWorkingProjector block,
									final int modelId,
									final RenderBlocks renderer) {
		if (renderPass == 0) {
			renderer.renderStandardBlock(block, x, y, z);
			return true;
		}

		if (!Config.renderHoloCone) return false;
		if (renderPass != 1) return false; // Just in case something breaks

		final Tessellator tex = Tessellator.instance;
		tex.addTranslation(x, y + TO_BLOCK_CENTRE, z);

		if (Config.coneBrightness != BRIGHTNESS_LEVEL_DEF) {
			tex.setBrightness(Math.max(BRIGHTNESS_LEVEL_MIN, Math.min(Config.coneBrightness, BRIGHTNESS_LEVEL_MAX)));
		}

		this.renderCoreWithTex(block.getIcon(world, x, y, z, -1), tex);

		tex.addTranslation(-x, -(y + TO_BLOCK_CENTRE), -z);

		return true;
	}

	private void renderCoreWithTex(final IIcon cone, final Tessellator tex) {
		final float minU = cone.getMinU();
		final float minV = cone.getMinV();
		final float maxU = cone.getMaxU();
		final float maxV = cone.getMaxV();

		this.renderNorthFace(tex, minU, minV, maxU, maxV);
		this.renderWestFace(tex, minU, minV, maxU, maxV);
		this.renderSouthFace(tex, minU, minV, maxU, maxV);
		this.renderEastFace(tex, minU, minV, maxU, maxV);
	}

	private void renderNorthFace(final Tessellator tex,
								 final float minU,
								 final float minV,
								 final float maxU,
								 final float maxV) {
		tex.addVertexWithUV(CONE_END, 0, 0.25, maxU, maxV);
		tex.addVertexWithUV(1, 1, 0, maxU, minV);
		tex.addVertexWithUV(0, 1, 0, minU, minV);
		tex.addVertexWithUV(CONE_BEGIN, 0, 0.25, minU, maxV);
		tex.addVertexWithUV(CONE_END, 0, 0.25, maxU, maxV);
		tex.addVertexWithUV(CONE_BEGIN, 0, 0.25, minU, maxV);
		tex.addVertexWithUV(0, 1, 0, minU, minV);
		tex.addVertexWithUV(1, 1, 0, maxU, minV);
	}

	private void renderWestFace(final Tessellator tex,
								final float minU,
								final float minV,
								final float maxU,
								final float maxV) {
		tex.addVertexWithUV(0.25, 0, CONE_END, maxU, maxV);
		tex.addVertexWithUV(0, 1, 1, maxU, minV);
		tex.addVertexWithUV(0, 1, 0, minU, minV);
		tex.addVertexWithUV(0.25, 0, CONE_BEGIN, minU, maxV);
		tex.addVertexWithUV(0.25, 0, CONE_END, maxU, maxV);
		tex.addVertexWithUV(0.25, 0, CONE_BEGIN, minU, maxV);
		tex.addVertexWithUV(0, 1, 0, minU, minV);
		tex.addVertexWithUV(0, 1, 1, maxU, minV);
	}

	private void renderEastFace(final Tessellator tex,
								final float minU,
								final float minV,
								final float maxU,
								final float maxV) {
		tex.addVertexWithUV(0.75, 0, CONE_END, maxU, maxV);
		tex.addVertexWithUV(1, 1, 1, maxU, minV);
		tex.addVertexWithUV(1, 1, 0, minU, minV);
		tex.addVertexWithUV(0.75, 0, CONE_BEGIN, minU, maxV);
		tex.addVertexWithUV(0.75, 0, CONE_END, maxU, maxV);
		tex.addVertexWithUV(0.75, 0, CONE_BEGIN, minU, maxV);
		tex.addVertexWithUV(1, 1, 0, minU, minV);
		tex.addVertexWithUV(1, 1, 1, maxU, minV);
	}

	private void renderSouthFace(final Tessellator tex,
								 final float minU,
								 final float minV,
								 final float maxU,
								 final float maxV) {
		tex.addVertexWithUV(CONE_END, 0, 0.75, maxU, maxV);
		tex.addVertexWithUV(1, 1, 1, maxU, minV);
		tex.addVertexWithUV(0, 1, 1, minU, minV);
		tex.addVertexWithUV(CONE_BEGIN, 0, 0.75, minU, maxV);
		tex.addVertexWithUV(CONE_END, 0, 0.75, maxU, maxV);
		tex.addVertexWithUV(CONE_BEGIN, 0, 0.75, minU, maxV);
		tex.addVertexWithUV(0, 1, 1, minU, minV);
		tex.addVertexWithUV(1, 1, 1, maxU, minV);
	}
}
