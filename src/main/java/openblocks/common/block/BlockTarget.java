package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityTarget;

public class BlockTarget extends OpenBlock {

	private int lastEntityHit = 0;

	public BlockTarget() {
		super(Material.rock);
		setLightLevel(0.3f);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		if (!world.isRemote && entity != null && entity instanceof EntityArrow) {
			if (lastEntityHit != entity.getEntityId()) {
				lastEntityHit = entity.getEntityId();
				return;
			}
			lastEntityHit = entity.getEntityId();
			onTargetHit(world, x, y, z, world.getWorldVec3Pool().getVecFromPool(entity.posX, entity.posY, entity.posZ));
		}
	}

	public void onTargetHit(World world, int x, int y, int z, Vec3 entityPosition) {

		if (world.isRemote) { return; }

		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityTarget)) { return; }

		/**
		 * onEntityCollidedWithBlock is called twice when the arrow is hit
		 * The first is from the raytracing, which is predictive and
		 * inaccurate The second is from the bounding box collision. We only
		 * care about the second one
		 */

		TileEntityTarget target = (TileEntityTarget)tile;

		if (!target.isEnabled()) { return; }

		ForgeDirection rotation = target.getRotation();
		ForgeDirection opposite = rotation.getOpposite();

		double centerX = x + 0.5 + (opposite.offsetX * 0.5);
		double centerY = y + 0.55 + (opposite.offsetY * 0.45);
		double centerZ = z + 0.5 + (opposite.offsetZ * 0.5);

		if (opposite == ForgeDirection.NORTH
				|| opposite == ForgeDirection.SOUTH) {
			entityPosition.zCoord = centerZ;
		} else if (opposite == ForgeDirection.EAST
				|| opposite == ForgeDirection.WEST) {
			entityPosition.xCoord = centerX;
		}

		Vec3Pool pool = world.getWorldVec3Pool();

		Vec3 bullseye = pool.getVecFromPool(centerX, centerY, centerZ);

		double distance = entityPosition.distanceTo(bullseye);

		target.setStrength(15 - ((int)Math.min(15, Math.max(0, Math.round(distance * 32)))));

	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int m) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if ((tile != null) && ((tile instanceof TileEntityTarget))) { return ((TileEntityTarget)tile).getStrength(); }
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int m) {
		return isProvidingWeakPower(world, x, y, z, m);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {

		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityTarget)) { return; }

		TileEntityTarget target = (TileEntityTarget)tile;

		if (!target.isEnabled()) {
			setBlockBounds(0, 0, 0, 1.0f, 0.1f, 1.0f);
			return;
		}

		ForgeDirection direction = target.getRotation();

		switch (direction) {
			case EAST:
				setBlockBounds(0, 0, 0, 0.1f, 1f, 1f);
				break;
			case WEST:
				setBlockBounds(0.9f, 0, 0, 1f, 1f, 1f);
				break;
			case NORTH:
				setBlockBounds(0, 0, 0.9f, 1f, 1f, 1f);
				break;
			case SOUTH:
				setBlockBounds(0, 0, 0f, 1f, 1f, 0.1f);
				break;
			default:
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isOnTopOfSolidBlock(world, x, y, z, side);
	}
}
