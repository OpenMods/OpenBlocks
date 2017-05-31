package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import openmods.block.OpenBlock;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockXPDrain extends OpenBlock.FourDirections {

	public BlockXPDrain() {
		super(Material.GLASS);
	}

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
		return layer == BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
}
