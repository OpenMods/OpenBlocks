package openblocks.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import openblocks.OpenBlocks;
import openmods.renderer.BlockRenderingHandlerBase;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRenderingHandler extends BlockRenderingHandlerBase implements ISimpleBlockRenderingHandler {

	public BlockRenderingHandler() {
		blockRenderers.put(OpenBlocks.Blocks.path, new BlockPathRenderer());
		BlockCanvasRenderer canvasRenderer = new BlockCanvasRenderer();
		blockRenderers.put(OpenBlocks.Blocks.canvas, canvasRenderer);
		blockRenderers.put(OpenBlocks.Blocks.canvasGlass, canvasRenderer);
		blockRenderers.put(OpenBlocks.Blocks.paintCan, new BlockPaintCanRenderer());
	}

	@Override
	public int getRenderId() {
		return OpenBlocks.renderId;
	}

	// doing this for now, as I have a feeling reobfuscation will cry if i move
	// them
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		doRenderInventoryBlock(block, metadata, modelID, renderer);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return doRenderWorldBlock(world, x, y, z, block, modelId, renderer);
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

}
