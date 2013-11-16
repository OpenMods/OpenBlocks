package openblocks.common.entity;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

/**
 * Ugly EntityItem holder thingy with no air resistance. Because physics is hard enough as it is
 *
 */
public class EntityItemProjectile extends EntityItem {

	public EntityItemProjectile(World par1World, double par2, double par4,
			double par6) {
		super(par1World, par2, par4, par6);
	}

	public EntityItemProjectile(World par1World, double par2, double par4,
			double par6, ItemStack par8ItemStack) {
		super(par1World, par2, par4, par6, par8ItemStack);
	}

	public EntityItemProjectile(World par1World) {
		super(par1World);
	}

	@Override
	public void onUpdate() {
		ItemStack stack = this.getDataWatcher().getWatchableObjectItemStack(10);
		if (stack != null && stack.getItem() != null) {
			if (stack.getItem().onEntityItemUpdate(this)) {
				return;
			}
		}

		super.onUpdate();

		if (this.delayBeforeCanPickup > 0) {
			--this.delayBeforeCanPickup;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.03999999910593033D;
		this.noClip = this.pushOutOfBlocks(this.posX,
				(this.boundingBox.minY + this.boundingBox.maxY) / 2.0D,
				this.posZ);
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		boolean flag = (int) this.prevPosX != (int) this.posX
				|| (int) this.prevPosY != (int) this.posY
				|| (int) this.prevPosZ != (int) this.posZ;

		if (flag || this.ticksExisted % 25 == 0) {
			if (this.worldObj.getBlockMaterial(
					MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.posY),
					MathHelper.floor_double(this.posZ)) == Material.lava) {
				this.motionY = 0.20000000298023224D;
				this.motionX = (double) ((this.rand.nextFloat() - this.rand
						.nextFloat()) * 0.2F);
				this.motionZ = (double) ((this.rand.nextFloat() - this.rand
						.nextFloat()) * 0.2F);
				this.playSound("random.fizz", 0.4F,
						2.0F + this.rand.nextFloat() * 0.4F);
			}

			if (!this.worldObj.isRemote) {
				this.searchForOtherItemsNearby();
			}
		}

		// Zero Air Friction
		float f = 0F;

		
		// Keep ground friction
		if (this.onGround) {
			f = 0.58800006F;
			int i = this.worldObj.getBlockId(
					MathHelper.floor_double(this.posX),
					MathHelper.floor_double(this.boundingBox.minY) - 1,
					MathHelper.floor_double(this.posZ));

			if (i > 0) {
				f = Block.blocksList[i].slipperiness * 0.98F;
			}
		}

		this.motionX *= (double) f;
		// Y would there be Y resistance :D
		// this.motionY *= 0.9800000190734863D; 
		this.motionZ *= (double) f;

		if (this.onGround) {
			this.motionY *= -0.5D;
		}

		++this.age;

		ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

		if (!this.worldObj.isRemote && this.age >= lifespan) {
			if (item != null) {
				ItemExpireEvent event = new ItemExpireEvent(this,
						(item.getItem() == null ? 6000 : item.getItem()
								.getEntityLifespan(item, worldObj)));
				if (MinecraftForge.EVENT_BUS.post(event)) {
					lifespan += event.extraLife;
				} else {
					this.setDead();
				}
			} else {
				this.setDead();
			}
		}

		if (item != null && item.stackSize <= 0) {
			this.setDead();
		}
	}

	/**
	 * Looks for other itemstacks nearby and tries to stack them together
	 */
	private void searchForOtherItemsNearby() {
		Iterator iterator = this.worldObj.getEntitiesWithinAABB(
				EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D))
				.iterator();

		while (iterator.hasNext()) {
			EntityItem entityitem = (EntityItem) iterator.next();
			this.combineItems(entityitem);
		}
	}

}
