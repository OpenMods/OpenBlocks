package openblocks.client.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FXLiquidSpray extends EntityFX {

	public FXLiquidSpray(World par1World, FluidStack liquid, double x, double y, double z, ForgeDirection sprayDirection, float angle, float spread) {
		super(par1World, x, y, z, 0, 0, 0);

		// vec.xCoord = Math.abs(vec.xCoord);
		// vec.yCoord = Math.abs(vec.yCoord);
		// vec.zCoord = Math.abs(vec.zCoord);

		double sinPitch = Math.sin(angle);
		double cosPitch = Math.cos(angle);

		double vecX = 0, vecY = 0, vecZ = 0;

		if (sprayDirection.offsetZ == 0) {
			vecY = Math.abs(cosPitch);
			vecZ = sinPitch * sprayDirection.offsetX;
		} else {
			vecY = Math.abs(cosPitch);
			vecX = -sinPitch * sprayDirection.offsetZ;
		}

		this.posX = x;
		this.posY = y;
		this.posZ = z;

		particleGravity = 0.7f;
		this.particleMaxAge = 50;
		setSize(0.5F, 0.5F);
		this.particleScale = 0.3f;
		this.noClip = false;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (sprayDirection.offsetX == 0) {
			vecZ = (rand.nextDouble() - 0.5) * spread;
		} else {
			vecX = (rand.nextDouble() - 0.5) * spread;
		}
		motionX = vecX / 2;
		motionY = vecY / 2;
		motionZ = vecZ / 2;

		Fluid fluid = liquid.getFluid();
		func_110125_a(fluid.getStillIcon());
		

		// this.setParticleTextureIndex(0 + this.rand.nextInt(7));
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7) {
		// float f6 = (this.particleAge + par2) / this.particleMaxAge;
		// this.particleScale = this.flameScale * (1.0F - f6 * f6 * 0.5F);
		super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onUpdate();
	}

}
