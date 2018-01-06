package openblocks.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntitySmoothMove extends Entity {

	public class MoveSmoother {
		private final double damp;
		private final double cutoff;
		private final double panicLengthSq;
		private final double minimalLengthSq;

		private double targetX;
		private double targetY;
		private double targetZ;

		public MoveSmoother(double damp, double cutoff, double panicLength, double minimalLength) {
			this.damp = damp;
			this.cutoff = cutoff;
			this.panicLengthSq = panicLength * panicLength;
			this.minimalLengthSq = minimalLength * minimalLength;
		}

		protected boolean shouldJump(double x, double y, double z) {
			double dx = x - posX;
			double dy = y - posY;
			double dz = z - posZ;

			double lenSq = dx * dx + dy * dy + dz * dz;
			return shouldJump(lenSq);
		}

		private boolean shouldJump(double lenSq) {
			return (lenSq > panicLengthSq || lenSq < minimalLengthSq);
		}

		public void setTarget(Vec3d position) {
			setTarget(position.x, position.y, position.z);
		}

		public void setTarget(double targetX, double targetY, double targetZ) {
			if (!isPositionValid || shouldJump(targetX, targetY, targetZ)) {
				setPositionRaw(targetX, targetY, targetZ);
				motionX = motionY = motionZ = 0;
			}

			this.targetX = targetX;
			this.targetY = targetY;
			this.targetZ = targetZ;
		}

		public void update() {
			double dx = targetX - posX;
			double dy = targetY - posY;
			double dz = targetZ - posZ;

			final double lenSq = dx * dx + dy * dy + dz * dz;
			if (shouldJump(lenSq)) {
				setPositionRaw(targetX, targetY, targetZ);
				motionX = motionY = motionZ = 0;
			} else {
				if (lenSq > cutoff * cutoff) {
					double scale = cutoff / Math.sqrt(lenSq);
					dx *= scale;
					dy *= scale;
					dz *= scale;
				}

				move(MoverType.SELF, motionX + dx * damp, motionY + dy * damp, motionZ + dz * damp);
			}
		}
	}

	protected final MoveSmoother smoother;

	private boolean isPositionValid;

	public EntitySmoothMove(World world) {
		super(world);
		smoother = createSmoother(world.isRemote);
	}

	protected MoveSmoother createSmoother(boolean isRemote) {
		return isRemote? new MoveSmoother(0.25, 1.0, 8.0, 0.01) : new MoveSmoother(0.5, 5.0, 128.0, 0.01);
	}

	private boolean isFullyInitialized() {
		// may be null for calls made in superclass
		return smoother != null;
	}

	private void setPositionRaw(double x, double y, double z) {
		// ignore dummy position set in super constructor
		isPositionValid = isFullyInitialized();
		super.setPosition(x, y, z);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		if (isFullyInitialized()) smoother.setTarget(x, y, z);
		else setPositionRaw(x, y, z);
	}

	protected void updatePrevPosition() {
		prevDistanceWalkedModified = distanceWalkedModified;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevRotationPitch = this.rotationPitch;
		prevRotationYaw = this.rotationYaw;
	}
}
