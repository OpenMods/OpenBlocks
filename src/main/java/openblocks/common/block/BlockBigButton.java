package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityBigButton;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class BlockBigButton extends OpenBlock.FourDirections {

	public BlockBigButton() {
		super(Material.circuits);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isFullBlock() {
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
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		// TODO 1.8.9 Move pressed state to block state
		TileEntityBigButton tile = getTileEntity(world, pos, TileEntityBigButton.class);

		if (tile == null) { return; }

		boolean pressed = tile.isButtonActive();
		final Orientation orientation = tile.getOrientation();

		final AxisAlignedBB aabb = new AxisAlignedBB(0.0625, 0.0625, 0, 0.9375, 0.9375, pressed? 0.0625 : 0.125);
		final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
		setBlockBounds(rotatedAabb);
	}

	@Override
	public void setBlockBoundsForItemRender() {
		setBlockBounds(0.0625f, 0.0625f, 0.4f, 0.9375f, 0.9375f, 0.525f);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		TileEntityBigButton te = getTileEntity(world, pos, TileEntityBigButton.class);
		return te != null && te.isButtonActive()? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		EnumFacing direction = side.getOpposite();
		TileEntityBigButton button = getTileEntity(world, pos, TileEntityBigButton.class);
		return (button != null && direction == button.getOrientation().north() && button.isButtonActive())? 15 : 0;
	}
}
