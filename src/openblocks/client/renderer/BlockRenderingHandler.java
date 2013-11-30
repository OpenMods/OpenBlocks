package openblocks.client.renderer;

import openblocks.OpenBlocks;
import openmods.renderer.BlockRenderingHandlerBase;

public class BlockRenderingHandler extends BlockRenderingHandlerBase {

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
}
