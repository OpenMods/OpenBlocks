package openblocks.common.tileentity;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import openblocks.Config;
import openmods.api.IActivateAwareTile;
import openmods.api.IAddAwareTile;
import openmods.api.INeighbourAwareTile;
import openmods.api.IPlaceAwareTile;
import openmods.model.eval.EvalModelState;
import openmods.sync.ISyncListener;
import openmods.sync.ISyncableObject;
import openmods.sync.SyncMap;
import openmods.sync.SyncableByte;
import openmods.sync.SyncableFloat;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.BlockUtils;

public class TileEntityFan extends SyncedTileEntity implements IPlaceAwareTile, INeighbourAwareTile, IAddAwareTile, ITickable, IActivateAwareTile {

	private static final int ANGLE_SPEED_PER_REDSTONE_POWER = 45;
	private static final double CONE_HALF_APERTURE = 1.2 / 2.0;

	private SyncableFloat angle;
	private SyncableByte power;
	private float bladeRotation;
	private float bladeSpeed;

	private EvalModelState baseClipState = EvalModelState.EMPTY;

	public TileEntityFan() {}

	@Override
	protected void createSyncedFields() {
		angle = new SyncableFloat();
		power = new SyncableByte();
	}

	@Override
	protected void onSyncMapCreate(SyncMap syncMap) {
		syncMap.addUpdateListener(new ISyncListener() {
			@Override
			public void onSync(Set<ISyncableObject> changes) {
				if (changes.contains(angle)) {
					setStateAngle(angle.get());
					markBlockForRenderUpdate(getPos());
				}
			}
		});
	}

	@Override
	public void update() {
		float redstonePower = power.get() / 15.0f;

		bladeSpeed = ANGLE_SPEED_PER_REDSTONE_POWER * redstonePower;
		bladeRotation += bladeSpeed;

		final double maxForce = Config.fanForce * redstonePower;
		if (maxForce <= 0) return;

		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, getEntitySearchBoundingBox());
		if (entities.isEmpty()) return;

		double angle = Math.toRadians(getAngle() - 90);
		final Vec3d blockPos = getConeApex(angle);
		final Vec3d basePos = getConeBaseCenter(angle);
		final Vec3d coneAxis = new Vec3d(basePos.xCoord - blockPos.xCoord, basePos.yCoord - blockPos.yCoord, basePos.zCoord - blockPos.zCoord);

		for (Entity entity : entities) {
			if (entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) continue;
			Vec3d directionVec = new Vec3d(
					entity.posX - blockPos.xCoord,
					entity.posY - blockPos.yCoord,
					entity.posZ - blockPos.zCoord);

			if (isLyingInSphericalCone(coneAxis, directionVec, CONE_HALF_APERTURE)) {
				final double distToOrigin = directionVec.lengthVector();
				final double force = (1.0 - distToOrigin / Config.fanRange) * maxForce;
				if (force <= 0) continue;
				Vec3d normal = directionVec.normalize();
				entity.motionX += force * normal.xCoord;
				entity.motionZ += force * normal.zCoord;
			}
		}
	}

	private Vec3d getConeBaseCenter(double angle) {
		// TODO this may be semi-constant
		return new Vec3d(pos)
				.addVector(
						(Math.cos(angle) * Config.fanRange),
						0.5,
						(Math.sin(angle) * Config.fanRange));
	}

	private Vec3d getConeApex(double angle) {
		return new Vec3d(pos)
				.addVector(
						0.5 - Math.cos(angle) * 1.1,
						0.5,
						0.5 - Math.sin(angle) * 1.1);
	}

	private AxisAlignedBB getEntitySearchBoundingBox() {
		AxisAlignedBB boundingBox = BlockUtils.aabbOffset(pos, 0, -2, 0, +1, +3, 1);
		return boundingBox.expand(Config.fanRange, Config.fanRange, Config.fanRange);
	}

	private static boolean isLyingInSphericalCone(Vec3d coneAxis, Vec3d originToTarget, double halfAperture) {
		double angleToAxisCos = originToTarget.dotProduct(coneAxis) / originToTarget.lengthVector() / coneAxis.lengthVector();
		return angleToAxisCos > Math.cos(halfAperture);
	}

	@Override
	public void onBlockPlacedBy(IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
		final float placeAngle = placer.rotationYawHead;
		angle.set(placeAngle);
		setStateAngle(placeAngle);
	}

	public float getAngle() {
		return angle.get();
	}

	public float getBladeRotation(float partialTickTime) {
		return (bladeRotation + bladeSpeed * partialTickTime) % 360;
	}

	@Override
	public void onNeighbourChanged(BlockPos neighbourPos, Block neighbourBlock) {
		updateRedstone();
	}

	@Override
	public void onAdded() {
		updateRedstone();
	}

	private void updateRedstone() {
		if (!world.isRemote) {
			int power = Config.redstoneActivatedFan? world.isBlockIndirectlyGettingPowered(pos) : 15;
			this.power.set((byte)power);
			sync();
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
			angle.set(angle.get() + (player.isSneaking()? -10f : +10f));
			sync();
			return true;
		}

		return false;
	}

	@Override
	public boolean hasFastRenderer() {
		return true;
	}

	private void setStateAngle(float angle) {
		baseClipState = baseClipState.withArg("base_rotation", angle);
	}

	public EvalModelState getStaticRenderState() {
		return baseClipState;
	}

	public EvalModelState getTesrRenderState(float partialTickTime) {
		return baseClipState.withArg("blade_rotation", getBladeRotation(partialTickTime), true);
	}
}
