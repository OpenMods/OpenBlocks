package openblocks.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.common.tileentity.TileEntityTarget;
import openmods.block.BlockRotationMode;
import openmods.geometry.BlockSpaceTransform;
import openmods.geometry.Orientation;

public class BlockTarget extends OpenBlock {

	private int lastEntityHit = 0;

	public BlockTarget() {
		super(Material.rock);
		setLightLevel(0.3f);
		setRotationMode(BlockRotationMode.FOUR_DIRECTIONS);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean isOpaqueCube() {
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
			onTargetHit(world, x, y, z, Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ));
		}
	}

	public void onTargetHit(World world, int x, int y, int z, Vec3 entityPosition) {

		if (world.isRemote) return;

		final TileEntityTarget target = getTileEntity(world, x, y, z, TileEntityTarget.class);
		if (target == null) return;

		/**
		 * onEntityCollidedWithBlock is called twice when the arrow is hit
		 * The first is from the raytracing, which is predictive and
		 * inaccurate The second is from the bounding box collision. We only
		 * care about the second one
		 */

		if (!target.isEnabled()) return;

		ForgeDirection opposite = target.getOrientation().south();

		double centerX = x + 0.5 + (opposite.offsetX * 0.5);
		double centerY = y + 0.55 + (opposite.offsetY * 0.45);
		double centerZ = z + 0.5 + (opposite.offsetZ * 0.5);

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

		Vec3 bullseye = Vec3.createVectorHelper(centerX, centerY, centerZ);

		double distance = entityPosition.distanceTo(bullseye);

		target.setStrength(15 - Math.min(15, Math.max(0, (int)(distance * 32))));

	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int m) {
		final TileEntityTarget tile = getTileEntity(world, x, y, z, TileEntityTarget.class);
		return tile != null? tile.getStrength() : 0;
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

		final Orientation orientation = target.getOrientation();

		final AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.9, 1.0, 1.0, 1.0);
		final AxisAlignedBB rotatedAabb = BlockSpaceTransform.instance.mapBlockToWorld(orientation, aabb);
		setBlockBounds(rotatedAabb);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, ForgeDirection side) {
		return isOnTopOfSolidBlock(world, x, y, z, side);
	}
}
