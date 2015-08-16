package openblocks.common.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.api.IPointable;
import openblocks.common.entity.EntityItemProjectile;
import openblocks.rpc.ICannon;
import openmods.api.ISurfaceAttachment;
import openmods.inventory.legacy.ItemDistribution;
import openmods.sync.SyncableDouble;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.InventoryUtils;
import openmods.utils.render.GeometryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCannon extends SyncedTileEntity implements IPointable, ISurfaceAttachment, ICannon {

	private static final int YAW_CHANGE_SPEED = 3;
	public SyncableDouble targetPitch;
	public SyncableDouble targetYaw;
	public SyncableDouble targetSpeed;

	public double currentPitch = 45;
	public double currentYaw = 0;
	private double currentSpeed = 1.4;

	public Vec3 motion;

	public boolean renderLine = true;

	private int ticksSinceLastFire = Integer.MAX_VALUE;

	@Override
	protected void createSyncedFields() {
		targetPitch = new SyncableDouble();
		targetYaw = new SyncableDouble();
		targetSpeed = new SyncableDouble(1.4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void prepareForInventoryRender(Block block, int metadata) {
		super.prepareForInventoryRender(block, metadata);
		renderLine = false;
	}

	@Override
	public void updateEntity() {
        if(Double.isNaN(currentPitch)) {
            System.out.println("Pitch was NaN");
            currentPitch = 45;
            targetPitch.set(currentPitch);
        }
        if(Double.isNaN(currentYaw)) {
            System.out.println("Yaw was NaN");
            currentYaw = 0;
        }

        currentPitch = targetPitch.get();
        currentYaw = targetYaw.get();
        currentSpeed = targetSpeed.get();

		super.updateEntity();

		// ugly, need to clean
		currentPitch = currentPitch - ((currentPitch - targetPitch.get()) / 20);
		currentYaw = GeometryUtils.normalizeAngle(currentYaw);

		final double targetYaw = GeometryUtils.normalizeAngle(this.targetYaw.get());
		if (Math.abs(currentYaw - targetYaw) < YAW_CHANGE_SPEED) currentYaw = targetYaw;
		else {
			double dist = GeometryUtils.getAngleDistance(currentYaw, targetYaw);
			currentYaw += YAW_CHANGE_SPEED * Math.signum(dist);
		}

		currentSpeed = currentSpeed - ((currentSpeed - targetSpeed.get()) / 20);

		invalidateMotion();

		if (!worldObj.isRemote) {
			if (worldObj.getTotalWorldTime() % 20 == 0) {
				if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
					ItemStack stack = findStack();
					if (stack != null) fireStack(stack);
				}
			}
		} else {
			if (ticksSinceLastFire < 100) {
				ticksSinceLastFire++;
			}
		}
	}

	private ItemStack findStack() {
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			IInventory inventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord, zCoord, direction);
			if (inventory != null) {
				ItemStack stack = ItemDistribution.removeFromFirstNonEmptySlot(inventory);
				if (stack != null) return stack;
			}
		}

		return null;
	}

	private void fireStack(ItemStack stack) {
		final ICannon rpc = createServerRpcProxy(ICannon.class);
		rpc.fireCannon();

		EntityItem item = new EntityItemProjectile(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, stack);
		item.delayBeforeCanPickup = 20;

		Vec3 motion = getMotion();
		item.motionX = motion.xCoord;
		item.motionY = motion.yCoord;
		item.motionZ = motion.zCoord;
		worldObj.spawnEntityInWorld(item);
		worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:cannon.activate", 0.2f, 1.0f);
	}

	@Override
	public void fireCannon() {
		ticksSinceLastFire = 0;
		double pitchRad = Math.toRadians(currentYaw - 90);
		double x = -0.5 * Math.cos(pitchRad);
		double z = -0.5 * Math.sin(pitchRad);
		for (int i = 0; i < 20; i++) {
			worldObj.spawnParticle((i < 4? "large" : "") + "smoke", x + xCoord + 0.3 + (worldObj.rand.nextDouble() * 0.4), yCoord + 0.7, z + zCoord + 0.3 + (worldObj.rand.nextDouble() * 0.4), 0.0D, 0.0D, 0.0D);
		}
	}

	public int getTicksSinceLastFire() {
		return ticksSinceLastFire;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(32.0, 32.0, 32.0);
	}

	private Vec3 calcMotionFromAngles() {
		double p = Math.toRadians(currentPitch);
		double y = Math.toRadians(180 - currentYaw);
		double sinPitch = Math.sin(p);
		double cosPitch = Math.cos(p);
		double sinYaw = Math.sin(y);
		double cosYaw = Math.cos(y);

		return Vec3.createVectorHelper(-cosPitch * sinYaw * currentSpeed,
                sinPitch * currentSpeed,
                -cosPitch * cosYaw * currentSpeed);
	}

	private void invalidateMotion() {
		motion = null;
	}

	public Vec3 getMotion() {
		if (motion == null) motion = calcMotionFromAngles();
		return motion;
	}

    // Copied from Bukkit Forums, adjusts for Block/Entity rotations having different north.
    public static float getLookAtYaw(Vec3 motion) {
        double dx = motion.xCoord;
        double dz = motion.yCoord;
        double yaw = 0;
        // Set yaw
        if (dx != 0) {
            // Set yaw start value based on dx
            if (dx < 0) {
                yaw = 1.5 * Math.PI;
            } else {
                yaw = 0.5 * Math.PI;
            }
            yaw -= Math.atan(dz / dx);
        } else if (dz < 0) {
            yaw = Math.PI;
        }
        return (float) (-yaw * 180 / Math.PI - 90); // -90 because Minecraft.
    }

	public void setTarget(int x, int y, int z) {

        final Vec3 origin = Vec3.createVectorHelper(xCoord + 0.5, yCoord, zCoord + 0.5);
        final Vec3 target = Vec3.createVectorHelper(x + 0.5, y + 1, z + 0.5);
        final Vec3 gravity = Vec3.createVectorHelper(0, -TileEntityCannonLogic.PHYS_PARTIAL_WORLD_GRAVITY, 0);
        final double dim2Distance = Math.sqrt(
                Math.pow(target.xCoord - origin.xCoord, 2)
                + Math.pow(target.zCoord - origin.zCoord, 2));

        final double verticalLobScale = Math.min(20, Math.max(0, target.yCoord - origin.yCoord) * 4);

        final float lobScale = (float)Math.max(20, 5 + dim2Distance + verticalLobScale);

        final Vec3 velocity = TileEntityCannonLogic.calculateTrajectory(origin, target, gravity, lobScale);

        System.out.println(velocity);

        final Vec3 direction = velocity.normalize();
        final double force = velocity.lengthVector();

        final double pitch = Math.asin(direction.yCoord);

        // Reverse our velocity and force in to angles for the cannon model.

        // Probably right, use other method for the moment
		final double atan2 = Math.atan2(direction.zCoord, direction.xCoord);
		final double yawDegrees = Math.toDegrees(atan2) - 90;
		targetYaw.set(yawDegrees);
		currentYaw = targetYaw.get();


		targetPitch.set(Math.toDegrees(pitch));
		currentPitch = targetPitch.get();

		// We have selected what we feel to be the best angle
		// But the velocity suggested doesn't scale on all 3 axis
		// So we have to change that a bit
		targetSpeed.set(force);
		sync();

        System.out.println(getMotion());
	}

	public void disableLineRender() {
		renderLine = false;
	}

	@Override
	public void onPointingStart(ItemStack itemStack, EntityPlayer player) {
		player.addChatComponentMessage(new ChatComponentTranslation("openblocks.misc.selected_cannon"));
	}

	@Override
	public void onPointingEnd(ItemStack itemStack, EntityPlayer player, int x, int y, int z) {
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.pointed_cannon", x, y, z));
		setTarget(x, y, z);
	}

	public void setSpeed(double speed) {
		targetSpeed.set(speed);
		sync();
	}

	public void setPitch(double pitch2) {
		targetPitch.set(pitch2);
		sync();
	}

	public void setYaw(double yaw2) {
		targetYaw.set(yaw2);
		sync();
	}

	static class TileEntityCannonLogic {

		/*
		 * Hello, If you think you can improve the code below to work better,
		 * all power to you! But please, if you give up and revert your changes.
		 * Please
		 * increment the counter below as an increasing warning to the next
		 * sorry soul
		 * that thinks they can make this work better. Regards -NC
		 */

		public static final int HOURS_WASTED_ON_CANNON_LOGIC = 13;

        /**
         * 20 physics ticks per second (on a good day)
         */
        private static final double PHYS_STEPS_PER_SECOND = 20D;

        /**
         * Physics calculation time in partial seconds
         */
        private static final double PHYS_PARTIAL_TIME = 1D / PHYS_STEPS_PER_SECOND;

        /**
         * Minecraft known gravity in meters/second/second
         */
        private static final double PHYS_WORLD_GRAVITY = 0.8D;

        /**
         * Amount of gravity acceleration per physics tick.
         */
        private static final double PHYS_PARTIAL_WORLD_GRAVITY = PHYS_WORLD_GRAVITY * PHYS_PARTIAL_TIME;

        public static Vec3 calculateTrajectory(Vec3 startPosition, Vec3 targetPosition, Vec3 gravity, float scale) {
            final double physicsTimestep = PHYS_PARTIAL_TIME;
            final double physicsTimestepSquare = physicsTimestep * physicsTimestep;
            final double timestepPerSecond = PHYS_STEPS_PER_SECOND;
            final double n = scale * timestepPerSecond;

            final Vec3 a = Vec3.createVectorHelper(
                    gravity.xCoord * physicsTimestepSquare,
                    gravity.yCoord * physicsTimestepSquare,
                    gravity.zCoord * physicsTimestepSquare
            );

            final Vec3 p = targetPosition;
            final Vec3 s = startPosition;

            final double nSquare = n * n;
            final double nModifier = 0.5 * nSquare + n; // ( /2f )
            final Vec3 scaledAcceleration = Vec3.createVectorHelper(
                    a.xCoord * nModifier,
                    a.yCoord * nModifier,
                    a.zCoord * nModifier
            );

            final double inverseNegN = -1.0 / n;
            final double finalMultiplier = inverseNegN * timestepPerSecond; // ( Same as /physicsTimestep)

            final Vec3 velocity = Vec3.createVectorHelper(
                    (s.xCoord + scaledAcceleration.xCoord - p.xCoord) * finalMultiplier,
                    (s.yCoord + scaledAcceleration.yCoord - p.yCoord) * finalMultiplier,
                    (s.zCoord + scaledAcceleration.zCoord - p.zCoord) * finalMultiplier
            );

            return velocity;
        }
	}

	@Override
	public ForgeDirection getSurfaceDirection() {
		return ForgeDirection.DOWN;
	}
}
