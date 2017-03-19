package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityTarget;
import openmods.block.OpenBlock;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;

public class BlockTarget extends OpenBlock.FourDirections {

	private static final AxisAlignedBB FOLDED_AABB = new AxisAlignedBB(0, 0, 0, 1.0f, 0.1f, 1.0f);
	private static final AxisAlignedBB DEPLOYED_AABB = new AxisAlignedBB(0.0, 0.0, 0.9, 1.0, 1.0, 1.0);
	private int lastEntityHit = 0;

	public BlockTarget() {
		super(Material.ROCK);
		setLightLevel(0.3f);
	}

	// TODO 1.8.9 loooks like it
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (!world.isRemote && entity != null && entity instanceof EntityArrow) {
			if (lastEntityHit != entity.getEntityId()) {
				lastEntityHit = entity.getEntityId();
				return;
			}
			lastEntityHit = entity.getEntityId();
			onTargetHit(world, pos, new Vec3d(entity.posX, entity.posY, entity.posZ));
		}
	}

	public void onTargetHit(World world, BlockPos pos, Vec3d entityPosition) {
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

		final Vec3d bullseye = new Vec3d(centerX, centerY, centerZ);

		double distance = entityPosition.distanceTo(bullseye);

		target.setStrength(15 - Math.min(15, Math.max(0, (int)(distance * 32))));

	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		final TileEntityTarget tile = getTileEntity(blockAccess, pos, TileEntityTarget.class);
		return tile != null? tile.getStrength() : 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		// TODO Side-aware?
		return getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		final TileEntityTarget target = getTileEntity(source, pos, TileEntityTarget.class);

		if (target.isEnabled()) {
			final Orientation orientation = target.getOrientation();
			return BlockSpaceTransform.instance.mapBlockToWorld(orientation, DEPLOYED_AABB);
		} else {
			return FOLDED_AABB;
		}

	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		// TODO 1.8.9 verify
		return isOnTopOfSolidBlock(world, pos, side);
	}
}
