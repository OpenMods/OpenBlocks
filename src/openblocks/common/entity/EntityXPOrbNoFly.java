package openblocks.common.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrbNoFly extends EntityXPOrb {

	public EntityXPOrbNoFly(World world) {
		super(world);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	public EntityXPOrbNoFly(World world, double x, double y, double z, int xp) {
		super(world, x, y, z, xp);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	public void onUpdate()
	{
		super.onEntityUpdate();

		if (this.field_70532_c > 0) {
			--this.field_70532_c;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.029999999329447746D;

		if (this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) == Material.lava) {
			this.motionY = 0.20000000298023224D;
			this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
		}

		this.pushOutOfBlocks(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
		double d0 = 8.0D;

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		float f = 0.98F;

		if (this.onGround) {
			f = 0.58800006F;
			int i = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
			if (i > 0) {
				f = Block.blocksList[i].slipperiness * 0.98F;
			}
		}

		this.motionX *= (double)f;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= (double)f;

		if (this.onGround) {
			this.motionY *= -0.8999999761581421D;
		}

		++this.xpColor;
		++this.xpOrbAge;

		if (this.xpOrbAge >= 6000) {
			this.setDead();
		}
	}

}
