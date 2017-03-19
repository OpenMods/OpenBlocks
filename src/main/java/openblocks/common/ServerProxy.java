package openblocks.common;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import openblocks.IOpenBlocksProxy;

public class ServerProxy implements IOpenBlocksProxy {

	@Override
	public void preInit() {}

	@Override
	public void init() {}

	@Override
	public void postInit() {}

	@Override
	public void registerRenderInformation() {}

	@Override
	public void spawnLiquidSpray(World worldObj, FluidStack fluid, double x, double y, double z, float scale, float gravity, Vec3d velocity) {}

	@Override
	public int getParticleSettings() {
		return 3; // really no particles
	}

}
