package openblocks.common;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import openmods.OpenMods;

public class PedometerHandler {

	private static final String PROPERTY_PEDOMETER = "Pedometer";

	public static Vec3 getEntityPosition(Entity entity) {
		return Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ);
	}

	public static Vec3 getEntityVelocity(Entity entity) {
		if (entity.ridingEntity != null) return getEntityVelocity(entity.ridingEntity);
		return Vec3.createVectorHelper(entity.motionX, entity.motionY, entity.motionZ);
	}

	public static Vec3 subtract(Vec3 a, Vec3 b) {
		return Vec3.createVectorHelper(a.xCoord - b.xCoord, a.yCoord - b.yCoord, a.zCoord - b.zCoord);
	}

	public static class PedometerTracker implements IExtendedEntityProperties {
		private double totalDistance;

		private long startTicks;

		private Vec3 startPos;

		private Vec3 prevTickPos;

		private PedometerData lastResult;

		@Override
		public void saveNBTData(NBTTagCompound compound) {}

		@Override
		public void loadNBTData(NBTTagCompound compound) {}

		@Override
		public void init(Entity entity, World world) {
			totalDistance = 0;
			lastResult = null;
			prevTickPos = startPos = getEntityPosition(entity);
			startTicks = OpenMods.proxy.getTicks(world);
		}

		public void update(Entity entity) {
			Vec3 currentPosition = getEntityPosition(entity);
			Vec3 currentVelocity = getEntityVelocity(entity);

			Vec3 dS = subtract(currentPosition, prevTickPos);
			prevTickPos = currentPosition;
			Vec3 totalDelta = subtract(currentPosition, startPos);

			double dist = dS.lengthVector();
			totalDistance += dist;

			long currentTime = OpenMods.proxy.getTicks(entity.worldObj);
			double totalTime = currentTime - startTicks;

			lastResult = new PedometerData(startPos, totalTime, totalDistance, totalDelta.lengthVector(), currentVelocity.lengthVector());
		}
	}

	public static class PedometerData {
		public final Vec3 startingPoint;
		public final double totalTime;
		public final double totalDistance;
		public final double straightLineDistance;

		public final double currentSpeed;

		private PedometerData(Vec3 startingPoint, double totalTime, double totalDistance, double straightLineDistance, double currentSpeed) {
			this.startingPoint = startingPoint;
			this.totalTime = totalTime;
			this.totalDistance = totalDistance;
			this.straightLineDistance = straightLineDistance;
			this.currentSpeed = currentSpeed;
		}

		public double averageSpeed() {
			return totalDistance / totalTime;
		}

		public double straightLineSpeed() {
			return straightLineDistance / totalTime;
		}
	}

	public static void reset(World world, Entity entity) {
		IExtendedEntityProperties property = entity.getExtendedProperties(PROPERTY_PEDOMETER);

		PedometerTracker data;
		if (property instanceof PedometerTracker) {
			data = (PedometerTracker)property;
		} else {
			data = new PedometerTracker();
			entity.registerExtendedProperties(PROPERTY_PEDOMETER, data);
		}

		data.init(entity, world);
	}

	public static void updatePedometerData(Entity entity) {
		IExtendedEntityProperties property = entity.getExtendedProperties(PROPERTY_PEDOMETER);
		if (property instanceof PedometerTracker) {
			PedometerTracker data = (PedometerTracker)property;
			data.update(entity);
		}
	}

	public static PedometerData getPedometerData(Entity entity) {
		IExtendedEntityProperties property = entity.getExtendedProperties(PROPERTY_PEDOMETER);
		if (property instanceof PedometerTracker) {
			PedometerTracker data = (PedometerTracker)property;
			return data.lastResult;
		}

		return null;
	}
}
