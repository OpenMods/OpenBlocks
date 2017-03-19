package openblocks.common.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
	@SuppressWarnings("deprecation")
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity) {
		if (entity instanceof EntityLivingBase) {
			Orientation orientation = getOrientation(world, pos);
			EnumFacing playerRotation = ((EntityLivingBase)entity).getHorizontalFacing();
			if (orientation.north() == playerRotation) {
				super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity);
			}
		} else {
			super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity);
		}
	}

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0f / 16.0f);

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
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighbour) {
		final EnumFacing dir = getOrientation(state).north();

		if (world.isAirBlock(pos.offset(dir))) {
			// TODO 1.10 verify if it still drops
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
				// TODO 1.10 verify handness
				if (world.isAirBlock(placePos) && manipulator.place(state, orientation.north(), EnumHand.MAIN_HAND)) {
					if (!Config.infiniteLadder) stack.stackSize--;
				} else return;

				placePos = placePos.down();
			}
		}
	}

}
