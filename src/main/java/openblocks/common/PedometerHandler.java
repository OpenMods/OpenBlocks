package openblocks.common;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import openmods.OpenMods;

public class PedometerHandler {

	private static final String PROPERTY_PEDOMETER = "Pedometer";

	public static class PedometerState implements IExtendedEntityProperties {
		private double totalDistance;

		private long startTicks;

		private Vec3 startPos;

		private Vec3 prevTickPos;

		private long prevTickTime;

		private Vec3 lastCheckPos;

		private long lastCheckTime;

		private PedometerData lastResult;

		private boolean isRunning;

		@Override
		public void saveNBTData(NBTTagCompound compound) {}

		@Override
		public void loadNBTData(NBTTagCompound compound) {}

		public void reset() {
			isRunning = false;
			totalDistance = 0;
			lastResult = null;

			lastCheckPos = null;
			lastCheckTime = 0;

			prevTickPos = null;
			prevTickTime = 0;
		}

		@Override
		public void init(Entity entity, World world) {
			lastCheckPos = prevTickPos = startPos = entity.getPositionVector();
			lastCheckTime = prevTickTime = startTicks = OpenMods.proxy.getTicks(world);
			isRunning = true;
		}

		public void update(Entity entity) {
			Vec3 currentPosition = entity.getPositionVector();
			Vec3 deltaSinceLastUpdate = currentPosition.subtract(prevTickPos);
			prevTickPos = currentPosition;

			long currentTime = OpenMods.proxy.getTicks(entity.worldObj);
			double ticksSinceLastUpdate = currentTime - prevTickTime;
			prevTickTime = currentTime;

			double distanceSinceLastTick = deltaSinceLastUpdate.lengthVector();
			double currentSpeed = ticksSinceLastUpdate != 0? distanceSinceLastTick / ticksSinceLastUpdate : 0;
			totalDistance += distanceSinceLastTick;

			Vec3 deltaFromStart = currentPosition.subtract(startPos);
			long ticksFromStart = currentTime - startTicks;

			double distanceFromStart = deltaFromStart.lengthVector();

			double distanceFromLastCheck = 0;
			if (lastCheckPos != null) distanceFromLastCheck = currentPosition.subtract(lastCheckPos).lengthVector();

			long timeFromLastCheck = currentTime - lastCheckTime;

			lastResult = new PedometerData(startPos, ticksFromStart, totalDistance, distanceFromStart, distanceFromLastCheck, timeFromLastCheck, currentSpeed);
		}

		public boolean isRunning() {
			return isRunning;
		}

		public PedometerData getData() {
			lastCheckPos = prevTickPos;
			lastCheckTime = prevTickTime;
			return lastResult;
		}
	}

	public static class PedometerData {
		public final Vec3 startingPoint;
		public final long totalTime;
		public final double totalDistance;
		public final double straightLineDistance;
		public final double lastCheckDistance;
		public final long lastCheckTime;

		public final double currentSpeed;

		private PedometerData(Vec3 startingPoint,
				long totalTime,
				double totalDistance,
				double straightLineDistance,
				double lastCheckDistance,
				long lastCheckTime,
				double currentSpeed) {
			this.startingPoint = startingPoint;
			this.totalTime = totalTime;
			this.totalDistance = totalDistance;
			this.straightLineDistance = straightLineDistance;
			this.lastCheckDistance = lastCheckDistance;
			this.lastCheckTime = lastCheckTime;
			this.currentSpeed = currentSpeed;
		}

		public double averageSpeed() {
			if (totalTime == 0) return 0;
			return totalDistance / totalTime;
		}

		public double straightLineSpeed() {
			if (totalTime == 0) return 0;
			return straightLineDistance / totalTime;
		}

		public double lastCheckSpeed() {
			if (lastCheckTime == 0) return 0;
			return lastCheckDistance / lastCheckTime;
		}
	}

	public static PedometerState getProperty(Entity entity) {
		IExtendedEntityProperties property = entity.getExtendedProperties(PROPERTY_PEDOMETER);

		PedometerState state;
		if (property instanceof PedometerState) {
			state = (PedometerState)property;
		} else {
			state = new PedometerState();
			entity.registerExtendedProperties(PROPERTY_PEDOMETER, state);
		}
		return state;
	}

	public static void reset(Entity entity) {
		PedometerState state = getProperty(entity);
		state.reset();
	}

	public static void updatePedometerData(Entity entity) {
		IExtendedEntityProperties property = entity.getExtendedProperties(PROPERTY_PEDOMETER);
		if (property instanceof PedometerState) {
			PedometerState state = (PedometerState)property;
			if (state.isRunning) state.update(entity);
		}
	}
}
