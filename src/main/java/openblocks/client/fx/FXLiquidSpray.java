package openblocks.client.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FXLiquidSpray extends EntityFX {

	public FXLiquidSpray(World world, FluidStack fluid, double x, double y, double z, float scale, float gravity, Vec3 velocity) {
		this(world, fluid.getFluid(), x, y, z, scale, gravity, velocity);
	}

	public FXLiquidSpray(World world, Fluid fluid, double x, double y, double z, float scale, float gravity, Vec3 velocity) {
		this(world, fluid.getStillIcon(), x, y, z, scale, gravity, velocity);
	}

	public FXLiquidSpray(World world, IIcon icon, double x, double y, double z, float scale, float gravity, Vec3 velocity) {
		super(world, x, y, z, 0, 0, 0);

		this.lastTickPosX = this.prevPosX = this.posX = x;
		this.lastTickPosY = this.prevPosY = this.posY = y;
		this.lastTickPosZ = this.prevPosZ = this.posZ = z;

		particleGravity = gravity;
		this.particleMaxAge = 50;
		setSize(0.2f, 0.2f);
		this.particleScale = scale;
		this.noClip = false;
		motionX = velocity.xCoord;
		motionY = velocity.yCoord;
		motionZ = velocity.zCoord;

		setParticleIcon(icon);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}
}
