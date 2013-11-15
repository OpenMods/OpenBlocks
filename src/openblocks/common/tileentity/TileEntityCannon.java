package openblocks.common.tileentity;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.api.IPointable;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.entity.EntityMount;
import openblocks.network.TileEntityMessageEventPacket;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableDouble;
import openblocks.sync.SyncableInt;
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

		// ugly, need to clean
		currentPitch = currentPitch - ((currentPitch - targetPitch.getValue()) / 20);
		currentYaw = currentYaw - ((currentYaw - targetYaw.getValue()) / 20);
		currentSpeed = currentSpeed - ((currentSpeed - targetSpeed.getValue()) / 20);
		//currentPitch = targetPitch.getValue();
		//currentYaw = targetYaw.getValue();
		//currentSpeed = targetSpeed.getValue();
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
								sendEventToPlayers();
								EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, stack);
								item.delayBeforeCanPickup = 20;
								item.motionX = motionX * currentSpeed;
								item.motionY = motionY * currentSpeed;
								item.motionZ = motionZ * currentSpeed;
								worldObj.spawnEntityInWorld(item);
								worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:mortar", 0.2f, 1.0f);
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

	public void onEvent(TileEntityMessageEventPacket event) {
		ticksSinceLastFire = 0;
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
	public void onSynced(List<ISyncableObject> changes) {
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
		double dX = ((double)xCoord + 0.5) - ((double)x + 0.5);
		double dY = ((double)yCoord + 0.5) - ((double)y + 0.5);
		double dZ = ((double)zCoord + 0.5) - ((double)z + 0.5);

		double dist = dX*dX + dY*dY + dZ*dZ;
		
		// find the yaw, and give it a fixed pitch
		targetYaw.setValue(Math.toDegrees(Math.atan2(dZ, dX)) + 90);
		targetPitch.setValue(30);
		currentYaw = targetYaw.getValue();
		currentPitch = targetPitch.getValue();
		
		// so, starting with tiny amount of speed (0.01), we keep increasing
		// the speed and checking the distance that it falls below the target Y point
		// if the distance is further than the distance to the target, we've found the
		// speed we want
		double speed = 0.01;
		double d;
		while ((d = getDistance(speed, (double)y + 0.5)) < dist) {
			speed += 0.01;
		}
		
		System.out.println("Distance = " + dist);
		System.out.println("Distance = " + d);
		setSpeed(speed);
		sync();
	}
	
	private double getDistance(double speed, double y) {
		getMotionFromAngles();
		double posX = (double)xCoord + 0.5;
		double posY = (double)yCoord + 0.5;
		double posZ = (double)zCoord + 0.5;
		double prevPosY = posY;
		double prevPosX = posX;
		double prevPosZ = posZ;
		for (int i = 0; i < 100; i++) {
			motionY -= 0.03999999910593033D;
			posX += motionX * speed;
			posY += motionY * speed;
			posZ += motionZ * speed;
			motionX *= 0.98;
			motionY *= 0.9800000190734863D;
			motionZ *= 0.98;
			if (posY < y) {
				double dX = ((double)xCoord + 0.5) - posX + 0.5;
				double dY = ((double)yCoord + 0.5) - posY;
				double dZ = ((double)zCoord + 0.5) - posZ + 0.5;
				return dX*dX + dY*dY + dZ*dZ;
			}
			prevPosY = posY;
			prevPosX = posX;
			prevPosZ = posZ;
		}
		return Integer.MIN_VALUE;
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
}
