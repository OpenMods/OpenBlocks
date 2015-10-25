package openblocks.client.renderer.block;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import openblocks.common.block.BlockSky;
import openmods.renderer.IBlockRenderer;
import openmods.utils.render.RenderUtils;

public class BlockSkyRenderer implements IBlockRenderer<BlockSky> {

	@Override
	public void renderInventoryBlock(BlockSky block, int metadata, int modelID, RenderBlocks renderer) {
		RenderUtils.renderInventoryBlock(renderer, block, 0);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockSky block, int modelId, RenderBlocks renderer) {
		int meta = world.getBlockMetadata(x, y, z);
		return !BlockSky.isActive(meta) && renderer.renderStandardBlock(block, x, y, z);
	}
}
