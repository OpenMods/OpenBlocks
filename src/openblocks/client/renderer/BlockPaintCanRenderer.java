package openblocks.client.renderer;

import openblocks.common.block.BlockPaintCan;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class BlockPaintCanRenderer implements IBlockRenderer {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		BlockPaintCan blockPaintCan = (BlockPaintCan) block;
		blockPaintCan.renderPass = 0;
		renderer.renderStandardBlock(block, x, y, z);
		blockPaintCan.renderPass++;
		renderer.renderStandardBlock(block, x, y, z);
		return true;
	}

}
