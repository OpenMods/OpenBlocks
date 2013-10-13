package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.common.api.IAwareTile;
import openblocks.common.entity.EntityMount;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableDouble;
import openblocks.sync.SyncableInt;
import openblocks.utils.InventoryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCannon extends NetworkedTileEntity implements IAwareTile {

	private EntityMount cannon = null;

	public SyncableDouble pitch = new SyncableDouble();
	public SyncableDouble yaw = new SyncableDouble();
	public SyncableInt cannonId = new SyncableInt(0);
	public SyncableInt ridingEntity = new SyncableInt(0);

	public double motionX = 0;
	public double motionY = 0;
	public double motionZ = 0;

	public boolean renderLine = true;

	public enum Keys {
		pitch, yaw, cannonId, ridingEntity
	}

	public TileEntityCannon() {
		addSyncedObject(Keys.pitch, pitch);
		addSyncedObject(Keys.yaw, yaw);
		addSyncedObject(Keys.cannonId, cannonId);
		addSyncedObject(Keys.ridingEntity, ridingEntity);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (cannon == null) {
			ridingEntity.setValue(0);
		}
		if (cannon != null && cannon.riddenByEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)cannon.riddenByEntity;
			double p = player.rotationPitch;
			double y = player.rotationYawHead;
			pitch.setValue(p);
			yaw.setValue(y);
			sync();
		}

		if (cannon != null && cannon.riddenByEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)cannon.riddenByEntity;
			Vec3 pos = getPositionDistanceAway(-0.7, player.rotationPitch, player.rotationYawHead + 90);
			if (worldObj.isRemote) {
				cannon.posX = pos.xCoord + 0.5 + xCoord;
				cannon.posY = yCoord;
				cannon.posZ = pos.zCoord + 0.5 + zCoord;
				cannon.setPosition(cannon.posX, cannon.posY, cannon.posZ);
				if (player != null) {
					player.setPosition(cannon.posX, cannon.posY + 1.0, cannon.posZ);
				}
			}
		}

		if (!worldObj.isRemote) {
			if (worldObj.getWorldTime() % 20 == 0) {
				if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
					for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
						IInventory inventory = InventoryUtils.getInventory(worldObj, xCoord, yCoord, zCoord, direction);
						if (inventory != null) {
							ItemStack stack = InventoryUtils.removeNextItemStack(inventory);
							if (stack != null) {
								getMotionFromAngles();
								EntityItem item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, stack);
								item.delayBeforeCanPickup = 20;
								item.motionX = motionX * 1.4;
								item.motionY = motionY * 1.4;
								item.motionZ = motionZ * 1.4;
								worldObj.spawnEntityInWorld(item);
								worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "openblocks:mortar", 0.2f, 1.0f);
								break;
							}
						}
					}

				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = super.getRenderBoundingBox();
		return box.expand(32.0, 32.0, 32.0);
	}

	@Override
	public void onBlockBroken() {}

	@Override
	public void onBlockAdded() {}

	private Vec3 getPositionDistanceAway(double distance, double pitch, double yaw) {
		double p = Math.toRadians(pitch);
		double y = Math.toRadians(yaw);
		double k = -0.7;
		double xzLength = Math.cos(p) * k;
		double dx = xzLength * Math.cos(y);
		double dz = xzLength * Math.sin(y);
		double dy = k * Math.sin(p);
		return worldObj.getWorldVec3Pool().getVecFromPool(dx, dy, dz);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (!worldObj.isRemote && !player.isSneaking()) {
			cannon = new EntityMount(worldObj, xCoord, yCoord, zCoord);
			worldObj.spawnEntityInWorld(cannon);
			player.rotationPitch = player.prevRotationPitch = (float)pitch.getValue();
			player.renderYawOffset = player.prevRotationYawHead = player.rotationYawHead = player.prevRotationYaw = player.rotationYaw = (float)yaw.getValue();
			player.mountEntity(cannon);
			cannonId.setValue(cannon.entityId);
			ridingEntity.setValue(player.entityId);
			sync();
		}
		return true;
	}

	@Override
	public void onNeighbourChanged(int blockId) {}

	@Override
	public void onBlockPlacedBy(EntityPlayer player, ForgeDirection side, ItemStack stack, float hitX, float hitY, float hitZ) {}

	@Override
	public boolean onBlockEventReceived(int eventId, int eventParam) {
		return false;
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		getMotionFromAngles();
		int cId = cannonId.getValue();
		cannon = null;
		if (cId > 0) {
			Entity tmpCannon = worldObj.getEntityByID(cannonId.getValue());
			if (tmpCannon != null && tmpCannon instanceof EntityMount
					&& !tmpCannon.isDead) {
				cannon = (EntityMount)tmpCannon;
			}
		}
		int playerId = ridingEntity.getValue();
		if (playerId > 0) {
			Entity player = worldObj.getEntityByID(ridingEntity.getValue());
			if (player != null && player instanceof EntityMount
					&& !player.isDead) {
				if (cannon != null) {
					player.ridingEntity = cannon;
					cannon.riddenByEntity = player;
				}
			}
		}

	}

	private void getMotionFromAngles() {
		double p = Math.toRadians(pitch.getValue() - 180);
		double y = Math.toRadians(yaw.getValue());
		motionX = Math.sin(y) * Math.cos(p);
		motionY = Math.sin(p);
		motionZ = -Math.cos(y) * Math.cos(p);
	}

	public void disableLineRender() {
		renderLine = false;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		pitch.writeToNBT(tag, "pitch");
		yaw.writeToNBT(tag, "yaw");
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		pitch.readFromNBT(tag, "pitch");
		yaw.readFromNBT(tag, "yaw");
	}

}
