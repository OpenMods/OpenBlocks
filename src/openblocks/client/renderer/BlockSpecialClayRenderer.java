package openblocks.client.renderer;

import openblocks.common.block.OpenBlock;
import openblocks.common.tileentity.TileEntitySpecialStainedClay;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

public class BlockSpecialClayRenderer implements IBlockRenderer {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		// TODO Auto-generated method stub

	}

	/**
	 * Ok, this is starting to seem a bit silly. It didn't seem much at first, but in theory:
	 * 1536 squares per block. If you had a wall of these you're really going to feel it.  like.. really.
	 * Caching a texture seems like it could be a better way to go.
	 */
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int metadata = world.getBlockMetadata(x, y, z);
		ForgeDirection rotation = ForgeDirection.getOrientation(metadata);
		BlockRenderingHandler.rotateFacesOnRenderer((OpenBlock)block, rotation, renderer);
		Tessellator tessellator = Tessellator.instance;
		renderer.renderStandardBlock(block, x, y, z);
		BlockRenderingHandler.resetFacesOnRenderer(renderer);
		tessellator.setColorOpaque(0, 0, 0);
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te instanceof TileEntitySpecialStainedClay) {
			TileEntitySpecialStainedClay clay = (TileEntitySpecialStainedClay) te;
			byte[] texture = clay.getTexture();
			double pSize = (double) 1 / 16;
			for (int px = 0, i = 0; px < 16; px++) {
				for (int py = 0; py < 16; py++, i++) {
					int bitPart = i % 2;
					int textureIndex = i / 2;
					if (textureIndex < texture.length) {
						double pxStart = (pSize * px) + x;
						double pyStart = (pSize * py) + y;
						byte byteVal = texture[textureIndex];
						byte val = 0;
						if (bitPart == 0) {
							val = (byte)(byteVal >> 4);
						}else {
							val = (byte)(byteVal & 0xF);
						}
						int color = val < 8 ? 0 : 0xFF;
						int opac = 31 * Math.min(8, Math.abs(val - 8));
						tessellator.setColorRGBA(color, color, color, opac);
						
						tessellator.addVertex(pxStart + pSize, 	pyStart, 	z-0.001);
						tessellator.addVertex(pxStart, 			pyStart, 	z-0.001);
						tessellator.addVertex(pxStart, 	 		pyStart + pSize, z-0.001);
						tessellator.addVertex(pxStart + pSize, 	pyStart + pSize, z-0.001);
					}
				}
			}
		}
		return true;
	}

}
