package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openmods.api.IPlaceAwareTile;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityFan extends SyncedTileEntity implements IPlaceAwareTile {

	private static final double CONE_HALF_APERTURE = 1.2 / 2.0;
	private SyncableFloat angle;

	public TileEntityFan() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat(0.0f);
	}

	@Override
	public void updateEntity() {
		final double maxForce = Config.fanForce * (Config.redstoneActivatedFan? worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) / 15.0 : 1);
		if (maxForce <= 0) return;
		@SuppressWarnings("unchecked")
		List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, getEntitySearchBoundingBox());
		if (entities.isEmpty()) return;

		final Vec3 blockPos = getConeApex();
		final Vec3 basePos = getConeBaseCenter();
		final Vec3 coneAxis = worldObj.getWorldVec3Pool().getVecFromPool(basePos.xCoord - blockPos.xCoord, basePos.yCoord - blockPos.yCoord, basePos.zCoord - blockPos.zCoord);

		for (Entity entity : entities) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) continue;
			Vec3 directionVec = worldObj.getWorldVec3Pool().getVecFromPool(
					entity.posX - blockPos.xCoord,
					entity.posY - blockPos.yCoord,
					entity.posZ - blockPos.zCoord);

			if (isLyingInSphericalCone(coneAxis, directionVec, CONE_HALF_APERTURE)) {
				final double distToOrigin = directionVec.lengthVector();
				final double force = (1.0 - distToOrigin / Config.fanRange) * maxForce;
				if (force <= 0) continue;
				Vec3 normal = directionVec.normalize();
				entity.motionX += force * normal.xCoord;
				entity.motionZ += force * normal.zCoord;
			}
		}
	}

	private Vec3 getConeBaseCenter() {
		double angle = Math.toRadians(getAngle() - 90);
		return worldObj.getWorldVec3Pool().getVecFromPool(
				xCoord + (Math.cos(angle) * Config.fanRange),
				yCoord + 0.5,
				zCoord + (Math.sin(angle) * Config.fanRange));
	}

	private Vec3 getConeApex() {
		double angle = Math.toRadians(getAngle() - 90);
		return worldObj.getWorldVec3Pool().getVecFromPool(xCoord + 0.5 - Math.cos(angle) * 1.1, yCoord + 0.5, zCoord + 0.5 - Math.sin(angle) * 1.1);
	}

	private AxisAlignedBB getEntitySearchBoundingBox() {
		AxisAlignedBB boundingBox = AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord - 2, zCoord, xCoord + 1, yCoord + 3, zCoord + 1);
		return boundingBox.expand(Config.fanRange, Config.fanRange, Config.fanRange);
	}

	private static boolean isLyingInSphericalCone(Vec3 coneAxis, Vec3 originToTarget, double halfAperture) {
		double angleToAxisCos = originToTarget.dotProduct(coneAxis) / originToTarget.lengthVector() / coneAxis.lengthVector();
		return angleToAxisCos > Math.cos(halfAperture);
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		angle.setValue(player.rotationYawHead);
		sync();
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {}

	public float getAngle() {
		return angle.getValue();
	}
}
