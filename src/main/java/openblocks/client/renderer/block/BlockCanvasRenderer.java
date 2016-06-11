package openblocks.client.renderer.block;

import java.util.List;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import openblocks.common.block.BlockCanvas;
import openblocks.common.sync.SyncableBlockLayers;
import openblocks.common.sync.SyncableBlockLayers.Layer;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.renderer.FixedRenderBlocks;
import openmods.renderer.IBlockRenderer;
import openmods.utils.render.RenderUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class BlockCanvasRenderer implements IBlockRenderer<BlockCanvas> {

	public FixedRenderBlocks renderBlocks = new FixedRenderBlocks();

	@Override
	public void renderInventoryBlock(BlockCanvas block, int metadata, int modelID, RenderBlocks renderer) {
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
		RenderUtils.renderInventoryBlock(renderer, block, 0);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockCanvas block, int modelId, RenderBlocks renderer) {
		if (renderer.hasOverrideBlockTexture()) {
			// breaking animation handling
			renderer.setRenderBoundsFromBlock(block);
			block.setLayerForRender(BlockCanvas.NO_LAYER);
			block.setSideForRender(BlockCanvas.RENDER_ALL_SIDES);
			renderer.renderStandardBlock(block, x, y, z);
			return true;
		}

		renderBlocks.setWorld(world);
		renderBlocks.setRenderBoundsFromBlock(block);

		boolean visible = false;
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)tile;
			for (int i = 0; i < 6; i++) {
				renderBlocks.setAllFaces(0);
				block.setLayerForRender(-1);
				block.setSideForRender(i);
				if (renderBlocks.renderStandardBlock(block, x, y, z)) {
					visible = true;
					SyncableBlockLayers sideLayersContainer = canvas.getLayersForSide(i);
					List<Layer> layers = sideLayersContainer.getAllLayers();
					for (int l = 0; l < layers.size(); l++) {
						block.setLayerForRender(l);
						byte rot = layers.get(l).getRotation();
						if (rot == 2) {
							rot = 3;
						} else if (rot == 3) {
							rot = 2;
						}
						renderBlocks.setAllFaces(rot);
						renderBlocks.renderStandardBlock(block, x, y, z);
					}
				}
			}
			RenderUtils.resetFacesOnRenderer(renderBlocks);
		}

		return visible;
	}
}
