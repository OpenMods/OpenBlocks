package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockXPShower extends OpenBlock.FourDirections {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(7.0 / 16.0, 7.0 / 16.0, 0.0 / 16.0, 9.0 / 16.0, 9.0 / 16.0, 9.0 / 16.0);

	public BlockXPShower() {
		super(Material.ROCK);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	// TODO 1.8.9 and you too...
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		Orientation orientation = getOrientation(source, pos);
		return BlockSpaceTransform.instance.mapBlockToWorld(orientation, AABB);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
}
