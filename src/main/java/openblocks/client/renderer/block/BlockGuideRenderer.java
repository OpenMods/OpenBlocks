package openblocks.client.renderer.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.block.BlockGuide;
import openmods.renderer.IBlockRenderer;
import openmods.utils.render.RenderUtils;

public class BlockGuideRenderer implements IBlockRenderer<BlockGuide> {

	private static final double UNIT = 1.0 / 16.0;

	@Override
	public void renderInventoryBlock(BlockGuide block, int metadata, int modelID, RenderBlocks renderer) {
		renderer.setOverrideBlockTexture(block.getCenterTexture());
		renderer.setRenderBounds(6 * UNIT, 6 * UNIT, 6 * UNIT, 10 * UNIT, 10 * UNIT, 10 * UNIT);
		RenderUtils.renderInventoryBlockNoBounds(renderer, block, ForgeDirection.SOUTH);

		renderer.clearOverrideBlockTexture();

		RenderUtils.renderInventoryBlock(renderer, block, ForgeDirection.SOUTH);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockGuide block, int modelId, RenderBlocks renderer) {
		final boolean result = renderer.renderStandardBlock(block, x, y, z);

		if (result) {
			renderer.setOverrideBlockTexture(block.getCenterTexture());
			renderer.renderAllFaces = true;
			renderer.setRenderBounds(6 * UNIT, 6 * UNIT, 6 * UNIT, 10 * UNIT, 10 * UNIT, 10 * UNIT);
			renderer.renderStandardBlock(block, x, y, z);
			renderer.renderAllFaces = false;
			renderer.clearOverrideBlockTexture();
		}

		return result;
	}
}
