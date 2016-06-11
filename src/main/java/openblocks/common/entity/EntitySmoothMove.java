package openblocks.common.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
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

		public void setTarget(Vec3 position) {
			setTarget(position.xCoord, position.yCoord, position.zCoord);
		}

		public void setTarget(double targetX, double targetY, double targetZ) {
			this.targetX = targetX;
			this.targetY = targetY;
			this.targetZ = targetZ;
		}

		public void update() {
			double dx = targetX - posX;
			double dy = targetY - posY;
			double dz = targetZ - posZ;

			double lenSq = dx * dx + dy * dy + dz * dz;
			if (lenSq > panicLengthSq || lenSq < minimalLengthSq) {
				setPosition(targetX, targetY, targetZ);
				motionX = motionY = motionZ = 0;
			} else {
				if (lenSq > cutoff * cutoff) {
					double scale = cutoff / Math.sqrt(lenSq);
					dx *= scale;
					dy *= scale;
					dz *= scale;
				}
				moveEntity(motionX + dx * damp, motionY + dy * damp, motionZ + dz * damp);
			}
		}
	}

	protected final MoveSmoother smoother;

	public EntitySmoothMove(World world) {
		super(world);

		smoother = createSmoother(world.isRemote);
	}

	protected MoveSmoother createSmoother(boolean isRemote) {
		return isRemote? new MoveSmoother(0.25, 1.0, 8.0, 0.01) : new MoveSmoother(0.5, 5.0, 128.0, 0.01);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		if (smoother != null) smoother.setTarget(x, y, z);
		super.setPosition(x, y, z);
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		smoother.setTarget(x, y, z);
		super.setPositionAndRotation(x, y, z, yaw, pitch);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int something) {
		smoother.setTarget(x, y, z);
		super.setRotation(yaw, pitch);
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
