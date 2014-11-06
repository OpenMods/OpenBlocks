package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.Config;
import openmods.api.INeighbourAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.sync.SyncableByte;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;

public class TileEntityFan extends SyncedTileEntity implements IPlaceAwareTile, INeighbourAwareTile {

	private static final double CONE_HALF_APERTURE = 1.2 / 2.0;
	private SyncableFloat angle;
	private SyncableByte power;
	private float bladeRotation;
	private float bladeSpeed;

	public TileEntityFan() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat();
		power = new SyncableByte();
	}

	@Override
	public void updateEntity() {
		float redstonePower = power.get() / 15.0f;

		bladeSpeed = redstonePower;
		bladeRotation += redstonePower;

		final double maxForce = Config.fanForce * redstonePower;
		if (maxForce <= 0) return;
		@SuppressWarnings("unchecked")
		List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, getEntitySearchBoundingBox());
		if (entities.isEmpty()) return;

		double angle = Math.toRadians(getAngle() - 90);
		final Vec3 blockPos = getConeApex(angle);
		final Vec3 basePos = getConeBaseCenter(angle);
		final Vec3 coneAxis = Vec3.createVectorHelper(basePos.xCoord - blockPos.xCoord, basePos.yCoord - blockPos.yCoord, basePos.zCoord - blockPos.zCoord);

		for (Entity entity : entities) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) continue;
			Vec3 directionVec = Vec3.createVectorHelper(
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

	private Vec3 getConeBaseCenter(double angle) {
		return Vec3.createVectorHelper(
				xCoord + (Math.cos(angle) * Config.fanRange),
				yCoord + 0.5,
				zCoord + (Math.sin(angle) * Config.fanRange));
	}

	private Vec3 getConeApex(double angle) {
		return Vec3.createVectorHelper(xCoord + 0.5 - Math.cos(angle) * 1.1, yCoord + 0.5, zCoord + 0.5 - Math.sin(angle) * 1.1);
	}

	private AxisAlignedBB getEntitySearchBoundingBox() {
		AxisAlignedBB boundingBox = AxisAlignedBB.getBoundingBox(xCoord, yCoord - 2, zCoord, xCoord + 1, yCoord + 3, zCoord + 1);
		return boundingBox.expand(Config.fanRange, Config.fanRange, Config.fanRange);
	}

	private static boolean isLyingInSphericalCone(Vec3 coneAxis, Vec3 originToTarget, double halfAperture) {
		double angleToAxisCos = originToTarget.dotProduct(coneAxis) / originToTarget.lengthVector() / coneAxis.lengthVector();
		return angleToAxisCos > Math.cos(halfAperture);
	}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {
		angle.set(player.rotationYawHead);
	}

	public float getAngle() {
		return angle.get();
	}

	public float getBladeRotation(float partialTickTime) {
		return bladeRotation + bladeSpeed * partialTickTime;
	}

	@Override
	public void onNeighbourChanged(Block block) {
		if (!worldObj.isRemote) {
			int power = Config.redstoneActivatedFan? worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) : 15;
			this.power.set((byte)power);
			sync();
		}
	}
}
