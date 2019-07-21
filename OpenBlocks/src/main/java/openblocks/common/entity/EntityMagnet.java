package openblocks.common.entity;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import openblocks.Config;
import openblocks.api.IMagnetAware;
import openblocks.common.CraneRegistry;
import openblocks.common.MagnetWhitelists;
import openblocks.common.item.ItemCraneBackpack;
import openmods.entity.DelayedEntityLoadManager;
import openmods.entity.EntityBlock;
import openmods.entity.IEntityLoadListener;

public class EntityMagnet extends EntitySmoothMove implements IEntityAdditionalSpawnData {

	private static final float MAGNET_HEIGHT = 0.5f;
	private static final float MAGNET_WIDTH = 0.5f;
	private static final Random RANDOM = new Random();

	protected static class PickTargetPredicate implements Predicate<Entity> {
		@Override
		public boolean apply(@Nullable Entity entity) {
			return (entity instanceof LivingEntity) || MagnetWhitelists.instance.entityWhitelist.check(entity);
		}
	}

	public interface IEntityBlockFactory {
		EntityBlock create(LivingEntity player);
	}

	public interface IOwner {
		boolean isValid(EntityMagnet magnet);

		Vec3d getTarget();

		EntityBlock createByPlayer(IEntityBlockFactory factory);
	}

	private static class EntityPlayerTarget implements IOwner {
		private final WeakReference<LivingEntity> owner;

		public EntityPlayerTarget(LivingEntity owner) {
			this.owner = new WeakReference<>(owner);
		}

		@Override
		public boolean isValid(EntityMagnet magnet) {
			LivingEntity player = owner.get();
			if (player == null || player.isDead) return false;
			if (magnet.world != player.world) return false;
			return ItemCraneBackpack.isWearingCrane(player);
		}

		@Override
		public Vec3d getTarget() {
			LivingEntity player = owner.get();
			if (player == null) return null;

			double posX = player.posX + CraneRegistry.ARM_RADIUS * MathHelper.cos((player.rotationYaw + 90) * (float)Math.PI / 180);
			double posZ = player.posZ + CraneRegistry.ARM_RADIUS * MathHelper.sin((player.rotationYaw + 90) * (float)Math.PI / 180);

			double posY = player.posY + player.height
					- CraneRegistry.instance.getCraneMagnetDistance(player);

			return new Vec3d(posX, posY, posZ);
		}

		@Override
		public EntityBlock createByPlayer(IEntityBlockFactory factory) {
			LivingEntity player = owner.get();
			if (player == null) return null;

			return factory.create(player);
		}
	}

	public static class PlayerBound extends EntityMagnet implements IEntityLoadListener {
		private WeakReference<Entity> owner;

		public PlayerBound(World world) {
			super(world);
			owner = new WeakReference<>(null);
		}

		public PlayerBound(World world, LivingEntity owner) {
			super(world, new EntityPlayerTarget(owner), false);
			this.owner = new WeakReference<>(owner);
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
			if (entityId >= 0) DelayedEntityLoadManager.instance.registerLoadListener(world, this, entityId);
		}

		@Override
		public void onEntityLoaded(Entity entity) {
			if (entity instanceof PlayerEntity) {
				owner = new WeakReference<>(entity);
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
		Vec3d initialTarget = owner.getTarget();
		setPosition(initialTarget.x, initialTarget.y, initialTarget.z);
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
	protected void readEntityFromNBT(CompoundNBT tag) {}

	@Override
	protected void writeEntityToNBT(CompoundNBT tag) {}

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

		if (!world.isRemote) {
			if (owner == null || !owner.isValid(this)) {
				setDead();
				return;
			} else if (owner != null) {
				final Vec3d target = owner.getTarget().addVector(0, -height, 0);
				smoother.setTarget(target);
			}
		}

		updatePrevPosition();

		smoother.update();

		isAboveTarget = !detectEntityTargets().isEmpty();

		if (isMagic && world.isRemote && RANDOM.nextDouble() < 0.2) world.spawnParticle(EnumParticleTypes.PORTAL,
				posX + RANDOM.nextDouble() * 0.1,
				posY - RANDOM.nextDouble() * 0.2, posZ + RANDOM.nextDouble() * 0.1,
				RANDOM.nextGaussian(),
				-Math.abs(RANDOM.nextGaussian()),
				RANDOM.nextGaussian());
	}

	protected void fixSize() {
		final List<Entity> passengers = getPassengers();
		if (!passengers.isEmpty()) {
			Entity passenger = passengers.get(0);
			float width = Math.max(MAGNET_WIDTH, passenger.width);
			float height = MAGNET_HEIGHT + passenger.height;
			setSize(width, height);
		} else {
			setSize(MAGNET_WIDTH, MAGNET_HEIGHT);
		}
	}

	@Override
	public double getMountedYOffset() {
		final List<Entity> passengers = getPassengers();
		if (passengers.isEmpty()) return 0;

		return getMountedYOffset(passengers.get(0));
	}

	private static double getMountedYOffset(Entity rider) {
		return 0;
	}

	public boolean toggleMagnet() {
		final List<Entity> passengers = getPassengers();
		if (!passengers.isEmpty()) {
			final Entity passenger = passengers.get(0);

			if (passenger instanceof IMagnetAware && !((IMagnetAware)passenger).canRelease())
				return false;
			// default unmount position is above entity and it
			// looks strange, so we hack around that
			double tmpPosY = passenger.posY;
			removePassengers();
			passenger.setPosition(passenger.posX, tmpPosY, passenger.posZ);
			return true;
		} else if (!world.isRemote) {
			Entity target = null;

			if (Config.canMagnetPickEntities) target = findEntityToPick();

			if (target == null && Config.canMagnetPickBlocks) target = createBlockEntity();

			if (target != null) {
				target.startRiding(this);
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
		AxisAlignedBB aabb = getEntityBoundingBox().grow(0.25, 0, 0.25).offset(0, -1, 0);
		return world.getEntitiesInAABBexcluding(this, aabb, createPickTargetPredicate());
	}

	protected Predicate<Entity> createPickTargetPredicate() {
		return new PickTargetPredicate();
	}

	private Entity createBlockEntity() {
		final int x = MathHelper.floor(posX);
		final int y = MathHelper.floor(posY - 0.5);
		final int z = MathHelper.floor(posZ);

		final BlockPos pos = new BlockPos(x, y, z);

		if (!world.isBlockLoaded(pos) || world.isAirBlock(pos)) return null;

		Entity result = null;

		if (MagnetWhitelists.instance.testBlock(world, pos)) {
			result = owner.createByPlayer(player -> EntityBlock.create(player, world, pos, EntityMountedBlock::new));
		}

		if (result != null) {
			result.setPosition(posX, posY + getMountedYOffset(result), posZ);
			world.spawnEntity(result);
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
		return isBeingRidden();
	}

	public boolean isValid() {
		return owner != null && owner.isValid(this);
	}
}
