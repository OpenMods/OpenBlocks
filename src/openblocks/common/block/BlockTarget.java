package openblocks.common.block;

import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityLightbox;
import openblocks.common.tileentity.TileEntityTarget;
import openblocks.utils.BlockUtils;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockTarget extends OpenBlock {

	private int lastEntityHit = 0;

	public BlockTarget() {
		super(OpenBlocks.Config.blockTargetId, Material.ground);
		setupBlock(this, "target", "Target", TileEntityTarget.class);
		setLightValue(0.3f);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entity, ItemStack itemstack) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if (tile != null && tile instanceof TileEntityTarget) {
			TileEntityTarget target = (TileEntityTarget) tile;
			target.setRotation(BlockUtils.get2dOrientation(entity));
		}
	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int par5) {
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if (te != null && te instanceof TileEntityTarget) {
			((TileEntityTarget) te).neighbourBlockChanged();
		}
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenBlocks.renderId;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z,
			Entity entity) {

		if (!world.isRemote && entity != null && entity instanceof EntityArrow) {

			TileEntity tile = world.getBlockTileEntity(x, y, z);

			if (tile == null || !(tile instanceof TileEntityTarget)) {
				return;
			}

			/**
			 * onEntityCollidedWithBlock is called twice when the arrow is hit
			 * The first is from the raytracing, which is predictive and
			 * inaccurate The second is from the bounding box collision. We only
			 * care about the second one
			 */
			if (lastEntityHit != entity.entityId) {
				lastEntityHit = entity.entityId;
				return;
			}
			lastEntityHit = entity.entityId;

			TileEntityTarget target = (TileEntityTarget) tile;

			if (!target.isPowered()) {
				return;
			}

			ForgeDirection rotation = target.getRotation();
			ForgeDirection opposite = rotation.getOpposite();
			ForgeDirection parallel = opposite.getRotation(ForgeDirection.UP);

			double centerX = (double) x + 0.5 + (opposite.offsetX * 0.5);
			double centerY = (double) y + 0.55 + (opposite.offsetY * 0.45);
			double centerZ = (double) z + 0.5 + (opposite.offsetZ * 0.5);

			double entityX = entity.posX;
			double entityY = entity.posY;
			double entityZ = entity.posZ;

			if (opposite == ForgeDirection.NORTH
					|| opposite == ForgeDirection.SOUTH) {
				entityZ = centerZ;
			} else if (opposite == ForgeDirection.EAST
					|| opposite == ForgeDirection.WEST) {
				entityX = centerX;
			}

			Vec3Pool pool = world.getWorldVec3Pool();

			Vec3 bullseye = pool.getVecFromPool(centerX, centerY, centerZ);
			Vec3 arrow = pool.getVecFromPool(entityX, entityY, entityZ);

			double distance = arrow.distanceTo(bullseye);

			target.setStrength(15 - ((int) Math.min(15,
					Math.max(0, Math.round(distance * 32)))));

		}
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z,
			int m) {
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if ((tile != null) && ((tile instanceof TileEntityTarget))) {
			return ((TileEntityTarget) tile).getStrength();
		}
		return 0;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z,
			int m) {
		return isProvidingWeakPower(world, x, y, z, m);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x,
			int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x,
			int y, int z) {
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y,
			int z) {

		TileEntity tile = world.getBlockTileEntity(x, y, z);

		if (tile == null || !(tile instanceof TileEntityTarget)) {
			return;
		}

		TileEntityTarget target = (TileEntityTarget) tile;

		if (!target.isPowered()) {
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
}
