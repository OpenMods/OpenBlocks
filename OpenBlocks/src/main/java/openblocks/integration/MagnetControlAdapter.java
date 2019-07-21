package openblocks.integration;

import com.google.common.base.Preconditions;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import java.lang.ref.WeakReference;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.entity.EntityMagnet.IEntityBlockFactory;
import openblocks.common.entity.EntityMagnet.IOwner;
import openmods.entity.EntityBlock;
import openmods.fakeplayer.FakePlayerPool;
import openperipheral.api.adapter.IWorldProvider;
import openperipheral.api.adapter.method.Alias;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.IMultiReturn;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;
import openperipheral.api.helpers.MultiReturn;
import openperipheral.api.peripheral.ExposeInterface;
import openperipheral.api.peripheral.PeripheralTypeId;

@PeripheralTypeId("openblocks_magnet")
@ExposeInterface({ ITickingTurtle.class, IAttachable.class })
public class MagnetControlAdapter implements ITickingTurtle, IWorldProvider, IAttachable {

	public class Owner implements IOwner {

		private Vec3d target;

		public Owner() {
			this.target = new Vec3d(0, 0, 0);
		}

		public synchronized void setTargetPosition(double x, double y, double z) {
			target = new Vec3d(x, y, z);
		}

		public synchronized Vec3d getTarget(Vec3d pos, Direction side) {
			double x = pos.x + 0.5;
			double y = pos.y + 0.5;
			double z = pos.z + 0.5;

			y += target.y;
			switch (side) {
				case NORTH:
					x += target.z;
					z -= target.x;
					break;
				case SOUTH:
					x -= target.z;
					z += target.x;
					break;
				case WEST:
					x -= target.x;
					z -= target.z;
					break;
				case EAST:
					x += target.x;
					z += target.z;
					break;
				default:
					break;
			}

			return new Vec3d(x, y, z);
		}

		@Override
		public boolean isValid(EntityMagnet magnet) {
			return turtle != null && turtle.getWorld() != null && isAttached;
		}

		@Override
		public Vec3d getTarget() {
			return getTarget(new Vec3d(turtle.getPosition()), turtle.getDirection());
		}

		@Override
		public EntityBlock createByPlayer(final IEntityBlockFactory factory) {
			World world = turtle.getWorld();

			if (world instanceof ServerWorld) return FakePlayerPool.instance.executeOnPlayer((ServerWorld)world, factory::create);
			return null;
		}
	}

	private final TurtleSide side;

	private final ITurtleAccess turtle;

	private boolean isAttached;

	private WeakReference<EntityMagnet> magnet = new WeakReference<>(null);
	private Owner magnetOwner;

	public MagnetControlAdapter(ITurtleAccess turtle, TurtleSide side) {
		this.turtle = turtle;
		this.side = side;
	}

	private int fuelTick = 0;

	@Override
	public World getWorld() {
		return turtle.getWorld();
	}

	protected boolean consumeFuel(int amount) {
		return turtle.consumeFuel(amount);
	}

	@ScriptCallable(description = "Activate magnet")
	public void activate() {
		EntityMagnet magnet = this.magnet.get();
		Preconditions.checkState(magnet == null || magnet.isDead, "Magnet already active");
		World world = getWorld();
		Preconditions.checkNotNull(world, "Trying to spawn magnet, but turtle is unloaded");
		Preconditions.checkState(canSpawn(world), "Can't deploy magnet");
		Preconditions.checkState(consumeFuel(5), "No fuel");

		magnetOwner = new Owner();
		magnetOwner.setTargetPosition(0, side == TurtleSide.Left? -1 : 1, 0);
		magnet = new EntityMagnet(world, magnetOwner, true);
		world.spawnEntity(magnet);

		magnet.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1, 1);
		this.magnet = new WeakReference<>(magnet);
	}

	@ScriptCallable(description = "Deactive magnet")
	public void deactivate() {
		despawnMagnet(true);
	}

	@ScriptCallable(description = "Set target for magnet")
	public void setTarget(@Arg(name = "x") double x,
			@Arg(name = "y") double y,
			@Arg(name = "z") double z) {
		Preconditions.checkNotNull(magnetOwner, "Magnet not active");
		Preconditions.checkArgument(checkTargetRange(x, y, z), "Target out of range");
		magnetOwner.setTargetPosition(x, y, z);
	}

	@ScriptCallable(returnTypes = { ReturnType.NUMBER, ReturnType.NUMBER, ReturnType.NUMBER },
			description = "Get turtle position")
	public IMultiReturn getPosition() {
		EntityMagnet magnet = getMagnet();
		Vec3d rotated = getRelativeDistance(magnet);
		return MultiReturn.wrap(rotated.x, rotated.y, rotated.z);
	}

	@ScriptCallable(returnTypes = ReturnType.BOOLEAN, description = "Is magnet above grabbable entity")
	public boolean isAboveEntity() {
		return getMagnet().isAboveTarget();
	}

	@Alias("toggle")
	@ScriptCallable(returnTypes = ReturnType.BOOLEAN, description = "Grab or release entity/block under magnet")
	public boolean toggleMagnet() {
		return getMagnet().toggleMagnet();
	}

	@ScriptCallable(returnTypes = ReturnType.BOOLEAN, description = "Is magnet currently grabbing block or entity")
	public boolean isGrabbing() {
		return getMagnet().isLocked();
	}

	@Alias("distance")
	@ScriptCallable(returnTypes = { ReturnType.NUMBER, ReturnType.NUMBER, ReturnType.NUMBER })
	public IMultiReturn getDistanceToTarget() {
		EntityMagnet magnet = getMagnet();
		Vec3d current = getRelativeDistance(magnet);
		Vec3d target = magnetOwner.target;
		return MultiReturn.wrap(current.x - target.x,
				current.y - target.y,
				current.z - target.z);
	}

	@Override
	public void onPeripheralTick() {
		EntityMagnet magnet = this.magnet.get();
		if (magnet != null && !magnet.isDead && isAttached) {
			if (++fuelTick >= 20) {
				fuelTick = 0;
				int fuel = magnet.isLocked()? 2 : 1;
				if (!consumeFuel(fuel)) despawnMagnet(false);

			}
		}
	}

	private static boolean checkTargetRange(double x, double y, double z) {
		return Math.abs(x) <= Config.turtleMagnetRange
				&& Math.abs(y) <= Config.turtleMagnetRange
				&& Math.abs(z) <= Config.turtleMagnetRange;
	}

	private Vec3d getRelativeDistance(EntityMagnet magnet) {
		Vec3d magnetPos = new Vec3d(magnet.posX, magnet.posY, magnet.posZ);
		Vec3d turtlePos = new Vec3d(turtle.getPosition()).addVector(0.5, 0.5, 0.5);

		Vec3d dist = turtlePos.subtract(magnetPos);

		Direction side = turtle.getDirection();

		switch (side) {
			case NORTH:
				return new Vec3d(-dist.z, dist.y, dist.x);
			case SOUTH:
				return new Vec3d(dist.z, dist.y, -dist.x);
			case EAST:
				return new Vec3d(dist.x, dist.y, dist.z);
			case WEST:
				return new Vec3d(-dist.x, dist.y, -dist.z);
			default:
				return dist;
		}
	}

	private boolean canSpawn(World world) {
		Direction facing = turtle.getDirection();

		Direction spawnSide = (side == TurtleSide.Left)? facing.rotateYCCW() : facing.rotateY();
		BlockPos spawnPos = turtle.getPosition().offset(spawnSide);

		return world.isAirBlock(spawnPos);
	}

	private EntityMagnet getMagnet() {
		EntityMagnet magnet = this.magnet.get();
		Preconditions.checkState(magnet != null && !magnet.isDead, "Magnet not active");
		return magnet;
	}

	private void despawnMagnet(boolean checkPosition) {
		EntityMagnet magnet = this.magnet.get();
		Preconditions.checkNotNull(magnet, "Magnet not active");

		Vec3d magnetPos = new Vec3d(magnet.posX, magnet.posY, magnet.posZ);
		Vec3d turtlePos = new Vec3d(turtle.getPosition()).addVector(0.5, 0.5, 0.5);

		Preconditions.checkState(!checkPosition || canOperateOnMagnet(magnetPos, turtlePos), "Magnet too far");

		magnet.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1, 1);
		magnet.setDead();
		this.magnet.clear();
		magnetOwner = null;
	}

	private static boolean canOperateOnMagnet(Vec3d magnetPos, Vec3d turtlePos) {
		return magnetPos.squareDistanceTo(turtlePos) <= Config.turtleMagnetRangeDeactivate * Config.turtleMagnetRangeDeactivate;
	}

	@Override
	public boolean isValid() {
		return turtle != null && turtle.getWorld() != null && isAttached;
	}

	@Override
	public void addComputer(IArchitectureAccess computer) {
		isAttached = true;
	}

	@Override
	public void removeComputer(IArchitectureAccess computer) {
		isAttached = false;
	}
}
