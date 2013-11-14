package openblocks.common.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.api.IPointable;
import openblocks.common.api.IActivateAwareTile;
import openblocks.common.entity.EntityMount;
import openblocks.sync.ISyncableObject;
import openblocks.sync.SyncableDouble;
import openblocks.sync.SyncableInt;
import openblocks.utils.InventoryUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCannon extends SyncedTileEntity implements IActivateAwareTile, IPointable {

	private EntityMount cannon = null;

	public SyncableDouble pitch;
	public SyncableDouble yaw;
	public SyncableInt cannonId;
	public SyncableInt ridingEntity;

	public double motionX = 0;
	public double motionY = 0;
	public double motionZ = 0;

	public boolean renderLine = true;

	public TileEntityCannon() {
	}

	@Override
	protected void createSyncedFields() {
		pitch = new SyncableDouble();
		yaw = new SyncableDouble();
		cannonId = new SyncableInt(0);
		ridingEntity = new SyncableInt(0);
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
		if (cannon == null) {
			ridingEntity.setValue(0);
		}
		if (cannon != null && cannon.riddenByEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) cannon.riddenByEntity;
			double p = player.rotationPitch;
			double y = player.rotationYawHead;
			pitch.setValue(p);
			yaw.setValue(y);
			sync();
		}

		if (cannon != null && cannon.riddenByEntity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) cannon.riddenByEntity;
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
			seek();

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

	private Vec3 getPositionDistanceAway(double distance, double pitch, double yaw) {
		double p = Math.toRadians(pitch);
		double y = Math.toRadians(yaw);
		double k = distance;
		double xzLength = Math.cos(p) * k;
		double dx = xzLength * Math.cos(y);
		double dz = xzLength * Math.sin(y);
		double dy = k * Math.sin(p);
		return worldObj.getWorldVec3Pool().getVecFromPool(dx, dy, dz);
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		return false;
/*		if (!worldObj.isRemote) {
			setTarget(1504, 5, -1013);
		}
		return true;*/
	}

	@Override
	public void onSynced(List<ISyncableObject> changes) {
		getMotionFromAngles();
		int cId = cannonId.getValue();
		cannon = null;
		if (cId > 0) {
			Entity tmpCannon = worldObj.getEntityByID(cannonId.getValue());
			if (tmpCannon != null && tmpCannon instanceof EntityMount && !tmpCannon.isDead) {
				cannon = (EntityMount) tmpCannon;
			}
		}
		int playerId = ridingEntity.getValue();
		if (playerId > 0) {
			Entity player = worldObj.getEntityByID(ridingEntity.getValue());
			if (player != null && player instanceof EntityMount && !player.isDead) {
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

	public double targetX = -1;
	public double targetY = -1;
	public double targetZ = -1;
	public boolean seeking = false;

	public void setTarget(int x, int y, int z) {
		this.targetX = x + 0.5;
		this.targetY = y;
		this.targetZ = z + 0.5;
		this.seeking = true;
	}

	public double getTargetError(double d, double e) {
		boolean rising = false;
		double x = xCoord + 0.5F;
		double y = yCoord + 0.5F;
		double z = zCoord + 0.5F;

		double pc = Math.toRadians(d - 180);
		double yw = Math.toRadians(e);

		double mX = Math.sin(yw) * Math.cos(pc) * 1.4;
		double mY = Math.sin(pc) * 1.4;
		double mZ = -Math.cos(yw) * Math.cos(pc) * 1.4;

		// continue until the trajectory is just about to become falling below
		// the target y-level

		for (int i = 0; i < 200 && !(mY < 0.03999999910593033D && y < targetY - mY + 0.03999999910593033D); i++) {
			mY -= 0.03999999910593033D;
			x += mX;
			y += mY;
			z += mZ;
			mX *= 0.98;
			mY *= 0.9800000190734863D;
			mZ *= 0.98;
		}

		// too low to reach the target
		if (y < targetY)
			return Math.sin(pc);
		// return ((x - targetX) * (x - targetX) + (y - targetY) * (y - targetY)
		// + (z - targetZ) * (z - targetZ));

		mY -= 0.03999999910593033D;

		double dt = (y - targetY) / mY; // calculate the micro-step time needed
										// to reach the target

		x += dt * mX;
		// y += dt * mY; (== targetY) by definition
		z += dt * mZ;

		return Math.sqrt((x - targetX) * (x - targetX) + (z - targetZ) * (z - targetZ));
	}

	double tol = 0.1;

	double maxSpeed = 3;
	double pitchSpeed = 0;
	double yawSpeed = 0;
	double alpha = 0.3;

	public void seek() {
		if (seeking && targetY > 0) {
			double d;
			if ((d = getTargetError(pitch.getValue(), yaw.getValue())) < 0.25) {
				seeking = false;
				return;
			}

			double h = 0.01 * pitchSpeed;
			if (h == 0)
				h = 0.01;

			if (Math.sin(Math.toRadians(pitch.getValue() - 180)) < 0)
				pitch.setValue(270);

			double dp = (getTargetError(pitch.getValue() + h, yaw.getValue()) - getTargetError(pitch.getValue() - h, yaw.getValue())) / (2 * h);

			double dy = 10 * Math.sin(Math.atan2(targetX - xCoord - 0.5 + 0.0001, -targetZ + zCoord + 0.5 - 0.0001) - Math.toRadians(yaw.getValue()));

			pitchSpeed = -dp * alpha;
			yawSpeed = -dy * alpha;

			if (pitchSpeed > maxSpeed)
				pitchSpeed = maxSpeed;
			else if (pitchSpeed < -maxSpeed)
				pitchSpeed = -maxSpeed;

			if (yawSpeed > maxSpeed)
				yawSpeed = maxSpeed;
			else if (yawSpeed < -maxSpeed)
				yawSpeed = -maxSpeed;

			pitch.setValue(pitch.getValue() + pitchSpeed);
			yaw.setValue(yaw.getValue() + yawSpeed);
			sync();
		}
	}

	public void disableLineRender() {
		renderLine = false;
	}

	@Override
	public void onPoint(ItemStack itemStack, EntityPlayer player, int x, int y, int z) {
		player.sendChatToPlayer(ChatMessageComponent.createFromText(String.format("Pointed cannon at %s, %s, %s", x, y, z)));
		setTarget(x, y, z);
	}

}
