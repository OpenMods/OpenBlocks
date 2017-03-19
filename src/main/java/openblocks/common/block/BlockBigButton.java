package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityBigButton;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBigButton extends OpenBlock.SixDirections {

	private static final AxisAlignedBB ACTIVE_AABB = new AxisAlignedBB(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.0625);
	private static final AxisAlignedBB INACTIVE_AABB = new AxisAlignedBB(0.0625, 0.0625, 0, 0.9375, 0.9375, 0.125);

	public BlockBigButton() {
		super(Material.CIRCUITS);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		// TODO 1.8.9 verigy
		return super.canPlaceBlockOnSide(world, pos, side) && isNeighborBlockSolid(world, pos, side);
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		// TODO 1.8.9 Move pressed state to block state
		TileEntityBigButton tile = getTileEntity(source, pos, TileEntityBigButton.class);

		boolean pressed = tile != null && tile.isButtonActive();
		final Orientation orientation = getOrientation(state);

		return BlockSpaceTransform.instance.mapBlockToWorld(orientation, pressed? ACTIVE_AABB : INACTIVE_AABB);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		TileEntityBigButton te = getTileEntity(world, pos, TileEntityBigButton.class);
		return te != null && te.isButtonActive()? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		EnumFacing direction = side.getOpposite();
		TileEntityBigButton button = getTileEntity(world, pos, TileEntityBigButton.class);
		return (button != null && direction == button.getOrientation().north() && button.isButtonActive())? 15 : 0;
	}
}
