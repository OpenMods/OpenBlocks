package openblocks.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import openmods.utils.ItemUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityGoldenEye extends EntitySmoothMove {

	private static final int TTL = 60;
	private int timeToLive;
	private ItemStack spawningStack;

	public EntityGoldenEye(World world, ItemStack spawningStack, Entity owner, ChunkPosition target) {
		super(world);
		timeToLive = TTL;
		this.spawningStack = spawningStack.copy();
		targetStructure(owner, target);
		setSize(0.02f, 0.02f);
	}

	public EntityGoldenEye(World world) {
		super(world);
		setSize(0.02f, 0.02f);
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
		if (tag.hasKey("SpawningItem")) {
			NBTTagCompound item = tag.getCompoundTag("SpawningItem");
			spawningStack = ItemUtils.readStack(item);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		if (spawningStack != null) {
			NBTTagCompound item = ItemUtils.writeStack(spawningStack);
			tag.setTag("SpawningItem", item);
		}
	}

	public void setTarget(double x, double y, double z) {
		smoother.setTarget(x, y, z);
	}

	@Override
	public void onUpdate() {
		if (!worldObj.isRemote && timeToLive-- < 0) {
			setDead();
			if (spawningStack != null) {
				EntityItem dropped = new EntityItem(worldObj, posX, posY, posZ, spawningStack);
				worldObj.spawnEntityInWorld(dropped);
			}
			return;
		}

		updatePrevPosition();
		smoother.update();

		if (worldObj.isRemote) {
			worldObj.spawnParticle("portal",
					posX + rand.nextGaussian() * 0.3 - 0.15,
					posY - 0.5 + rand.nextGaussian() * 0.3 - 0.15,
					posZ - rand.nextGaussian() * 0.3 - 0.15,
					motionX, motionY, motionZ);
		}
	}

	private void targetStructure(Entity owner, ChunkPosition target) {
		double playerY = owner.posY + 1.62 - owner.yOffset;
		double dx = target.chunkPosX - owner.posX;
		double dz = target.chunkPosZ - owner.posZ;
		double dist = Math.sqrt(dx * dx + dz * dz);
		dx /= dist;
		dz /= dist;

		double yaw = Math.toRadians(owner.rotationYaw);
		double px = -Math.sin(yaw);
		double pz = Math.cos(yaw);

		setPosition(owner.posX + 0.75 * px, playerY, owner.posZ + 0.75 * pz);

		double targetX = owner.posX;
		double targetY = playerY;
		double targetZ = owner.posZ;

		if (dist < 16) {
			targetY -= 4;
			rotationPitch = 90;
		} else {
			targetX += 6 * dx;
			targetZ += 6 * dz;
			rotationYaw = (float)Math.toDegrees(Math.atan2(dx, dz));
		}

		setTarget(targetX, targetY, targetZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}

}
