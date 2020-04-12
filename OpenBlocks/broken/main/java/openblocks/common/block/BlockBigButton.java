package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityBigButton;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockNotifyFlags;

@BookDocumentation
public class BlockBigButton extends OpenBlock.SixDirections {

	private static final AxisAlignedBB ACTIVE_AABB = new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375);
	private static final AxisAlignedBB INACTIVE_AABB = new AxisAlignedBB(0.0625, 0, 0.0625, 0.9375, 0.125, 0.9375);

	public static final IProperty<Boolean> POWERED = PropertyBool.create("powered");

	private static final int MASK_ACTIVE = 0x8;

	public BlockBigButton() {
		super(Material.CIRCUITS);
		setPlacementMode(BlockPlacementMode.SURFACE);
		setDefaultState(getDefaultState().withProperty(POWERED, Boolean.FALSE));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, getPropertyOrientation(), POWERED);
	}

	@Override
	public BlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta)
				.withProperty(POWERED, (meta & MASK_ACTIVE) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return super.getMetaFromState(state) | (state.getValue(POWERED)? MASK_ACTIVE : 0);
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, Direction side) {
		return super.canPlaceBlockOnSide(world, pos, side) && isNeighborBlockSolid(world, pos, side.getOpposite());
	}

	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		final boolean pressed = state.getValue(POWERED);
		final Orientation orientation = getOrientation(state);

		return BlockSpaceTransform.instance.mapBlockToWorld(orientation, pressed? ACTIVE_AABB : INACTIVE_AABB);
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(BlockState blockState, IBlockAccess world, BlockPos pos, Direction side) {
		return blockState.getValue(POWERED)? 15 : 0;
	}

	@Override
	public int getStrongPower(BlockState blockState, IBlockAccess world, BlockPos pos, Direction side) {
		final boolean pressed = blockState.getValue(POWERED);
		return (side == getFront(blockState) && pressed)? 15 : 0;
	}

	private void setPoweredState(BlockState state, World world, BlockPos pos, boolean isPowered) {
		world.setBlockState(pos, state.withProperty(BlockBigButton.POWERED, isPowered), BlockNotifyFlags.ALL);

		world.notifyNeighborsOfStateChange(pos, this, false);
		final Direction rot = getBack(state);
		world.notifyNeighborsOfStateChange(pos.offset(rot), this, false);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (hand != Hand.MAIN_HAND) return false;

		if (!worldIn.isRemote) {
			if (playerIn.isSneaking()) {
				final TileEntityBigButton te = getTileEntity(worldIn, pos, TileEntityBigButton.class);
				te.openGui(OpenBlocks.instance, playerIn);
			} else if (!state.getValue(POWERED)) {
				push(state, worldIn, pos);
			}
		}

		return true;
	}

	protected void scheduleUpdate(World worldIn, BlockPos pos) {
		final TileEntityBigButton te = getTileEntity(worldIn, pos, TileEntityBigButton.class);
		worldIn.scheduleUpdate(pos, this, te.getTickTime());
	}

	protected void push(BlockState state, World worldIn, BlockPos pos) {
		worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
		setPoweredState(state, worldIn, pos, true);
		scheduleUpdate(worldIn, pos);
	}

	protected void pop(BlockState state, World worldIn, BlockPos pos) {
		worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
		setPoweredState(state, worldIn, pos, false);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, BlockState state, Random rand) {
		if (!worldIn.isRemote && state.getValue(POWERED)) {
			updateAfterTimeout(state, worldIn, pos);
		}
	}

	protected void updateAfterTimeout(BlockState state, World worldIn, BlockPos pos) {
		pop(state, worldIn, pos);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return BlockFaceShape.UNDEFINED;
	}
}
