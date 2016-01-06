package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTarget;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;
t openmods.geometry.Orientation;

public class BlockTarget extends OpenBlock.FourDirections {

	private int lastEntityHit = 0;

	public BlockTarget() {
		super(Material.rock);
		setLightLevel(0.3f);
	}

	// TODO 1.8.9 loooks like it
	@Override
	public int getRenderType() {
		return 2; // TESR only
	}


	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {

		if (!world.isRemote && entity != null && entity instanceof EntityArrow) {
			if (lastEntityHit != entity.getEntityId()) {
				lastEntityHit = entity.getEntityId();
				return;
			}
			lastEntityHit = entity.getEntityId();
			onTargetHit(world, pos, new Vec3(entity.posX, entity.posY, entity.posZ));
		}
	}

	public void onTargetHit(World world, BlockPos pos, Vec3 entityPosition) {

		if (world.isRemote) return;

		final TileEntityTarget target = getTileEntity(world, pos, TileEntityTarget.class);
		if (target == null) return;

		/**
		 * onEntityCollidedWithBlock is called twice when the arrow is hit
		 * The first is from the raytracing, which is predictive and
		 * inaccurate The second is from the bounding box collision. We only
		 * care about the second one
		 */

		if (!target.isEnabled()) return;

		EnumFacing opposite = target.getOrientation().south();

		double centerX = pos.getY() + 0.5 + (opposite.getFrontOffsetX() * 0.5);
		double centerY = pos.getY() + 0.55 + (opposite.getFrontOffsetY() * 0.45);
		double centerZ = pos.getZ() + 0.5 + (opposite.getFrontOffsetZ() * 0.5);

		switch (opposite) {
			case NORTH:
			case SOUTH:
				entityPosition.zCoord = centerZ;
				break;
			case EAST:
			case WEST:
				entityPosition.xCoord = centerX;
				break;
			default:
				break;

		}

		final Vec3 bullseye = new Vec3(centerX, centerY, centerZ);

		double distance = entityPosition.distanceTo(bullseye);

		target.setStrength(15 - Math.min(15, Math.max(0, (int)(distance * 32))));

	}

	@Override
	public int getWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		final TileEntityTarget tile = getTileEntity(world, pos, TileEntityTarget.class);
		return tile != null? tile.getStrength() : 0;
	}

	@Override
	public int getStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		// TODO Side-aware?
		return getWeakPower(world, pos, state, side);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {

		final TileEntityTarget target = getTileEntity(world, pos, TileEntityTarget.class);

		if (!target.isEnabled()) {
			setBlockBounds(0, 0, 0, 1.0f, 0.1f, 1.0f);
			return;
		}

		final Orientation orientation = target.getOrientation();

		final AxisAlignedBB aabb = AxisAlignedBB.fromBounds(0.0, 0.0, 0.9, 1.0, 1.0, 1.0);
		final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
		setBlockBounds(rotatedAabb);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		// TODO 1.8.9 verify
		return isOnTopOfSolidBlock(world, pos, side);
	}
}
