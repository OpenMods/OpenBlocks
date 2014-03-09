package openblocks.integration;

import java.lang.ref.WeakReference;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.common.entity.EntityMagnet;
import openperipheral.api.*;

import com.google.common.base.Preconditions;

import dan200.turtle.api.ITurtleAccess;
import dan200.turtle.api.TurtleSide;

@OnTick
@Freeform
public class MagnetControlAdapter implements IUpdateHandler, IWorldProvider {

	public static class Owner implements EntityMagnet.IOwner {

		private final Vec3 target;
		private WeakReference<ITurtleAccess> turtle;

		public Owner(ITurtleAccess turtle) {
			this.turtle = new WeakReference<ITurtleAccess>(turtle);
			this.target = Vec3.createVectorHelper(0, 0, 0);
		}

		public synchronized void setTarget(double x, double y, double z) {
			target.xCoord = x;
			target.yCoord = y;
			target.zCoord = z;
		}

		@Override
		public synchronized Vec3 getTarget() {
			ITurtleAccess turtle = this.turtle.get();
			if (turtle == null) return null;

			Vec3 pos = turtle.getPosition().addVector(0.5, 0.5, 0.5);

			pos.yCoord += target.yCoord;
			ForgeDirection side = ForgeDirection.getOrientation(turtle.getFacingDir());
			switch (side) {
				case NORTH:
					pos.xCoord += target.zCoord;
					pos.zCoord -= target.xCoord;
					break;
				case SOUTH:
					pos.xCoord -= target.zCoord;
					pos.zCoord += target.xCoord;
					break;
				case WEST:
					pos.xCoord -= target.xCoord;
					pos.zCoord -= target.zCoord;
					break;
				case EAST:
					pos.xCoord += target.xCoord;
					pos.zCoord += target.zCoord;
					break;
				default:
					break;
			}

			return pos;
		}

		@Override
		public boolean isValid(EntityMagnet magnet) {
			ITurtleAccess turtle = this.turtle.get();
			return turtle != null && turtle.getWorld() != null;
		}
	}

	private WeakReference<EntityMagnet> magnet = new WeakReference<EntityMagnet>(null);
	private Owner magnetOwner;

	private final TurtleSide side;

	private final ITurtleAccess turtle;

	private int fuelTick = 0;

	public MagnetControlAdapter(ITurtleAccess turtle, TurtleSide side) {
		this.turtle = turtle;
		this.side = side;
	}

	@LuaCallable(description = "Activate magnet")
	public void activate() {
		EntityMagnet magnet = this.magnet.get();
		Preconditions.checkState(magnet == null || magnet.isDead, "Magnet already active");
		World world = turtle.getWorld();
		Preconditions.checkNotNull(world, "Trying to spawn magnet, but turtle is unloaded");
		Preconditions.checkState(canSpawn(world), "Can't deploy magnet");
		Preconditions.checkState(turtle.consumeFuel(5), "No fuel");

		magnetOwner = new Owner(turtle);
		magnetOwner.target.zCoord = side == TurtleSide.Left? -1 : 1;
		magnet = new EntityMagnet(world, magnetOwner, true);
		world.spawnEntityInWorld(magnet);

		magnet.playSound("mob.endermen.portal", 1, 1);
		this.magnet = new WeakReference<EntityMagnet>(magnet);
	}

	@LuaCallable(description = "Deactive magnet")
	public void deactivate() {
		despawnMagnet(true);
	}

	@LuaCallable(description = "Set target for magnet")
	public void setTarget(@Arg(name = "x", type = LuaType.NUMBER) double x,
			@Arg(name = "x", type = LuaType.NUMBER) double y,
			@Arg(name = "x", type = LuaType.NUMBER) double z) {
		Preconditions.checkNotNull(magnetOwner, "Magnet not active");
		Preconditions.checkArgument(checkTargetRange(x, y, z), "Target out of range");
		magnetOwner.setTarget(x, y, z);
	}

	@LuaCallable(returnTypes = { LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER },
			description = "Get turtle position")
	public IMultiReturn getPosition() {
		EntityMagnet magnet = getMagnet();
		Vec3 rotated = getRelativeDistance(turtle, magnet);
		return OpenPeripheralAPI.wrap(rotated.xCoord, rotated.yCoord, rotated.zCoord);
	}

	@LuaCallable(returnTypes = LuaType.BOOLEAN, description = "Is magnet above grabbable entity")
	public boolean isAboveEntity() {
		return getMagnet().isAboveTarget();
	}

	@Alias("toggle")
	@LuaCallable(returnTypes = LuaType.BOOLEAN, description = "Grab or release entity/block under magnet")
	public boolean toggleMagnet() {
		return getMagnet().toggleMagnet();
	}

	@LuaCallable(returnTypes = LuaType.BOOLEAN, description = "Is magnet currently grabbing block or entity")
	public boolean isGrabbing() {
		return getMagnet().isLocked();
	}

	@Alias("distance")
	@LuaCallable(returnTypes = { LuaType.NUMBER, LuaType.NUMBER, LuaType.NUMBER })
	public IMultiReturn getDistanceToTarget() {
		EntityMagnet magnet = getMagnet();
		Vec3 current = getRelativeDistance(turtle, magnet);
		Vec3 target = magnetOwner.target;
		return OpenPeripheralAPI.wrap(current.xCoord - target.xCoord,
				current.yCoord - target.yCoord,
				current.zCoord - target.zCoord);
	}

	@Override
	public void onPeripheralUpdate() {
		EntityMagnet magnet = this.magnet.get();
		if (magnet != null && !magnet.isDead) {
			if (++fuelTick >= 20) {
				fuelTick = 0;
				int fuel = magnet.isLocked()? 2 : 1;
				if (!turtle.consumeFuel(fuel)) despawnMagnet(false);

			}
		}
	}

	private static boolean checkTargetRange(double x, double y, double z) {
		return Math.abs(x) <= Config.turtleMagnetRange
				&& Math.abs(y) <= Config.turtleMagnetRange
				&& Math.abs(z) <= Config.turtleMagnetRange;
	}

	private static Vec3 getRelativeDistance(ITurtleAccess turtle, EntityMagnet magnet) {
		Vec3 magnetPos = Vec3.createVectorHelper(magnet.posX, magnet.posY, magnet.posZ);
		Vec3 turtlePos = turtle.getPosition().addVector(0.5, 0.5, 0.5);

		Vec3 dist = turtlePos.subtract(magnetPos);

		ForgeDirection side = ForgeDirection.getOrientation(turtle.getFacingDir());

		switch (side) {
			case NORTH:
				return Vec3.createVectorHelper(-dist.zCoord, dist.yCoord, dist.xCoord);
			case SOUTH:
				return Vec3.createVectorHelper(dist.zCoord, dist.yCoord, -dist.xCoord);
			case EAST:
				return Vec3.createVectorHelper(dist.xCoord, dist.yCoord, dist.zCoord);
			case WEST:
				return Vec3.createVectorHelper(-dist.xCoord, dist.yCoord, -dist.zCoord);
			default:
				return dist;
		}
	}

	private boolean canSpawn(World world) {
		ForgeDirection facing = ForgeDirection.getOrientation(turtle.getFacingDir());
		ForgeDirection spawnSide = facing.getRotation((side == TurtleSide.Left)? ForgeDirection.DOWN : ForgeDirection.UP);
		Vec3 position = turtle.getPosition();
		int x = MathHelper.floor_double(position.xCoord) + spawnSide.offsetX;
		int y = MathHelper.floor_double(position.yCoord) + spawnSide.offsetY;
		int z = MathHelper.floor_double(position.zCoord) + spawnSide.offsetZ;

		return world.isAirBlock(x, y, z);
	}

	private EntityMagnet getMagnet() {
		EntityMagnet magnet = this.magnet.get();
		Preconditions.checkState(magnet != null && !magnet.isDead, "Magnet not active");
		return magnet;
	}

	private void despawnMagnet(boolean checkPosition) {
		EntityMagnet magnet = this.magnet.get();
		Preconditions.checkNotNull(magnet, "Magnet not active");

		Vec3 magnetPos = Vec3.createVectorHelper(magnet.posX, magnet.posY, magnet.posZ);
		Vec3 turtlePos = turtle.getPosition().addVector(0.5, 0.5, 0.5);

		Preconditions.checkState(!checkPosition || canOperateOnMagnet(magnetPos, turtlePos), "Magnet too far");

		magnet.playSound("mob.endermen.portal", 1, 1);
		magnet.setDead();
		this.magnet.clear();
		magnetOwner = null;
	}

	private static boolean canOperateOnMagnet(Vec3 magnetPos, Vec3 turtlePos) {
		return magnetPos.squareDistanceTo(turtlePos) > 1.2 * 1.2;
	}

	@Override
	public World getWorld() {
		return turtle.getWorld();
	}
}
