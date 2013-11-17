package openblocks.common.tileentity;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import openblocks.api.IPointable;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.entity.EntityItemProjectile;
import openblocks.common.events.TileEntityMessageEventPacket;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableDouble;
import openblocks.utils.GeometryUtils;
import openblocks.utils.InventoryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCannon extends SyncedTileEntity implements IActivateAwareTile, IPointable {

	public SyncableDouble targetPitch;
	public SyncableDouble targetYaw;
	public SyncableDouble targetSpeed;

	public double currentPitch = 45;
	public double currentYaw = 0;
	public double currentSpeed = 1.4;

	public double motionX = 0;
	public double motionY = 0;
	public double motionZ = 0;

	public boolean renderLine = true;

	private int ticksSinceLastFire = Integer.MAX_VALUE;

	public TileEntityCannon() {}

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
		super.updateEntity();

		if(currentYaw < 0) currentYaw += 360;
		currentYaw %= 360;
		double rotationSpeed = 3;
		// ugly, need to clean
		currentPitch = currentPitch - ((currentPitch - targetPitch.getValue()) / 20);
		if(Math.abs(currentYaw - targetYaw.getValue()) < rotationSpeed) 
			currentYaw = targetYaw.getValue();
		else
			currentYaw += rotationSpeed * GeometryUtils.getDirectionForRotation(currentYaw, targetYaw.getValue());
		currentSpeed = currentSpeed - ((currentSpeed - targetSpeed.getValue()) / 20);
		// currentPitch = targetPitch.getValue();
		// currentYaw = targetYaw.getValue();
		// currentSpeed = targetSpeed.getValue();
		getMotionFromAngles();

		if (!worldObj.isRemote) {

			if (worldObj.getWorldTime() % 20 == 0) {
				if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
					for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
						IInventory inventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord, zCoord, direction);
						if (inventory != null) {
							ItemStack stack = InventoryUtils.removeNextItemStack(inventory);
							if (stack != null) {
								getMotionFromAngles();
								new TileEntityMessageEventPacket(this).sendToWatchers((WorldServer)worldObj);
								EntityItem item = new EntityItemProjectile(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, stack);
								item.delayBeforeCanPickup = 20;
								item.motionX = motionX * currentSpeed;
								item.motionY = motionY * currentSpeed;
								item.motionZ = motionZ * currentSpeed;
								worldObj.spawnEntityInWorld(item);
								worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:cannon", 0.2f, 1.0f);
								break;
							}
						}
					}

				}
			}
		} else {
			if (ticksSinceLastFire < 100) {
				ticksSinceLastFire++;
			}
		}
	}

	@Override
	public void onEvent(TileEntityMessageEventPacket event) {
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

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public void onSynced(Set<ISyncableObject> changes) {
		getMotionFromAngles();
	}

	private void getMotionFromAngles() {
		double p = Math.toRadians(currentPitch);
		double y = Math.toRadians(180 - currentYaw);
		double sinPitch = Math.sin(p);
		double cosPitch = Math.cos(p);
		double sinYaw = Math.sin(y);
		double cosYaw = Math.cos(y);

		motionX = -cosPitch * sinYaw;
		motionY = sinPitch;
		motionZ = -cosPitch * cosYaw;
	}

	public void setTarget(int x, int y, int z) {

		// right, first we get the distance
		double dX = (xCoord + 0.5) - (x + 0.5);
		double dY = -(yCoord - y);
		double dZ = (zCoord + 0.5) - (z + 0.5);

		double yawDegrees = Math.toDegrees(Math.atan2(dZ, dX)) + 90;
		targetYaw.setValue(yawDegrees);
		currentYaw = targetYaw.getValue();

		double[] calc = TileEntityCannonLogic.getVariableVelocityTheta(dX, dY, dZ);
		double theta = Math.max(calc[0], calc[1]);
		targetPitch.setValue(Math.toDegrees(theta));
		currentPitch = targetPitch.getValue();

		// We have selected what we feel to be the best angle
		// But the velocity suggested doesn't scale on all 3 axis
		// So we have to change that a bit
		double d = Math.sqrt( dX * dX + dZ * dZ );
		double v = Math.sqrt((d * -TileEntityCannonLogic.WORLD_GRAVITY) / Math.sin(2 * theta));
		targetSpeed.setValue(v);
		sync();
	}

	public void disableLineRender() {
		renderLine = false;
	}

	@Override
	public void onPoint(ItemStack itemStack, EntityPlayer player, int x, int y, int z) {
		player.sendChatToPlayer(ChatMessageComponent.createFromText(String.format("Pointed cannon at %s, %s, %s", x, y, z)));
		setTarget(x, y, z);
	}

	public void setSpeed(double speed) {
		targetSpeed.setValue(speed);
		sync();
	}

	public void setPitch(double pitch2) {
		targetPitch.setValue(pitch2);
		sync();
	}

	public void setYaw(double yaw2) {
		targetYaw.setValue(yaw2);
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

		public static final int HOURS_WASTED_ON_CANNON_LOGIC = 10;

		public static final double CANNON_VELOCITY = 8 * 0.05; // 8
																// meters/second
		public static final double WORLD_GRAVITY = -0.8 * 0.05; // World Gravity
																// in
																// meters/second/second

		public static double[] getThetaByAngle(double deltaX, double deltaY, double deltaZ, double v) {
			v += 0.5;
			double r = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
			double e = Math.atan2(deltaY, r);
			double g = WORLD_GRAVITY;
			double c1 = Math.sqrt(Math.pow(v, 4) - g * (g * r * r * Math.pow(Math.cos(e), 2) + 2 * (v * v) * r * Math.sin(e)));
			double c2 = g * r * Math.cos(e);
			return new double[] {
					Math.atan(v * v + c1 / c2),
					Math.atan(v * v - c1 * c2)
			};
		}

		public static double[] getVariableVelocityTheta(double deltaX, double deltaY, double deltaZ) {
			double velocity = CANNON_VELOCITY;
			double[] theta = getThetaToPoint(deltaX, deltaY, deltaZ, velocity);
			int iterations = 100;
			while (Double.isNaN(theta[0]) && Double.isNaN(theta[1]) && --iterations > 0) {
				velocity += 0.025;
				theta = getThetaToPoint(deltaX, deltaY, deltaZ, velocity);
			}
			double[] result = new double[3];
			result[0] = theta[0];
			result[1] = theta[1];
			result[2] = velocity;
			return result;
		}

		public static double[] getThetaToPoint(double deltaX, double deltaY, double deltaZ, double velocity) {
			double x = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
			double y = deltaY + 0.4;
			double v = velocity;
			double g = WORLD_GRAVITY;
			double[] theta = new double[2];
			double mComponent = (v * v * v * v) - g * (g * (x * x) + 2 * (y * (v * v)));
			if (mComponent < 0) return new double[] { Double.NaN, Double.NaN };
			mComponent *= 100;
			mComponent = Math.sqrt(mComponent);
			mComponent /= 10;
			mComponent /= (g * x);
			theta[0] = Math.atan(v * v + mComponent);
			theta[1] = Math.atan(v * v - mComponent);
			return theta;
		}
	}
}
