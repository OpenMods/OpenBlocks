package openblocks.common.entity;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import openblocks.common.CraneRegistry;
import openblocks.common.MagnetWhitelists;
import openblocks.common.item.ItemCraneBackpack;
import openblocks.integration.MagnetControlPeripheral;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityMagnet extends EntitySmoothMove implements IEntityAdditionalSpawnData {

	private static final Random RANDOM = new Random();

	public enum OwnerType {
		PLAYER {
			@Override
			public IOwner createProvider(World world) {
				return new EntityPlayerTarget(world);
			}
		},
		TURTLE {
			@Override
			public IOwner createProvider(World world) {
				Preconditions.checkState(Loader.isModLoaded(openblocks.Mods.COMPUTERCRAFT), "ComputerCraft not present!");
				return new MagnetControlPeripheral.Owner(world);
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

			double posX = player.posX
					+ CraneRegistry.ARM_RADIUS
					* MathHelper.cos((player.rotationYaw + 90) * (float)Math.PI
							/ 180);
			double posZ = player.posZ
					+ CraneRegistry.ARM_RADIUS
					* MathHelper.sin((player.rotationYaw + 90) * (float)Math.PI
							/ 180);

			double posY = player.posY + player.height
					- CraneRegistry.instance.getCraneMagnetDistance(player);

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

			return (entity instanceof EntityLivingBase && entity != player)
					|| MagnetWhitelists.instance.entityWhitelist.check(entity);
		}
	}

	private IOwner owner;
	private boolean isAboveTarget;
	private boolean isMagic;

	public EntityMagnet(World world) {
		super(world);
		setSize(0.5f, 0.5f);
	}

	public EntityMagnet(World world, IOwner owner, boolean isMagic) {
		this(world);
		this.owner = owner;
		this.isMagic = isMagic;
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
		data.writeBoolean(isMagic);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		int ownerTypeId = data.readInt();
		if (ownerTypeId >= 0) {
			OwnerType type = OwnerType.VALUES[ownerTypeId];
			owner = type.createProvider(worldObj);
			owner.read(data);
		}
		isMagic = data.readBoolean();
	}

	@Override
	public void onUpdate() {
		if (owner == null || !owner.isValid(this)) {
			setDead();
			return;
		}

		owner.update(this);

		if (!worldObj.isRemote) smoother.setTarget(owner.getTarget());

		updatePrevPosition();

		smoother.update();

		isAboveTarget = !detectEntityTargets().isEmpty();

		if (isMagic && worldObj.isRemote && RANDOM.nextDouble() < 0.2) worldObj.spawnParticle("portal", posX
				+ RANDOM.nextDouble() * 0.1, posY - RANDOM.nextDouble() * 0.2, posZ
				+ RANDOM.nextDouble() * 0.1, RANDOM.nextGaussian(), -Math.abs(RANDOM.nextGaussian()), RANDOM.nextGaussian());
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

		return getMountedYOffset(riddenByEntity);
	}

	private static double getMountedYOffset(Entity rider) {
		if (rider instanceof EntityItem) // yeah, hack
		return -0.5;
		double tmp = -Math.max(rider.getMountedYOffset(), rider.height);
		return tmp;
	}

	public boolean toggleMagnet() {
		if (riddenByEntity != null) {
			final Entity tmp = riddenByEntity;

			if (tmp instanceof IMagnetAware
					&& !((IMagnetAware)tmp).canRelease()) return false;
			// default unmount position is above entity and it
			// looks strange, so we hack around that
			double tmpPosY = tmp.posY;
			tmp.mountEntity(null);
			tmp.setPosition(tmp.posX, tmpPosY, tmp.posZ);
			return true;
		} else if (!worldObj.isRemote) {
			Entity target = null;

			List<Entity> result = detectEntityTargets();

			Iterator<Entity> it = result.iterator();
			if (it.hasNext()) target = it.next();
			else target = getBlockEntity();

			if (target != null) {
				target.mountEntity(this);
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Entity> detectEntityTargets() {
		if (owner == null) return ImmutableList.of();

		AxisAlignedBB aabb = boundingBox.expand(0.25, 0, 0.25).copy();
		aabb.minY -= 1;
		return worldObj.getEntitiesWithinAABBExcludingEntity(this, aabb, owner);
	}

	private Entity getBlockEntity() {
		int x = MathHelper.floor_double(posX);
		int y = MathHelper.floor_double(posY - 0.5);
		int z = MathHelper.floor_double(posZ);

		if (!worldObj.blockExists(x, y, z) || worldObj.isAirBlock(x, y, z)) return null;

		Entity result = null;

		if (MagnetWhitelists.instance.testBlock(worldObj, x, y, z)) result = EntityBlock.create(worldObj, x, y, z, EntityMountedBlock.class);

		if (result != null) {
			result.setPosition(posX, posY + getMountedYOffset(result), posZ);
			worldObj.spawnEntityInWorld(result);
		}

		return result;
	}

	public boolean isAboveTarget() {
		return isAboveTarget;
	}

	public boolean isLocked() {
		return riddenByEntity != null;
	}

	public boolean isValid() {
		return owner != null? owner.isValid(this) : false;
	}
}
