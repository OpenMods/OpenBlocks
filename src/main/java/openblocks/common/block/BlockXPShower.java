package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockXPShower extends OpenBlock.FourDirections {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(7.0 / 16.0, 7.0 / 16.0, 7.0 / 16.0, 9.0 / 16.0, 9.0 / 16.0, 16.0 / 16.0);

	public BlockXPShower() {
		super(Material.ROCK);
		setPlacementMode(BlockPlacementMode.SURFACE);
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

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
		switch (side) {
			case NORTH:
			case SOUTH:
			case EAST:
			case WEST:
				return true;
			default:
				return false;
		}
	}
}
