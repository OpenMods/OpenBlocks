package openblocks.client.fx;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import openmods.utils.TextureUtils;

public class FXLiquidSpray extends Particle {

	public FXLiquidSpray(World world, FluidStack fluid, double x, double y, double z, float scale, float gravity, Vec3d velocity) {
		this(world, TextureUtils.getFluidTexture(fluid), x, y, z, scale, gravity, velocity);
	}

	public FXLiquidSpray(World world, Fluid fluid, double x, double y, double z, float scale, float gravity, Vec3d velocity) {
		this(world, TextureUtils.getFluidTexture(fluid), x, y, z, scale, gravity, velocity);
	}

	public FXLiquidSpray(World world, TextureAtlasSprite icon, double x, double y, double z, float scale, float gravity, Vec3d velocity) {
		super(world, x, y, z, velocity.xCoord, velocity.yCoord, velocity.zCoord);

		particleGravity = gravity;
		this.particleMaxAge = 50;
		setSize(0.2f, 0.2f);
		this.particleScale = scale;
		this.canCollide = true;
		motionX = velocity.xCoord;
		motionY = velocity.yCoord;
		motionZ = velocity.zCoord;

		setParticleTexture(icon);
	}

	@Override
	public int getFXLayer() {
		return 1;
	}
}
