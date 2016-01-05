package openblocks.client.renderer.block;

import net.minecraft.world.IBlockAccess;
import openblocks.common.block.BlockPaintCan;

public class BlockPaintCanRenderer implements IBlockRenderer<BlockPaintCan> {

	@Override
	public void renderInventoryBlock(BlockPaintCan block, int metadata, int modelID, RenderBlocks renderer) {}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockPaintCan block, int modelId, RenderBlocks renderer) {
		block.renderPass = 0;
		renderer.renderStandardBlock(block, x, y, z);
		block.renderPass = 1;
		renderer.renderStandardBlock(block, x, y, z);
		return true;
	}

}
