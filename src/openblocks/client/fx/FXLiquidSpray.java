package openblocks.client.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FXLiquidSpray extends EntityFX {

	public FXLiquidSpray(World par1World, FluidStack liquid, double x, double y, double z, ForgeDirection sprayDirection, float angle, float spread) {
		super(par1World, x, y, z, 0, 0, 0);

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
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

}
