package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityFlag;
import openmods.block.OpenBlock;
import openmods.colors.RGB;
import openmods.geometry.Orientation;

public class BlockFlag extends OpenBlock.SixDirections {

	public static final RGB[] COLORS = {
			new RGB(20, 198, 0),
			new RGB(41, 50, 156),
			new RGB(221, 0, 0),
			new RGB(255, 174, 201),
			new RGB(185, 122, 87),
			new RGB(181, 230, 29),
			new RGB(0, 162, 232),
			new RGB(128, 0, 64),
			new RGB(255, 242, 0),
			new RGB(255, 127, 39),
			new RGB(255, 45, 45),
			new RGB(255, 23, 151),
			new RGB(195, 195, 195),
			new RGB(163, 73, 164),
			new RGB(0, 0, 0),
			new RGB(255, 255, 255)
	};

	public BlockFlag() {
		super(Material.CIRCUITS);

		setPlacementMode(BlockPlacementMode.SURFACE);
		setInventoryRenderOrientation(Orientation.XN_YN);
	}

	// TODO 1.8.9 Ehhh
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
		return NULL_AABB;
	}

	private static final AxisAlignedBB MIDDLE_AABB = new AxisAlignedBB(0.5 - (1.0 / 16.0), 0.0, 0.5 - (1.0 / 16.0), 0.5 + (1.0 / 16.0), 0.0 + 1.0, 0.5 + (1.0 / 16.0));
	private static final AxisAlignedBB NS_AABB = new AxisAlignedBB(0.5 - (1.0 / 16.0), 0.0, 0.5 - (5.0 / 16.0), 0.5 + (1.0 / 16.0), 0.0 + 1.0, 0.5 + (5.0 / 16.0));
	private static final AxisAlignedBB WE_AABB = new AxisAlignedBB(0.5 - (5.0 / 16.0), 0.0, 0.5 - (1.0 / 16.0), 0.5 + (5.0 / 16.0), 0.0 + 1.0, 0.5 + (1.0 / 16.0));

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		TileEntityFlag flag = getTileEntity(source, pos, TileEntityFlag.class);
		if (flag != null) {
			EnumFacing onSurface = flag.getOrientation().down();
			switch (onSurface) {
				case EAST:
				case WEST:
					return WE_AABB;
				case NORTH:
				case SOUTH:
					return NS_AABB;
				default:
					return MIDDLE_AABB;
			}
		}

		return MIDDLE_AABB;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		// TODO 1.8.9 verify
		if (side == EnumFacing.UP) return false;
		if (side == EnumFacing.DOWN) {
			final BlockPos blockBelow = pos.down();
			final Block belowBlock = world.getBlockState(blockBelow).getBlock();
			if (belowBlock instanceof BlockFence) return true;
			if (belowBlock == this) {
				TileEntityFlag flag = getTileEntity(world, blockBelow, TileEntityFlag.class);
				if (flag != null && flag.getOrientation().down() == EnumFacing.DOWN) return true;
			}
		}

		return isNeighborBlockSolid(world, pos, side);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		super.neighborChanged(state, world, pos, block);

		final Orientation orientation = getOrientation(state);
		if (!isNeighborBlockSolid(world, pos, orientation.down())) world.destroyBlock(pos, true);
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}
}
