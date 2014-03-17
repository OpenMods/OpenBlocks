package openblocks.client.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FXLiquidSpray extends EntityFX {

	public FXLiquidSpray(World par1World, FluidStack liquid, double x, double y, double z, float scale, float gravity, Vec3 vec) {
		super(par1World, x, y, z, 0, 0, 0);

		this.lastTickPosX = this.prevPosX = this.posX = x;
		this.lastTickPosY = this.prevPosY = this.posY = y;
		this.lastTickPosZ = this.prevPosZ = this.posZ = z;

		particleGravity = gravity;
		this.particleMaxAge = 50;
		setSize(0.2f, 0.2f);
		this.particleScale = scale;
		this.noClip = false;
		motionX = vec.xCoord;
		motionY = vec.yCoord;
		motionZ = vec.zCoord;

		Fluid fluid = liquid.getFluid();
		setParticleIcon(fluid.getStillIcon());
	}

	@Override
	public int getFXLayer() {
		return 1;
	}
}
