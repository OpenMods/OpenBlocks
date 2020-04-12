package openblocks.common.entity;

import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityGoldenEye extends EntitySmoothMove {

	private static final String TAG_SPAWNING_ITEM = "SpawningItem";
	private static final int TTL = 60;
	private int timeToLive;
	@Nonnull
	private ItemStack spawningStack = ItemStack.EMPTY;

	public EntityGoldenEye(World world, @Nonnull ItemStack spawningStack, Entity owner, BlockPos target) {
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

	public static void registerFixes(DataFixer fixer) {
		fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(EntityGoldenEye.class, TAG_SPAWNING_ITEM));
	}

	@Override
	protected void entityInit() {}

	@Override
	protected void readEntityFromNBT(CompoundNBT tag) {
		if (tag.hasKey(TAG_SPAWNING_ITEM)) {
			CompoundNBT item = tag.getCompoundTag(TAG_SPAWNING_ITEM);
			spawningStack = new ItemStack(item);
		}
	}

	@Override
	protected void writeEntityToNBT(CompoundNBT tag) {
		if (!spawningStack.isEmpty()) {
			CompoundNBT item = spawningStack.writeToNBT(new CompoundNBT());
			tag.setTag(TAG_SPAWNING_ITEM, item);
		}
	}

	@Override
	public void onUpdate() {
		if (!world.isRemote && timeToLive-- < 0) {
			setDead();
			if (!spawningStack.isEmpty()) {
				world.spawnEntity(new ItemEntity(world, posX, posY, posZ, spawningStack));
			}
			return;
		}

		updatePrevPosition();
		smoother.update();

		if (world.isRemote) {
			world.spawnParticle(EnumParticleTypes.PORTAL,
					posX + rand.nextGaussian() * 0.3 - 0.15,
					posY - 0.5 + rand.nextGaussian() * 0.3 - 0.15,
					posZ - rand.nextGaussian() * 0.3 - 0.15,
					motionX, motionY, motionZ);
		}
	}

	private void targetStructure(Entity owner, BlockPos target) {
		double playerY = owner.posY + owner.getEyeHeight();
		double dx = target.getX() - owner.posX;
		double dz = target.getZ() - owner.posZ;
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

		setPosition(targetX, targetY, targetZ);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRender3d(double x, double y, double z) {
		return true;
	}

}
