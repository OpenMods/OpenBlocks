package openblocks.common.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Ugly EntityItem holder thingy with no air resistance. Because physics is hard
 * enough as it is
 *
 */
public class EntityItemProjectile extends EntityItem {

	public EntityItemProjectile(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	public EntityItemProjectile(World world, double x, double y, double z, ItemStack stack) {
		super(world, x, y, z, stack);
	}

	public EntityItemProjectile(World world) {
		super(world);
	}

	@Override
	public void onUpdate() {
		final ItemStack stack = getDataWatcher().getWatchableObjectItemStack(10);
		if (stack == null)
		{
			setDead();
			return;
		}
		if (stack.getItem() != null && stack.getItem().onEntityItemUpdate(this)) return;

		// TODO 1.8.9 verify
		final double x = posX;
		final double y = posY;
		final double z = posZ;

		final double vx = motionX;
		final double vy = motionY;
		final double vz = motionZ;

		// let vanilla run
		super.onUpdate();
		if (!isDead) return;
		// and then overwrite

		this.posX = x;
		this.posY = y;
		this.posZ = z;

		this.motionX = vx;
		this.motionY = vy;
		this.motionZ = vz;

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.03999999910593033D;

		moveEntity(this.motionX, this.motionY, this.motionZ);

		boolean hasMoved = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;

		if (hasMoved || this.ticksExisted % 25 == 0)
		{
			if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.lava)
			{
				this.motionY = 0.20000000298023224D;
				this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
				this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
				playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
			}
		}

		// Zero Air Friction
		float f = 1F;

		// Keep ground friction
		if (this.onGround)
		{
			f = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.98F;
		}

		this.motionX *= f;
		// Y would there be Y resistance :D
		// ^ not my pun, I'm just porting :P, ~B
		// motionY *= 0.98;
		this.motionZ *= f;

		if (this.onGround) this.motionY *= -0.5D;

		handleWaterMovement();
	}

}
