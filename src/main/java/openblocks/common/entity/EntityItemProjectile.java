package openblocks.common.entity;

import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemExpireEvent;

/**
 * Ugly EntityItem holder thingy with no air resistance. Because physics is hard
 * enough as it is
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
		ItemStack stack = getDataWatcher().getWatchableObjectItemStack(10);
		if (stack != null && stack.getItem() != null) {
			if (stack.getItem().onEntityItemUpdate(this)) { return; }
		}

		super.onEntityUpdate();

		if (delayBeforeCanPickup > 0) --delayBeforeCanPickup;

		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.03999999910593033D;
		noClip = func_145771_j(posX, (boundingBox.minY + boundingBox.maxY) / 2.0D, posZ);
		moveEntity(motionX, motionY, motionZ);
		boolean flag = (int)prevPosX != (int)posX
				|| (int)prevPosY != (int)posY
				|| (int)prevPosZ != (int)posZ;

		if (flag || ticksExisted % 25 == 0) {
			Block block = worldObj.getBlock(
					MathHelper.floor_double(posX),
					MathHelper.floor_double(posY),
					MathHelper.floor_double(posZ));

			if (block.getMaterial() == Material.lava) {
				motionY = 0.20000000298023224D;
				motionX = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
				motionZ = (rand.nextFloat() - rand.nextFloat()) * 0.2F;
				playSound("random.fizz", 0.4F, 2.0F + rand.nextFloat() * 0.4F);
			}

			if (!worldObj.isRemote) {
				searchForOtherItemsNearby();
			}
		}

		// Zero Air Friction
		float f = 1F;

		// Keep ground friction
		if (onGround) {
			f = 0.58800006F;
			Block block = worldObj.getBlock(
					MathHelper.floor_double(posX),
					MathHelper.floor_double(boundingBox.minY) - 1,
					MathHelper.floor_double(posZ));

			if (block != null) {
				f = block.slipperiness * 0.98F;
			}
		}

		motionX *= f;
		// Y would there be Y resistance :D
		// motionY *= 0.9800000190734863D;
		motionZ *= f;

		if (onGround) {
			motionY *= -0.5D;
		}

		++age;

		ItemStack item = getDataWatcher().getWatchableObjectItemStack(10);

		if (!worldObj.isRemote && age >= lifespan) {
			if (item != null) {
				ItemExpireEvent event = new ItemExpireEvent(this,
						(item.getItem() == null? 6000 : item.getItem()
								.getEntityLifespan(item, worldObj)));
				if (MinecraftForge.EVENT_BUS.post(event)) {
					lifespan += event.extraLife;
				} else {
					setDead();
				}
			} else {
				setDead();
			}
		}

		if (item != null && item.stackSize <= 0) {
			setDead();
		}
		if (!worldObj.isRemote && onGround && !isDead) {
			EntityItem standardEntity = new EntityItem(worldObj);
			NBTTagCompound transferTag = new NBTTagCompound();
			writeToNBT(transferTag);
			standardEntity.readFromNBT(transferTag);
			setDead();
			worldObj.spawnEntityInWorld(standardEntity);
		}
	}

	/**
	 * Looks for other itemstacks nearby and tries to stack them together
	 */
	private void searchForOtherItemsNearby() {
		Iterator<?> iterator = this.worldObj.getEntitiesWithinAABB(
				EntityItem.class, this.boundingBox.expand(0.5D, 0.0D, 0.5D))
				.iterator();

		while (iterator.hasNext()) {
			EntityItem entityitem = (EntityItem)iterator.next();
			combineItems(entityitem);
		}
	}

}
