package openblocks.client.renderer.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import openblocks.Config;
import openblocks.common.block.BlockWorkingProjector;
import openmods.renderer.IBlockRenderer;

public class BlockWorkingProjectorRenderer implements IBlockRenderer<BlockWorkingProjector> {

	private static final float TO_BLOCK_CENTRE = 0.5F;
	private static final int BRIGHTNESS_LEVEL_MAX = 255;
	private static final int BRIGHTNESS_LEVEL_MIN = 0;
	private static final int BRIGHTNESS_LEVEL_DEF = -1;

	private static final float CONE_BEGIN = Config.renderHoloGrid? 0F : 0.25F;
	private static final float CONE_END = Config.renderHoloGrid? 1F : 0.75F;

	@Override
	public void renderInventoryBlock(BlockWorkingProjector block, int metadata, int modelID, RenderBlocks renderer) {
		// Do nothing: Working Projector cannot be obtained in inventory
	}

	@Override
	public boolean renderWorldBlock(final IBlockAccess world, int x, int y, int z, BlockWorkingProjector block, int modelId, RenderBlocks renderer) {
		if (ForgeHooksClient.getWorldRenderPass() == 0) {
			renderer.renderStandardBlock(block, x, y, z);
			return true;
		}

		if (!Config.renderHoloCone) return false;
		if (ForgeHooksClient.getWorldRenderPass() != 1) return false; // Just in case something breaks

		final Tessellator tex = Tessellator.instance;
		tex.addTranslation(x, y + TO_BLOCK_CENTRE, z);

		if (Config.coneBrightness != BRIGHTNESS_LEVEL_DEF) {
			tex.setBrightness(Math.max(BRIGHTNESS_LEVEL_MIN, Math.min(Config.coneBrightness, BRIGHTNESS_LEVEL_MAX)));
		}

		renderCoreWithTex(block.getIcon(world, x, y, z, -1), tex);

		tex.addTranslation(-x, -(y + TO_BLOCK_CENTRE), -z);

		return true;
	}

	private static void renderCoreWithTex(final IIcon cone, final Tessellator tex) {
		final float minU = cone.getMinU();
		final float minV = cone.getMinV();
		final float maxU = cone.getMaxU();
		final float maxV = cone.getMaxV();

		renderNorthFace(tex, minU, minV, maxU, maxV);
		renderWestFace(tex, minU, minV, maxU, maxV);
		renderSouthFace(tex, minU, minV, maxU, maxV);
		renderEastFace(tex, minU, minV, maxU, maxV);
	}

	private static void renderNorthFace(Tessellator tex, float minU, float minV, float maxU, float maxV) {
		tex.addVertexWithUV(CONE_END, 0, 0.25, maxU, maxV);
		tex.addVertexWithUV(1, 1, 0, maxU, minV);
		tex.addVertexWithUV(0, 1, 0, minU, minV);
		tex.addVertexWithUV(CONE_BEGIN, 0, 0.25, minU, maxV);
		tex.addVertexWithUV(CONE_END, 0, 0.25, maxU, maxV);
		tex.addVertexWithUV(CONE_BEGIN, 0, 0.25, minU, maxV);
		tex.addVertexWithUV(0, 1, 0, minU, minV);
		tex.addVertexWithUV(1, 1, 0, maxU, minV);
	}

	private static void renderWestFace(Tessellator tex, float minU, float minV, float maxU, float maxV) {
		tex.addVertexWithUV(0.25, 0, CONE_END, maxU, maxV);
		tex.addVertexWithUV(0, 1, 1, maxU, minV);
		tex.addVertexWithUV(0, 1, 0, minU, minV);
		tex.addVertexWithUV(0.25, 0, CONE_BEGIN, minU, maxV);
		tex.addVertexWithUV(0.25, 0, CONE_END, maxU, maxV);
		tex.addVertexWithUV(0.25, 0, CONE_BEGIN, minU, maxV);
		tex.addVertexWithUV(0, 1, 0, minU, minV);
		tex.addVertexWithUV(0, 1, 1, maxU, minV);
	}

	private static void renderEastFace(Tessellator tex, float minU, float minV, float maxU, float maxV) {
		tex.addVertexWithUV(0.75, 0, CONE_END, maxU, maxV);
		tex.addVertexWithUV(1, 1, 1, maxU, minV);
		tex.addVertexWithUV(1, 1, 0, minU, minV);
		tex.addVertexWithUV(0.75, 0, CONE_BEGIN, minU, maxV);
		tex.addVertexWithUV(0.75, 0, CONE_END, maxU, maxV);
		tex.addVertexWithUV(0.75, 0, CONE_BEGIN, minU, maxV);
		tex.addVertexWithUV(1, 1, 0, minU, minV);
		tex.addVertexWithUV(1, 1, 1, maxU, minV);
	}

	private static void renderSouthFace(Tessellator tex, float minU, float minV, float maxU, float maxV) {
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
