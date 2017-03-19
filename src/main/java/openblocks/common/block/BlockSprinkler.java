package openblocks.common.block;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntitySprinkler;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockSprinkler extends OpenBlock.TwoDirections {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.3, 0.0, 0.0, 0.7, 0.3, 1.0);

	public BlockSprinkler() {
		super(Material.WATER);
		setDefaultState(getDefaultState().withProperty(BlockLiquid.LEVEL, 1));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		// 1.8.9 Hack, crashes otherwise
		return new BlockStateContainer(this, getPropertyOrientation(), BlockLiquid.LEVEL);
	}

	// TODO 1.8.9 room for improvments?
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	// TODO 1.10 orientation to block state?
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntitySprinkler sprinkler = getTileEntity(source, pos, TileEntitySprinkler.class);
		if (sprinkler != null) {
			final Orientation orientation = sprinkler.getOrientation();
			return BlockSpaceTransform.instance.mapBlockToWorld(orientation, AABB);
		}

		return AABB;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		// TODO 1.8.9 verify
		return isOnTopOfSolidBlock(world, pos, side);
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
		return false;
	}
}
