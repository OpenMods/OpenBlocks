package openblocks.common.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.api.IPointable;
import openblocks.common.entity.EntityItemProjectile;
import openblocks.rpc.ITriggerable;
import openmods.Log;
import openmods.api.ISurfaceAttachment;
import openmods.inventory.legacy.ItemDistribution;
import openmods.sync.SyncableDouble;
import openmods.tileentity.SyncedTileEntity;
import openmods.utils.InventoryUtils;
import openmods.utils.render.GeometryUtils;

public class TileEntityCannon extends SyncedTileEntity implements IPointable, ISurfaceAttachment, ITriggerable, ITickable {

	/*
	 * Blocks and Entities have a right-angle offset
	 */
	private static final int YAW_OFFSET_DEGREES = -90;
	private static final int KNOB_YAW_CHANGE_SPEED = 3;
	private static final int KNOB_PITCH_CHANGE_SPEED = 20;
	private static final int KNOB_VEL_CHANGE_SPEED = 20;
	private static final int KNOB_LOB_MINIMUM_VALUE = 20;
	private static final int KNOB_LOB_MAXIMUM_VALUE = 75;
	private static final int KNOB_LOB_VERTICAL_MUL = 4;
	private static final int KNOB_LOB_HORIZONTAL_MUL = 1;
	private static final int KNOB_LOB_BONUS = 5;

	public SyncableDouble targetPitch;
	public SyncableDouble targetYaw;
	public SyncableDouble targetSpeed;

	public double currentPitch = 45;
	public double currentYaw = 0;
	private double currentSpeed = 1.4;

	public Vec3 motion;

	public boolean renderLine = true;
	private int ticksSinceLastFire = Integer.MAX_VALUE;

	private Vec3 projectileOrigin = null;

	@Override
	protected void createSyncedFields() {
		targetPitch = new SyncableDouble();
		targetYaw = new SyncableDouble();
		targetSpeed = new SyncableDouble(1.4);
	}

	@Override
	public void update() {
		checkOrigin();

		if (Double.isNaN(currentPitch)) {
			Log.warn("Pitch was NaN");
			currentPitch = 45;
			targetPitch.set(currentPitch);
		}
		if (Double.isNaN(currentYaw)) {
			Log.warn("Yaw was NaN");
			currentYaw = 0;
		}

		// ugly, need to clean
		currentPitch = currentPitch - ((currentPitch - targetPitch.get()) / KNOB_PITCH_CHANGE_SPEED);
		currentYaw = GeometryUtils.normalizeAngle(currentYaw);

		final double targetYaw = GeometryUtils.normalizeAngle(this.targetYaw.get());
		if (Math.abs(currentYaw - targetYaw) < KNOB_YAW_CHANGE_SPEED) currentYaw = targetYaw;
		else {
			double dist = GeometryUtils.getAngleDistance(currentYaw, targetYaw);
			currentYaw += KNOB_YAW_CHANGE_SPEED * Math.signum(dist);
		}

		currentSpeed = currentSpeed - ((currentSpeed - targetSpeed.get()) / KNOB_VEL_CHANGE_SPEED);

		invalidateMotion();

		if (!worldObj.isRemote) {
			if (worldObj.getTotalWorldTime() % 20 == 0) {
				if (worldObj.isBlockIndirectlyGettingPowered(pos) > 0) {
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
		for (EnumFacing direction : EnumFacing.VALUES) {
			IInventory inventory = InventoryUtils.getInventory(worldObj, pos, direction);
			if (inventory != null) {
				ItemStack stack = ItemDistribution.removeFromFirstNonEmptySlot(inventory);
				if (stack != null) return stack;
			}
		}

		return null;
	}

	private void fireStack(ItemStack stack) {
		final ITriggerable rpc = createServerRpcProxy(ITriggerable.class);
		rpc.trigger();

		// projectileOrigin is not used here, it's used for the calculations below.
		EntityItem item = new EntityItemProjectile(worldObj, pos.getX() + 0.5, pos.getY(), pos.getZ(), stack);
		item.setDefaultPickupDelay();

		// Now that we generate vectors instead of eular angles, this should be revised.
		Vec3 motion = getMotion();
		item.motionX = motion.xCoord;
		item.motionY = motion.yCoord;
		item.motionZ = motion.zCoord;
		worldObj.spawnEntityInWorld(item);
		playSoundAtBlock("openblocks:cannon.activate", 0.2f, 1.0f);
	}

	@Override
	public void trigger() {
		ticksSinceLastFire = 0;
		double pitchRad = Math.toRadians(currentYaw - 90);
		double x = -0.5 * Math.cos(pitchRad);
		double z = -0.5 * Math.sin(pitchRad);
		for (int i = 0; i < 20; i++) {
			spawnParticle((i < 4? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.SMOKE_NORMAL),
					x + 0.3 + (worldObj.rand.nextDouble() * 0.4),
					0.7,
					z + 0.3 + (worldObj.rand.nextDouble() * 0.4),
					0.0D, 0.0D, 0.0D);
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

		return new Vec3(-cosPitch * sinYaw * currentSpeed,
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

	private void checkOrigin() {
		if (projectileOrigin == null) {
			projectileOrigin = new Vec3(pos).addVector(0.5, 0, 0.5);
		}
	}

	public void setTarget(BlockPos pos) {
		checkOrigin();
		// We target the middle of the block, at the very top.
		final Vec3 target = new Vec3(pos).addVector(0.5, 1, 0.5);

		// Horizontal distance between the origin and target
		final double distHorizontal = KNOB_LOB_HORIZONTAL_MUL * Math.sqrt(
				Math.pow(target.xCoord - projectileOrigin.xCoord, 2)
						+ Math.pow(target.zCoord - projectileOrigin.zCoord, 2));

		// No vertical multiplier is applied for decline slopes.
		final double distVertical = Math.max((target.yCoord - projectileOrigin.yCoord) * KNOB_LOB_VERTICAL_MUL, 0);

		// Calculate the arc of the trajectory
		final float lobScale = (float)
				Math.min(KNOB_LOB_MAXIMUM_VALUE,
						Math.max(KNOB_LOB_MINIMUM_VALUE,
								KNOB_LOB_BONUS + distHorizontal + distVertical));

		// Calculate the velocity of the projectile
		final Vec3 velocity = TileEntityCannonLogic.calculateTrajectory(projectileOrigin, target, lobScale);

		// m/s applied to item.
		final double speed = velocity.lengthVector();
		targetSpeed.set(speed);

		// reverse the vector to angles for cannon model
		final Vec3 direction = velocity.normalize();
		final double pitch = Math.asin(direction.yCoord);
		final double yaw = Math.atan2(direction.zCoord, direction.xCoord);

		// Set yaw and pitch
		targetYaw.set(Math.toDegrees(yaw) + YAW_OFFSET_DEGREES);
		targetPitch.set(Math.toDegrees(pitch));

		currentYaw = targetYaw.get();
		currentPitch = targetPitch.get();

		// Sync targets
		sync();
	}

	public void disableLineRender() {
		renderLine = false;
	}

	@Override
	public void onPointingStart(ItemStack itemStack, EntityPlayer player) {
		player.addChatComponentMessage(new ChatComponentTranslation("openblocks.misc.selected_cannon"));
	}

	@Override
	public void onPointingEnd(ItemStack itemStack, EntityPlayer player, BlockPos pos) {
		player.addChatMessage(new ChatComponentTranslation("openblocks.misc.pointed_cannon", pos.getX(), pos.getY(), pos.getZ()));
		setTarget(pos);
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
		 * Increment the counter below as an increasing warning to the next
		 * sorry soul that thinks they can make this work better.
		 * Regards -NC
		 */

		public static final int HOURS_WASTED_ON_CANNON_LOGIC = 14;

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

		/**
		 * Squared timestep for acceleration
		 */
		private static final double PHYS_PARTIAL_TIME_SQUARE = PHYS_PARTIAL_TIME * PHYS_PARTIAL_TIME;

		/**
		 * Physics gravity vector in partial time squared for acceleration calculation
		 */
		private static final Vec3 PHYS_GRAVITY_VECTOR_SQUARE_PARTIAL = new Vec3(0, PHYS_PARTIAL_TIME_SQUARE * -PHYS_PARTIAL_WORLD_GRAVITY, 0);

		/**
		 * The actual work for calculating trajectory. Which is much simpler now.
		 *
		 * @param start
		 *            The origin of the projectile to be fired
		 * @param target
		 *            The target location of the projectile
		 * @param scale
		 *            The arcing size of the trajectory
		 * @return Vector to achieve trajectory
		 */
		public static Vec3 calculateTrajectory(Vec3 start, Vec3 target, float scale) {
			final double n = scale * PHYS_STEPS_PER_SECOND;
			final double accelerationMultiplier = 0.5 * n * n + n; // (n^2+n)/2

			final Vec3 scaledAcceleration = new Vec3(
					PHYS_GRAVITY_VECTOR_SQUARE_PARTIAL.xCoord * accelerationMultiplier,
					PHYS_GRAVITY_VECTOR_SQUARE_PARTIAL.yCoord * accelerationMultiplier,
					PHYS_GRAVITY_VECTOR_SQUARE_PARTIAL.zCoord * accelerationMultiplier
					);

			// -1 /n * Phys = -Phys / n
			final double velocityMultiplier = -PHYS_STEPS_PER_SECOND / n;

			final Vec3 velocity = new Vec3(
					(start.xCoord + scaledAcceleration.xCoord - target.xCoord) * velocityMultiplier,
					(start.yCoord + scaledAcceleration.yCoord - target.yCoord) * velocityMultiplier,
					(start.zCoord + scaledAcceleration.zCoord - target.zCoord) * velocityMultiplier
					);

			return velocity;
		}
	}

	@Override
	public EnumFacing getSurfaceDirection() {
		return EnumFacing.DOWN;
	}
}
