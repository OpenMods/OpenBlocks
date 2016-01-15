package openblocks.common.entity;

import io.netty.buffer.ByteBuf;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.Config;
import openblocks.api.IMagnetAware;
import openblocks.common.CraneRegistry;
import openblocks.common.MagnetWhitelists;
import openblocks.common.item.ItemCraneBackpack;
import openmods.entity.*;
import openmods.entity.EntityBlock.EntityFactory;

import com.google.common.base.Predicate;

public class EntityMagnet extends EntitySmoothMove implements IEntityAdditionalSpawnData {

	private static final float MAGNET_HEIGHT = 0.5f;
	private static final float MAGNET_WIDTH = 0.5f;
	private static final Random RANDOM = new Random();

	protected static class PickTargetPredicate implements Predicate<Entity> {
		@Override
		public boolean apply(@Nullable Entity entity) {
			return (entity instanceof EntityLivingBase) || MagnetWhitelists.instance.entityWhitelist.check(entity);
		}
	}

	public interface IEntityBlockFactory {
		public EntityBlock create(EntityPlayer player);
	}

	public interface IOwner {
		public boolean isValid(EntityMagnet magnet);

		public Vec3 getTarget();

		public EntityBlock createByPlayer(IEntityBlockFactory factory);
	}

	private static class EntityPlayerTarget implements IOwner {
		private WeakReference<EntityPlayer> owner;

		public EntityPlayerTarget(EntityPlayer owner) {
			this.owner = new WeakReference<EntityPlayer>(owner);
		}

		@Override
		public boolean isValid(EntityMagnet magnet) {
			EntityPlayer player = owner.get();
			if (player == null || player.isDead) return false;
			if (magnet.worldObj != player.worldObj) return false;
			return ItemCraneBackpack.isWearingCrane(player);
		}

		@Override
		public Vec3 getTarget() {
			EntityPlayer player = owner.get();
			if (player == null) return null;

			double posX = player.posX + CraneRegistry.ARM_RADIUS
					* MathHelper.cos((player.rotationYaw + 90) * (float)Math.PI / 180);
			double posZ = player.posZ
					+ CraneRegistry.ARM_RADIUS
					* MathHelper.sin((player.rotationYaw + 90) * (float)Math.PI / 180);

			double posY = player.posY + player.height
					- CraneRegistry.instance.getCraneMagnetDistance(player);

			return new Vec3(posX, posY, posZ);
		}

		@Override
		public EntityBlock createByPlayer(IEntityBlockFactory factory) {
			EntityPlayer player = owner.get();
			if (player == null) return null;

			return factory.create(player);
		}
	}

	public static class PlayerBound extends EntityMagnet implements IEntityLoadListener {
		private WeakReference<Entity> owner;

		public PlayerBound(World world) {
			super(world);
			owner = new WeakReference<Entity>(null);
		}

		public PlayerBound(World world, EntityPlayer owner) {
			super(world, new EntityPlayerTarget(owner), false);
			this.owner = new WeakReference<Entity>(owner);
			CraneRegistry.instance.bindMagnetToPlayer(owner, this);
		}

		@Override
		public void writeSpawnData(ByteBuf data) {
			super.writeSpawnData(data);
			Entity owner = this.owner.get();
			data.writeInt(owner != null? owner.getEntityId() : -1);
		}

		@Override
		public void readSpawnData(ByteBuf data) {
			super.readSpawnData(data);
			int entityId = data.readInt();
			if (entityId >= 0) DelayedEntityLoadManager.instance.registerLoadListener(worldObj, this, entityId);
		}

		@Override
		public void onEntityLoaded(Entity entity) {
			if (entity instanceof EntityPlayer) {
				owner = new WeakReference<Entity>(entity);
				CraneRegistry.instance.bindMagnetToPlayer(entity, this);
			}
		}

		@Override
		protected Predicate<Entity> createPickTargetPredicate() {
			return new PickTargetPredicate() {
				@Override
				public boolean apply(@Nullable Entity entity) {
					return entity != owner.get() && super.apply(entity);

				}
			};
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
	public boolean isEntityInvulnerable(DamageSource source) {
		return true;
	}

	@Override
	protected void dealFireDamage(int i) {}

	@Override
	public boolean canRenderOnFire() {
		return false;
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeBoolean(isMagic);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		isMagic = data.readBoolean();
	}

	@Override
	public void onUpdate() {
		fixSize();

		if (!worldObj.isRemote) {
			if (owner == null || !owner.isValid(this)) {
				setDead();
				return;
			} else if (owner != null) {
				final Vec3 target = owner.getTarget().addVector(0, -height, 0);
				smoother.setTarget(target);
			}
		}

		updatePrevPosition();

		smoother.update();

		isAboveTarget = !detectEntityTargets().isEmpty();

		if (isMagic && worldObj.isRemote && RANDOM.nextDouble() < 0.2) worldObj.spawnParticle(EnumParticleTypes.PORTAL, posX
				+ RANDOM.nextDouble() * 0.1, posY - RANDOM.nextDouble() * 0.2, posZ
				+ RANDOM.nextDouble() * 0.1, RANDOM.nextGaussian(), -Math.abs(RANDOM.nextGaussian()), RANDOM.nextGaussian());
	}

	protected void fixSize() {
		if (riddenByEntity != null) {
			float width = Math.max(MAGNET_WIDTH, riddenByEntity.width);
			float height = MAGNET_HEIGHT + riddenByEntity.height;
			setSize(width, height);
		} else {
			setSize(MAGNET_WIDTH, MAGNET_HEIGHT);
		}
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
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean something) {
		smoother.setTarget(x, y, z);
		super.setRotation(yaw, pitch);
	}

	@Override
	public double getMountedYOffset() {
		if (riddenByEntity == null) return 0;

		return getMountedYOffset(riddenByEntity);
	}

	private static double getMountedYOffset(Entity rider) {
		if (rider instanceof EntityPlayer) return 0.5f;
		return 0;
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

			if (Config.canMagnetPickEntities) target = findEntityToPick();

			if (target == null && Config.canMagnetPickBlocks) target = createBlockEntity();

			if (target != null) {
				target.mountEntity(this);
				return true;
			}
		}

		return false;
	}

	private Entity findEntityToPick() {
		List<Entity> result = detectEntityTargets();
		Iterator<Entity> it = result.iterator();
		return it.hasNext()? it.next() : null;
	}

	protected List<Entity> detectEntityTargets() {
		// TODO 1.8.9 verify addCoord usage
		AxisAlignedBB aabb = getEntityBoundingBox().expand(0.25, 0, 0.25).addCoord(0, -1, 0);
		return worldObj.getEntitiesInAABBexcluding(this, aabb, createPickTargetPredicate());
	}

	protected Predicate<Entity> createPickTargetPredicate() {
		return new PickTargetPredicate();
	}

	private Entity createBlockEntity() {
		final int x = MathHelper.floor_double(posX);
		final int y = MathHelper.floor_double(posY - 0.5);
		final int z = MathHelper.floor_double(posZ);

		final BlockPos pos = new BlockPos(x, y, z);

		if (!worldObj.isBlockLoaded(pos) || worldObj.isAirBlock(pos)) return null;

		Entity result = null;

		if (MagnetWhitelists.instance.testBlock(worldObj, pos)) {
			result = owner.createByPlayer(new IEntityBlockFactory() {
				@Override
				public EntityBlock create(EntityPlayer player) {
					return EntityBlock.create(player, worldObj, pos, new EntityFactory() {
						@Override
						public EntityBlock create(World world) {
							return new EntityMountedBlock(world);
						}
					});
				}
			});
		}

		if (result != null) {
			result.setPosition(posX, posY + getMountedYOffset(result), posZ);
			worldObj.spawnEntityInWorld(result);
		}

		return result;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public boolean shouldDismountInWater(Entity rider) {
		return false;
	}

	@Override
	public boolean canRiderInteract() {
		return false;
	}

	public boolean isAboveTarget() {
		return isAboveTarget && Config.canMagnetPickEntities;
	}

	public boolean isLocked() {
		return riddenByEntity != null;
	}

	public boolean isValid() {
		return owner != null? owner.isValid(this) : false;
	}
}
