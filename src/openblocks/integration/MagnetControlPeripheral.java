package openblocks.integration;

import static openblocks.integration.CCUtils.TRUE;
import static openblocks.integration.CCUtils.toDouble;
import static openblocks.integration.CCUtils.wrap;

import java.lang.ref.WeakReference;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openblocks.Config;
import openblocks.Log;
import openblocks.common.entity.EntityMagnet;
import openblocks.common.entity.EntityMagnet.OwnerType;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.ILuaContext;
import dan200.turtle.api.ITurtleAccess;
import dan200.turtle.api.TurtleSide;

public class MagnetControlPeripheral implements IHostedPeripheral {

	public static class Owner implements EntityMagnet.IOwner {

		private final Vec3 target;
		private WeakReference<ITurtleAccess> turtle;
		private WeakReference<World> world;

		public Owner(ITurtleAccess turtle) {
			this(turtle.getWorld(), turtle);
		}

		public Owner(World world) {
			this(world, null);
		}

		private Owner(World world, ITurtleAccess turtle) {
			this.world = new WeakReference<World>(world);
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
		public OwnerType getType() {
			return OwnerType.TURTLE;
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			return true;
		}

		@Override
		public boolean isValid(EntityMagnet magnet) {
			ITurtleAccess turtle = this.turtle.get();
			return turtle != null && turtle.getWorld() != null;
		}

		@Override
		public void read(ByteArrayDataInput input) {
			World world = this.world.get();
			Preconditions.checkNotNull(world, "world == null. Generally not a nice thing.");

			int x = input.readInt();
			int y = input.readInt();
			int z = input.readInt();

			TileEntity te = world.getBlockTileEntity(x, y, z);

			if (te instanceof ITurtleAccess) turtle = new WeakReference<ITurtleAccess>((ITurtleAccess)te);
			else Log.warn("Trying to create crane owner for (%d,%d,%d), but TE is %s", x, y, z, te);

			target.xCoord = input.readDouble();
			target.xCoord = input.readDouble();
			target.xCoord = input.readDouble();
		}

		@Override
		public void update(EntityMagnet magnet) {}

		@Override
		public void write(ByteArrayDataOutput output) {
			ITurtleAccess turtle = this.turtle.get();
			Preconditions.checkNotNull(turtle, "turtle == null. Generally not a nice thing.");
			Vec3 position = turtle.getPosition();
			// Might break somewhere in deep farlands, I quess
			output.writeInt(MathHelper.floor_double(position.xCoord));
			output.writeInt(MathHelper.floor_double(position.yCoord));
			output.writeInt(MathHelper.floor_double(position.zCoord));

			// initial position
			output.writeDouble(target.xCoord);
			output.writeDouble(target.yCoord);
			output.writeDouble(target.yCoord);
		}
	}

	private WeakReference<EntityMagnet> magnet = new WeakReference<EntityMagnet>(null);
	private Owner magnetOwner;

	private TurtleSide side;

	private final ITurtleAccess turtle;

	private int fuelTick = 0;

	public MagnetControlPeripheral(ITurtleAccess turtle, TurtleSide side) {
		this.turtle = turtle;
		this.side = side;
	}

	@Override
	public String getType() {
		return "magnet";
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {}

	@Override
	public boolean canAttachToSide(int side) {
		return true;
	}

	@Override
	public void attach(IComputerAccess computer) {}

	@Override
	public void detach(IComputerAccess computer) {}

	@Override
	public String[] getMethodNames() {
		return new String[] { "activate", "deactivate", "setTarget", "getPosition", "toggle", "isAboveEntity", "isGrabbing", "toTarget" };
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws Exception {
		switch (method) {
			case 0: // activate
				return spawnMagnet();
			case 1: // deactivate
				return despawnMagnet(true);
			case 2: {// setTarget
				if (magnetOwner == null) return wrap(false, "Magnet not active");

				Preconditions.checkArgument(arguments.length == 3, "Method needs three numbers");
				double x = toDouble(arguments[0]);
				double y = toDouble(arguments[1]);
				double z = toDouble(arguments[2]);
				if (!checkTargetRange(x, y, z)) return wrap(false, "Target out of range");

				magnetOwner.setTarget(x, y, z);
				return TRUE;
			}
			case 3: {// getPosition
				EntityMagnet magnet = this.magnet.get();
				if (magnet == null || magnet.isDead) return wrap(false, "Magnet not active");
				Vec3 rotated = getRelativeDistance(turtle, magnet);
				return wrap(rotated.xCoord, rotated.yCoord, rotated.zCoord);
			}
			case 4: {// toggle
				EntityMagnet magnet = this.magnet.get();
				if (magnet == null || magnet.isDead) return wrap(false, "Magnet not active");
				return wrap(magnet.toggleMagnet());
			}
			case 5: { // isAboveEntity
				EntityMagnet magnet = this.magnet.get();
				if (magnet == null || magnet.isDead) return wrap(false, "Magnet not active");
				return wrap(magnet.isAboveTarget());
			}
			case 6: {// isGrabbing
				EntityMagnet magnet = this.magnet.get();
				if (magnet == null || magnet.isDead) return wrap(false, "Magnet not active");
				return wrap(magnet.isLocked());
			}
			case 7: { // toTarget
				EntityMagnet magnet = this.magnet.get();
				if (magnet == null || magnet.isDead) return wrap(false, "Magnet not active");
				Vec3 current = getRelativeDistance(turtle, magnet);
				Vec3 target = magnetOwner.target;
				Vec3 dist = target.subtract(current);
				return wrap(dist.xCoord, dist.yCoord, dist.zCoord);
			}
		}

		throw new IllegalArgumentException("Invalid method id: " + method);
	}

	@Override
	public void update() {
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
		return Math.abs(x) <= Config.turtleMagnetRange &&
				Math.abs(y) <= Config.turtleMagnetRange &&
				Math.abs(z) <= Config.turtleMagnetRange;
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

	private Object[] spawnMagnet() {
		EntityMagnet magnet = this.magnet.get();
		if (magnet != null && !magnet.isDead) return wrap(false, "Magnet already active");
		World world = turtle.getWorld();
		if (world == null) {
			Log.warn("Trying to spawn magnet, but turtle is unloaded");
			return wrap(false, "WTF?");
		}

		if (!canSpawn(world)) return wrap(false, "Can't deploy magnet");

		if (!turtle.consumeFuel(5)) return wrap(false, "No fuel");

		magnetOwner = new Owner(turtle);
		magnetOwner.target.zCoord = side == TurtleSide.Left? -1 : 1;
		magnet = new EntityMagnet(world, magnetOwner, true);
		world.spawnEntityInWorld(magnet);

		magnet.playSound("mob.endermen.portal", 1, 1);
		this.magnet = new WeakReference<EntityMagnet>(magnet);
		return TRUE;
	}

	private Object[] despawnMagnet(boolean checkPosition) {
		EntityMagnet magnet = this.magnet.get();
		if (magnet == null) return wrap(false, "Magnet not active");

		Vec3 magnetPos = Vec3.createVectorHelper(magnet.posX, magnet.posY, magnet.posZ);
		Vec3 turtlePos = turtle.getPosition().addVector(0.5, 0.5, 0.5);

		if (checkPosition && magnetPos.squareDistanceTo(turtlePos) > 1.2 * 1.2) return wrap(false, "Magnet too far");

		magnet.playSound("mob.endermen.portal", 1, 1);
		magnet.setDead();
		this.magnet.clear();
		magnetOwner = null;

		return TRUE;
	}
}
