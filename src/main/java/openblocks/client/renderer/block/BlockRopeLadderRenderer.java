package openblocks.client.renderer.block;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import openblocks.common.block.BlockRopeLadder;

public class BlockRopeLadderRenderer implements IBlockRenderer<BlockRopeLadder> {

	@Override
	public void renderInventoryBlock(BlockRopeLadder block, int metadata, int modelID, RenderBlocks renderer) {
		// nothing, renders as 2d item
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockRopeLadder block, int modelId, RenderBlocks renderer) {
		final Tessellator tessellator = Tessellator.instance;
		final IIcon icon = renderer.hasOverrideBlockTexture()? renderer.overrideBlockTexture : block.getIcon(0, 0);

		tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

		final double um = icon.getMinU();
		final double vm = icon.getMinV();
		final double up = icon.getMaxU();
		final double vp = icon.getMaxV();

		int meta = world.getBlockMetadata(x, y, z);
		ForgeDirection dir = block.getOrientation(meta).north();

		float d = BlockRopeLadder.RENDER_THICKNESS;
		switch (dir) {
			case WEST:
				tessellator.addVertexWithUV(x + d, y + 1, z + 1, um, vm);
				tessellator.addVertexWithUV(x + d, y + 0, z + 1, um, vp);
				tessellator.addVertexWithUV(x + d, y + 0, z + 0, up, vp);
				tessellator.addVertexWithUV(x + d, y + 1, z + 0, up, vm);

				tessellator.addVertexWithUV(x + d, y + 1, z + 1, um, vm);
				tessellator.addVertexWithUV(x + d, y + 1, z + 0, up, vm);
				tessellator.addVertexWithUV(x + d, y + 0, z + 0, up, vp);
				tessellator.addVertexWithUV(x + d, y + 0, z + 1, um, vp);
				break;
			case EAST:
				tessellator.addVertexWithUV((x + 1) - d, y + 0, z + 1, up, vp);
				tessellator.addVertexWithUV((x + 1) - d, y + 1, z + 1, up, vm);
				tessellator.addVertexWithUV((x + 1) - d, y + 1, z + 0, um, vm);
				tessellator.addVertexWithUV((x + 1) - d, y + 0, z + 0, um, vp);

				tessellator.addVertexWithUV((x + 1) - d, y + 0, z + 1, up, vp);
				tessellator.addVertexWithUV((x + 1) - d, y + 0, z + 0, um, vp);
				tessellator.addVertexWithUV((x + 1) - d, y + 1, z + 0, um, vm);
				tessellator.addVertexWithUV((x + 1) - d, y + 1, z + 1, up, vm);
				break;
			case NORTH:
				tessellator.addVertexWithUV(x + 1, y + 0, z + d, up, vp);
				tessellator.addVertexWithUV(x + 1, y + 1, z + d, up, vm);
				tessellator.addVertexWithUV(x + 0, y + 1, z + d, um, vm);
				tessellator.addVertexWithUV(x + 0, y + 0, z + d, um, vp);

				tessellator.addVertexWithUV(x + 1, y + 0, z + d, up, vp);
				tessellator.addVertexWithUV(x + 0, y + 0, z + d, um, vp);
				tessellator.addVertexWithUV(x + 0, y + 1, z + d, um, vm);
				tessellator.addVertexWithUV(x + 1, y + 1, z + d, up, vm);
				break;
			default:
			case SOUTH:
				tessellator.addVertexWithUV(x + 1, y + 1, (z + 1) - d, um, vm);
				tessellator.addVertexWithUV(x + 1, y + 0, (z + 1) - d, um, vp);
				tessellator.addVertexWithUV(x + 0, y + 0, (z + 1) - d, up, vp);
				tessellator.addVertexWithUV(x + 0, y + 1, (z + 1) - d, up, vm);

				tessellator.addVertexWithUV(x + 1, y + 1, (z + 1) - d, um, vm);
				tessellator.addVertexWithUV(x + 0, y + 1, (z + 1) - d, up, vm);
				tessellator.addVertexWithUV(x + 0, y + 0, (z + 1) - d, up, vp);
				tessellator.addVertexWithUV(x + 1, y + 0, (z + 1) - d, um, vp);
		}

		return true;
	}
}
