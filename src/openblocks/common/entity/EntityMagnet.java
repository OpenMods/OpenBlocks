package openblocks.common.entity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openblocks.common.CraneRegistry;
import openblocks.common.item.ItemCraneBackpack;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityMagnet extends Entity implements IEntityAdditionalSpawnData {

	private enum OwnerType {
		PLAYER {
			@Override
			public IOwner createProvider(World world) {
				return new EntityPlayerTarget(world);
			}
		};

		public abstract IOwner createProvider(World world);

		public static final OwnerType[] VALUES = values();
	}

	public interface IOwner extends IEntitySelector {
		public boolean isValid(EntityMagnet magnet);

		public Vec3 getTarget();

		public OwnerType getType();

		public void read(ByteArrayDataInput input);

		public void write(ByteArrayDataOutput output);

		public void update(EntityMagnet magnet);
	}

	public static class EntityPlayerTarget implements IOwner {

		private int entityId;
		private final WeakReference<World> world;
		private WeakReference<EntityPlayer> owner;
		private boolean isRegistered;

		private EntityPlayerTarget(World world, EntityPlayer owner) {
			this.entityId = -1;
			this.world = new WeakReference<World>(world);
			this.owner = new WeakReference<EntityPlayer>(owner);
		}

		public EntityPlayerTarget(EntityPlayer owner) {
			this(owner.worldObj, owner);
		}

		public EntityPlayerTarget(World world) {
			this(world, null);
		}

		private EntityPlayer getOwner() {
			EntityPlayer player = owner.get();

			if (player == null && entityId >= 0) {
				World w = world.get();
				if (w != null) {
					Entity tmp = w.getEntityByID(entityId);
					if (tmp instanceof EntityPlayer) {
						player = (EntityPlayer)tmp;
						owner = new WeakReference<EntityPlayer>(player);
					}
				}
			}

			return player;
		}

		@Override
		public boolean isValid(EntityMagnet magnet) {
			EntityPlayer player = getOwner();
			if (player == null || player.isDead) return false;
			if (magnet.worldObj != player.worldObj) return false;
			return ItemCraneBackpack.isWearingCrane(player);
		}

		@Override
		public Vec3 getTarget() {
			EntityPlayer player = getOwner();
			if (player == null) return null;

			double posX = player.posX + CraneRegistry.ARM_RADIUS * MathHelper.cos((player.rotationYaw + 90) * (float)Math.PI / 180);
			double posZ = player.posZ + CraneRegistry.ARM_RADIUS * MathHelper.sin((player.rotationYaw + 90) * (float)Math.PI / 180);

			double posY = player.posY + player.height - CraneRegistry.instance.getCraneMagnetDistance(player);

			return Vec3.createVectorHelper(posX, posY, posZ);
		}

		@Override
		public OwnerType getType() {
			return OwnerType.PLAYER;
		}

		@Override
		public void read(ByteArrayDataInput input) {
			entityId = input.readInt();
		}

		@Override
		public void write(ByteArrayDataOutput output) {
			EntityPlayer player = owner.get();
			output.writeInt(player != null? player.entityId : -1);
		}

		@Override
		public void update(EntityMagnet magnet) {
			if (!isRegistered) {
				EntityPlayer player = getOwner();
				if (player != null) {
					CraneRegistry.instance.magnetData.put(player, magnet);
				}

				isRegistered = true;
			}
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			EntityPlayer player = getOwner();
			if (player == null) return false;

			return (entity instanceof EntityItem) ||
					(entity instanceof EntityLivingBase && entity != player) ||
					(entity instanceof EntityBoat) ||
					(entity instanceof EntityMinecart);
		}
	}

	public class MoveSmoother {
		private final double damp;
		private final double cutoff;
		private final double panicLengthSq;
		private final double minimalLengthSq;

		private double targetX;
		private double targetY;
		private double targetZ;

		public MoveSmoother(double damp, double cutoff, double panicLength, double minimalLength) {
			this.damp = damp;
			this.cutoff = cutoff;
			this.panicLengthSq = panicLength * panicLength;
			this.minimalLengthSq = minimalLength * minimalLength;
		}

		public void setTarget(Vec3 position) {
			setTarget(position.xCoord, position.yCoord, position.zCoord);
		}

		public void setTarget(double targetX, double targetY, double targetZ) {
			this.targetX = targetX;
			this.targetY = targetY;
			this.targetZ = targetZ;
		}

		public void update() {
			double dx = targetX - posX;
			double dy = targetY - posY;
			double dz = targetZ - posZ;

			double lenSq = dx * dx + dy * dy + dz * dz;
			if (lenSq > panicLengthSq || lenSq < minimalLengthSq) {
				setPosition(targetX, targetY, targetZ);
				motionX = motionY = motionZ = 0;
			}
			else {
				if (lenSq > cutoff * cutoff) {
					double scale = cutoff / Math.sqrt(lenSq);
					dx *= scale;
					dy *= scale;
					dz *= scale;
				}
				moveEntity(motionX + dx * damp, motionY + dy * damp, motionZ + dz * damp);
			}
		}
	}

	private IOwner owner;
	private final MoveSmoother smoother;
	private boolean isAboveTarget;

	public EntityMagnet(World world) {
		super(world);
		setSize(0.5f, 0.5f);

		if (world.isRemote) smoother = new MoveSmoother(0.25, 1.0, 4.0, 0.01);
		else smoother = new MoveSmoother(0.1, 2.0, 10.0, 0.01);
	}

	public EntityMagnet(World world, IOwner owner) {
		this(world);
		this.owner = owner;
		Vec3 initialTarget = owner.getTarget();
		setPosition(initialTarget.xCoord, initialTarget.yCoord, initialTarget.zCoord);
	}

	@Override
	public boolean isEntityInvulnerable() {
		return true;
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		if (owner != null) {
			data.writeInt(owner.getType().ordinal());
			owner.write(data);
		} else data.writeInt(-1);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		int ownerTypeId = data.readInt();
		if (ownerTypeId >= 0) {
			OwnerType type = OwnerType.VALUES[ownerTypeId];
			owner = type.createProvider(worldObj);
			owner.read(data);
		}
	}

	@Override
	public void onUpdate() {
		if (owner == null || !owner.isValid(this)) {
			setDead();
			return;
		}

		owner.update(this);

		prevDistanceWalkedModified = distanceWalkedModified;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevRotationPitch = this.rotationPitch;
		prevRotationYaw = this.rotationYaw;

		if (!worldObj.isRemote) smoother.setTarget(owner.getTarget());

		smoother.update();

		isAboveTarget = !detectTargets().isEmpty();
	}

	@Override
	public void setPosition(double x, double y, double z) {
		if (smoother != null) smoother.setTarget(x, y, z);

		super.setPosition(x, y, z);
	}

	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
		smoother.setTarget(x, y, z);
		super.setPositionAndRotation(x, y, z, yaw, pitch);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int something) {
		smoother.setTarget(x, y, z);
		super.setRotation(yaw, pitch);
	}

	@Override
	public double getMountedYOffset() {
		if (riddenByEntity == null) return 0;

		return -Math.max(riddenByEntity.getMountedYOffset(), riddenByEntity.height);
	}

	public void toggleMagnet() {
		if (riddenByEntity != null) {
			final Entity tmp = riddenByEntity;
			// default unmount position is above entity and it
			// looks strange, so we hack around that
			double tmpPosY = tmp.posY;
			tmp.mountEntity(null);
			tmp.setPosition(tmp.posX, tmpPosY, tmp.posZ);

		} else {
			Entity target = null;

			List<Entity> result = detectTargets();

			Iterator<Entity> it = result.iterator();
			if (it.hasNext()) target = it.next();

			if (target != null) target.mountEntity(this);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Entity> detectTargets() {
		if (owner == null) return ImmutableList.of();

		AxisAlignedBB aabb = boundingBox.copy();
		aabb.minY -= 1;
		return worldObj.selectEntitiesWithinAABB(Entity.class, aabb, owner);
	}

	public boolean isAboveTarget() {
		return isAboveTarget;
	}

	public boolean isLocked() {
		return riddenByEntity != null;
	}

	public Vec3 getTarget() {
		return owner != null? owner.getTarget() : null;
	}
}
