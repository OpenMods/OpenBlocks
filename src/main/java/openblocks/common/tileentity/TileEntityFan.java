package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ITickable;
import net.minecraft.util.Vec3;
import openblocks.Config;
import openmods.api.IAddAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.sync.SyncableByte;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityFan extends SyncedTileEntity implements IPlaceAwareTile, INeighbourAwareTile, IAddAwareTile, ITickable {

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
	public void update() {
		float redstonePower = power.get() / 15.0f;

		bladeSpeed = redstonePower;
		bladeRotation += redstonePower;

		final double maxForce = Config.fanForce * redstonePower;
		if (maxForce <= 0) return;

		List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, getEntitySearchBoundingBox());
		if (entities.isEmpty()) return;

		double angle = Math.toRadians(getAngle() - 90);
		final Vec3 blockPos = getConeApex(angle);
		final Vec3 basePos = getConeBaseCenter(angle);
		final Vec3 coneAxis = new Vec3(basePos.xCoord - blockPos.xCoord, basePos.yCoord - blockPos.yCoord, basePos.zCoord - blockPos.zCoord);

		for (Entity entity : entities) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) continue;
			Vec3 directionVec = new Vec3(
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
		// TODO 1.8.9 this may be constant
		return new Vec3(pos)
				.addVector(
						(Math.cos(angle) * Config.fanRange),
						0.5,
						(Math.sin(angle) * Config.fanRange));
	}

	private Vec3 getConeApex(double angle) {
		return new Vec3(pos)
				.addVector(
						0.5 - Math.cos(angle) * 1.1,
						0.5,
						0.5 - Math.sin(angle) * 1.1);
	}

	private AxisAlignedBB getEntitySearchBoundingBox() {
		AxisAlignedBB boundingBox = BlockUtils.aabbOffset(pos, 0, -2, 0, +1, +3, 1);
		return boundingBox.expand(Config.fanRange, Config.fanRange, Config.fanRange);
	}

	private static boolean isLyingInSphericalCone(Vec3 coneAxis, Vec3 originToTarget, double halfAperture) {
		double angleToAxisCos = originToTarget.dotProduct(coneAxis) / originToTarget.lengthVector() / coneAxis.lengthVector();
		return angleToAxisCos > Math.cos(halfAperture);
	}

	@Override
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, ItemStack stack) {
		angle.set(placer.rotationYawHead);
	}

	public float getAngle() {
		return angle.get();
	}

	public float getBladeRotation(float partialTickTime) {
		return bladeRotation + bladeSpeed * partialTickTime;
	}

	@Override
	public void onNeighbourChanged(Block block) {
		updateRedstone();
	}

	@Override
	public void onAdded() {
		updateRedstone();
	}

	private void updateRedstone() {
		if (!worldObj.isRemote) {
			int power = Config.redstoneActivatedFan? worldObj.isBlockIndirectlyGettingPowered(pos) : 15;
			this.power.set((byte)power);
			sync();
		}
	}

}
