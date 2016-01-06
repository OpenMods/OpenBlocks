package openblocks.common.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.Config;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
import openmods.infobook.BookDocumentation;
import openmods.utils.BlockManipulator;

@BookDocumentation
public class BlockRopeLadder extends OpenBlock.FourDirections {

	public static final float RENDER_THICKNESS = 1.0f / 64.0f;
	private static final float COLLISION_THICKNESS = 1.0f / 16.0f;

	public BlockRopeLadder() {
		super(Material.circuits);
		setHardness(0.4F);
		setStepSound(soundTypeLadder);
		setPlacementMode(BlockPlacementMode.SURFACE);
	}

	@Override
	public boolean isLadder(IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}

	@Override
	public int quantityDropped(Random random) {
		return Config.infiniteLadder? 0 : 1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> result, Entity entity) {
		if (entity instanceof EntityLivingBase) {
			Orientation orientation = getOrientation(world, pos);
			EnumFacing playerRotation = ((EntityLivingBase)entity).getHorizontalFacing();
			if (orientation.north() == playerRotation) {
				super.addCollisionBoxesToList(world, pos, state, mask, result, entity);
			}
		} else {
			super.addCollisionBoxesToList(world, pos, state, mask, result, entity);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		getBlockBounds(world, pos, COLLISION_THICKNESS);
	}

	private void getBlockBounds(IBlockAccess world, BlockPos pos, float thickness) {
		final Orientation orientation = getOrientation(world, pos);
		final AxisAlignedBB aabb = AxisAlignedBB.fromBounds(0.0, 0.0, 0.0, 1.0, 1.0, thickness);
		final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
		setBlockBounds(rotatedAabb);
	}

	@Override
	public boolean canRotateWithTool() {
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbour) {
		final EnumFacing dir = getOrientation(state).north();

		if (world.isAirBlock(pos.offset(dir))) {
			if (world.getBlockState(pos.up()).getBlock() != this) world.setBlockToAir(pos);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		pos = pos.down();

		if (pos.getY() > 0) {
			final Block bottomBlock = world.getBlockState(pos).getBlock();
			if (bottomBlock == this) {
				// TODO 1.8.9 verify
				world.destroyBlock(pos, true);
			}
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);

		if (placer instanceof EntityPlayer) {
			final EntityPlayer player = (EntityPlayer)placer;
			final Orientation orientation = getOrientation(state);

			BlockPos placePos = pos.down();
			while (placePos.getY() > 0 && (Config.infiniteLadder || stack.stackSize > 1)) {
				final BlockManipulator manipulator = new BlockManipulator(world, player, pos);

				// TODO 1.8.9 verify place direction
				if (world.isAirBlock(placePos) && manipulator.place(state, orientation.north())) {
					if (!Config.infiniteLadder) stack.stackSize--;
				}
				else return;

				placePos = placePos.down();
			}
		}
	}

}
