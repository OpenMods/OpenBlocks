package openblocks.common.tileentity;

import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
		syncMap.addUpdateListener(changes -> {
			if (changes.contains(angle)) {
				setStateAngle(angle.get());
				markBlockForRenderUpdate(getPos());
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
		final Vec3d coneAxis = new Vec3d(basePos.x - blockPos.x, basePos.y - blockPos.y, basePos.z - blockPos.z);

		for (Entity entity : entities) {
			if (entity instanceof PlayerEntity && ((PlayerEntity)entity).capabilities.isCreativeMode) continue;
			Vec3d directionVec = new Vec3d(
					entity.posX - blockPos.x,
					entity.posY - blockPos.y,
					entity.posZ - blockPos.z);

			if (isLyingInSphericalCone(coneAxis, directionVec, CONE_HALF_APERTURE)) {
				final double distToOrigin = directionVec.lengthVector();
				final double force = (1.0 - distToOrigin / Config.fanRange) * maxForce;
				if (force <= 0) continue;
				Vec3d normal = directionVec.normalize();
				entity.motionX += force * normal.x;
				entity.motionZ += force * normal.z;
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
		return boundingBox.grow(Config.fanRange);
	}

	private static boolean isLyingInSphericalCone(Vec3d coneAxis, Vec3d originToTarget, double halfAperture) {
		double angleToAxisCos = originToTarget.dotProduct(coneAxis) / originToTarget.lengthVector() / coneAxis.lengthVector();
		return angleToAxisCos > Math.cos(halfAperture);
	}

	@Override
	public void onBlockPlacedBy(BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
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
	public boolean onBlockActivated(PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote && hand == Hand.MAIN_HAND) {
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
