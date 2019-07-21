package openblocks.common;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.OpenBlocks;
import openmods.OpenMods;

public class PedometerHandler {

	public static class PedometerState {
		private double totalDistance;

		private long startTicks;

		private Vec3d startPos;

		private Vec3d prevTickPos;

		private long prevTickTime;

		private Vec3d lastCheckPos;

		private long lastCheckTime;

		private PedometerData lastResult;

		private boolean isRunning;

		public void reset() {
			isRunning = false;
			totalDistance = 0;
			lastResult = null;

			lastCheckPos = null;
			lastCheckTime = 0;

			prevTickPos = null;
			prevTickTime = 0;
		}

		public void init(Entity entity, World world) {
			lastCheckPos = prevTickPos = startPos = entity.getPositionVector();
			lastCheckTime = prevTickTime = startTicks = OpenMods.proxy.getTicks(world);
			isRunning = true;
		}

		public void update(Entity entity) {
			Vec3d currentPosition = entity.getPositionVector();
			Vec3d deltaSinceLastUpdate = currentPosition.subtract(prevTickPos);
			prevTickPos = currentPosition;

			long currentTime = OpenMods.proxy.getTicks(entity.world);
			double ticksSinceLastUpdate = currentTime - prevTickTime;
			prevTickTime = currentTime;

			double distanceSinceLastTick = deltaSinceLastUpdate.lengthVector();
			double currentSpeed = ticksSinceLastUpdate != 0? distanceSinceLastTick / ticksSinceLastUpdate : 0;
			totalDistance += distanceSinceLastTick;

			Vec3d deltaFromStart = currentPosition.subtract(startPos);
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
		public final Vec3d startingPoint;
		public final long totalTime;
		public final double totalDistance;
		public final double straightLineDistance;
		public final double lastCheckDistance;
		public final long lastCheckTime;

		public final double currentSpeed;

		private PedometerData(Vec3d startingPoint,
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

	private static final ResourceLocation CAPABILITY_KEY = OpenBlocks.location("pedometer_state");

	@CapabilityInject(PedometerState.class)
	private static Capability<PedometerState> PEDOMETER_CAPABILITY;

	private static class CapabilityInjector {

		@SubscribeEvent
		public void attachCapability(AttachCapabilitiesEvent<Entity> evt) {
			if (!(evt.getObject() instanceof PlayerEntity)) return;
			evt.addCapability(CAPABILITY_KEY, new ICapabilityProvider() {

				private PedometerState state;

				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
					return capability == PEDOMETER_CAPABILITY;
				}

				@Override
				@SuppressWarnings("unchecked")
				public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
					if (capability == PEDOMETER_CAPABILITY) {
						if (state == null)
							state = new PedometerState();

						return (T)state;
					}

					return null;
				}
			});
		}
	}

	public static void registerCapability() {
		CapabilityManager.INSTANCE.register(PedometerState.class, new Capability.IStorage<PedometerState>() {
			@Override
			public NBTBase writeNBT(Capability<PedometerState> capability, PedometerState instance, Direction side) {
				return null;
			}

			@Override
			public void readNBT(Capability<PedometerState> capability, PedometerState instance, Direction side, NBTBase nbt) {}

		}, PedometerState::new);

		MinecraftForge.EVENT_BUS.register(new CapabilityInjector());
	}

	public static PedometerState getProperty(Entity entity) {
		// sanity check: only call comes from ItemPedometer and capability is only registered when that item exists
		Preconditions.checkState(PEDOMETER_CAPABILITY != null);
		return entity.getCapability(PEDOMETER_CAPABILITY, Direction.UP);
	}
}
