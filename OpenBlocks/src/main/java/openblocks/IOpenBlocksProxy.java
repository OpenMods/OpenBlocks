package openblocks;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import openmods.api.IProxy;

public interface IOpenBlocksProxy extends IProxy {
	int getParticleSettings();

	void spawnLiquidSpray(World worldObj, FluidStack fluid, double x, double y, double z, float scale, float gravity, Vec3d velocity);

}
