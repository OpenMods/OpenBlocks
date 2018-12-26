package openblocks.common.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockManipulator;

@BookDocumentation
public class BlockRopeLadder extends OpenBlock.FourDirections {

	public BlockRopeLadder() {
		super(Material.CIRCUITS);
		setHardness(0.4F);
		setSoundType(SoundType.LADDER);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}

	@Override
	public int quantityDropped(Random random) {
		return Config.infiniteLadder? 0 : 1;
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean something) {
		if (entity instanceof EntityLivingBase) {
			EnumFacing playerRotation = ((EntityLivingBase)entity).getHorizontalFacing();
			if (getFront(state) == playerRotation.getOpposite()) {
				super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, something);
			}
		} else {
			super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, something);
		}
	}

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0, 0.0, 15.0f / 16.0f, 1.0, 1.0, 1.0);

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		final Orientation orientation = getOrientation(source, pos);
		return BlockSpaceTransform.instance.mapBlockToWorld(orientation, AABB);
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbour, BlockPos neigbourPos) {
		final EnumFacing dir = getBack(state);

		if (world.isAirBlock(pos.offset(dir))) {
			if (world.getBlockState(pos.up()).getBlock() != this) world.destroyBlock(pos, true);
		}
	}

	@Override
	public boolean canBlockBePlaced(World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ, int itemMetadata, EntityPlayer player) {
		if (side == EnumFacing.DOWN) {
			final IBlockState maybeLadder = world.getBlockState(pos.up());
			return maybeLadder.getBlock() == this;
		}

		for (Orientation o : getRotationMode().getValidDirections()) {
			final EnumFacing placeDir = getRotationMode().getFront(o).getOpposite();
			if (!world.isAirBlock(pos.offset(placeDir))) return true;
		}

		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		pos = pos.down();

		if (pos.getY() > 0) {
			final Block bottomBlock = world.getBlockState(pos).getBlock();
			if (bottomBlock == this) {
				world.destroyBlock(pos, true);
			}
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		Orientation orientation = calculateOrientationAfterPlace(pos, facing, placer);
		if (orientation == null) orientation = findValidBlock(world, pos);
		if (orientation == null) orientation = tryCloneState(world, pos, facing);
		if (orientation == null) return getDefaultState();

		return getStateFromMeta(meta).withProperty(propertyOrientation, orientation);
	}

	private Orientation tryCloneState(World world, BlockPos pos, EnumFacing facing) {
		if (facing == EnumFacing.DOWN) {
			final IBlockState maybeLadder = world.getBlockState(pos.up());
			if (maybeLadder.getBlock() == this)
				return maybeLadder.getValue(getPropertyOrientation());
		}

		return null;
	}

	private Orientation findValidBlock(World world, BlockPos pos) {
		for (Orientation o : getRotationMode().getValidDirections()) {
			final EnumFacing placeDir = getRotationMode().getFront(o).getOpposite();
			if (!world.isAirBlock(pos.offset(placeDir))) return o;
		}

		return null;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if (placer instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer)placer;
			final Orientation orientation = getOrientation(state);

			BlockPos placePos = pos.down();
			while (placePos.getY() > 0 && (Config.infiniteLadder || stack.getCount() > 1)) {
				final BlockManipulator manipulator = new BlockManipulator(world, player, placePos);

				if (world.isAirBlock(placePos) && manipulator.place(state, orientation.north(), EnumHand.MAIN_HAND)) {
					if (!Config.infiniteLadder) stack.shrink(1);
				} else return;

				placePos = placePos.down();
			}
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return face == getOrientation(state).south()? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

}
