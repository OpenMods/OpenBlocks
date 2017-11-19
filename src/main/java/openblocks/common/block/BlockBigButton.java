package openblocks.common.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
		return new BlockStateContainer(this, new IProperty[] { getPropertyOrientation(), POWERED });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta)
				.withProperty(POWERED, (meta & MASK_ACTIVE) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) | (state.getValue(POWERED)? MASK_ACTIVE : 0);
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
		return super.canPlaceBlockOnSide(world, pos, side) && isNeighborBlockSolid(world, pos, side.getOpposite());
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		final boolean pressed = state.getValue(POWERED);
		final Orientation orientation = getOrientation(state);

		return BlockSpaceTransform.instance.mapBlockToWorld(orientation, pressed? ACTIVE_AABB : INACTIVE_AABB);
	}

	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return blockState.getValue(POWERED)? 15 : 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
		final boolean pressed = blockState.getValue(POWERED);
		return (side == getFront(blockState) && pressed)? 15 : 0;
	}

	private void setPoweredState(World world, BlockPos pos, boolean isPowered) {
		final IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.withProperty(BlockBigButton.POWERED, isPowered), BlockNotifyFlags.ALL);

		world.notifyNeighborsOfStateChange(pos, this, false);
		final EnumFacing rot = getBack(state);
		world.notifyNeighborsOfStateChange(pos.offset(rot), this, false);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (hand != EnumHand.MAIN_HAND) return false;

		if (!worldIn.isRemote) {
			if (playerIn.isSneaking()) {
				final TileEntityBigButton te = getTileEntity(worldIn, pos, TileEntityBigButton.class);
				te.openGui(OpenBlocks.instance, playerIn);
			} else if (!state.getValue(POWERED)) {
				worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
				setPoweredState(worldIn, pos, true);
				final TileEntityBigButton te = getTileEntity(worldIn, pos, TileEntityBigButton.class);
				worldIn.scheduleUpdate(pos, this, te.getTickTime());
			}
		}

		return true;

	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			setPoweredState(worldIn, pos, false);
			worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
		}
	}
}
