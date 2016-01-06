package openblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
		super(Material.circuits);
		setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 1 / 16f);
		setPlacementMode(BlockPlacementMode.SURFACE);
		setInventoryRenderOrientation(Orientation.XN_YN);
	}

	// TODO 1.8.9 Ehhh
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		TileEntityFlag flag = getTileEntity(world, pos, TileEntityFlag.class);
		if (flag != null) {
			EnumFacing onSurface = flag.getOrientation().down();
			if (onSurface == EnumFacing.DOWN) {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 1 / 16f);
			} else if (onSurface == EnumFacing.EAST || onSurface == EnumFacing.WEST) {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 5 / 16f, 1f, 1 / 16f);
			} else {
				setupDimensionsFromCenter(0.5f, 0f, 0.5f, 1 / 16f, 1f, 5 / 16f);
			}
		}
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
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbour) {
		super.onNeighborBlockChange(world, pos, state, neighbour);

		final Orientation orientation = getOrientation(state);
		if (!isNeighborBlockSolid(world, pos, orientation.down())) world.destroyBlock(pos, true);
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}
}
