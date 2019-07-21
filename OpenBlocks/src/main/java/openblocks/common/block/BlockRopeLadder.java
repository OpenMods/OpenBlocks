package openblocks.common.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
	public boolean isLadder(BlockState state, IBlockAccess world, BlockPos pos, LivingEntity entity) {
		return true;
	}

	@Override
	public int quantityDropped(Random random) {
		return Config.infiniteLadder? 0 : 1;
	}

	@Override
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void addCollisionBoxToList(BlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean something) {
		if (entity instanceof LivingEntity) {
			Direction playerRotation = entity.getHorizontalFacing();
			if (getFront(state) == playerRotation.getOpposite()) {
				super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, something);
			}
		} else {
			super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity, something);
		}
	}

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0, 0.0, 15.0f / 16.0f, 1.0, 1.0, 1.0);

	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos) {
		final Orientation orientation = getOrientation(source, pos);
		return BlockSpaceTransform.instance.mapBlockToWorld(orientation, AABB);
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbour, BlockPos neigbourPos) {
		final Direction dir = getBack(state);

		if (world.isAirBlock(pos.offset(dir))) {
			if (world.getBlockState(pos.up()).getBlock() != this) world.destroyBlock(pos, true);
		}
	}

	@Override
	public boolean canBlockBePlaced(World world, BlockPos pos, Hand hand, Direction side, float hitX, float hitY, float hitZ, int itemMetadata, PlayerEntity player) {
		if (side == Direction.DOWN) {
			final BlockState maybeLadder = world.getBlockState(pos.up());
			return maybeLadder.getBlock() == this;
		}

		for (Orientation o : getRotationMode().getValidDirections()) {
			final Direction placeDir = getRotationMode().getFront(o).getOpposite();
			if (!world.isAirBlock(pos.offset(placeDir))) return true;
		}

		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		pos = pos.down();

		if (pos.getY() > 0) {
			final Block bottomBlock = world.getBlockState(pos).getBlock();
			if (bottomBlock == this) {
				world.destroyBlock(pos, true);
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
		Orientation orientation = calculateOrientationAfterPlace(pos, facing, placer);
		if (orientation == null) orientation = findValidBlock(world, pos);
		if (orientation == null) orientation = tryCloneState(world, pos, facing);
		if (orientation == null) return getDefaultState();

		return getStateFromMeta(meta).withProperty(propertyOrientation, orientation);
	}

	private Orientation tryCloneState(World world, BlockPos pos, Direction facing) {
		if (facing == Direction.DOWN) {
			final BlockState maybeLadder = world.getBlockState(pos.up());
			if (maybeLadder.getBlock() == this)
				return maybeLadder.getValue(getPropertyOrientation());
		}

		return null;
	}

	private Orientation findValidBlock(World world, BlockPos pos) {
		for (Orientation o : getRotationMode().getValidDirections()) {
			final Direction placeDir = getRotationMode().getFront(o).getOpposite();
			if (!world.isAirBlock(pos.offset(placeDir))) return o;
		}

		return null;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if (placer instanceof PlayerEntity) {
			final PlayerEntity player = (PlayerEntity)placer;
			final Orientation orientation = getOrientation(state);

			BlockPos placePos = pos.down();
			while (placePos.getY() > 0 && (Config.infiniteLadder || stack.getCount() > 1)) {
				final BlockManipulator manipulator = new BlockManipulator(world, player, placePos);

				if (world.isAirBlock(placePos) && manipulator.place(state, orientation.north(), Hand.MAIN_HAND)) {
					if (!Config.infiniteLadder) stack.shrink(1);
				} else return;

				placePos = placePos.down();
			}
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face) {
		return face == getOrientation(state).south()? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}

}
