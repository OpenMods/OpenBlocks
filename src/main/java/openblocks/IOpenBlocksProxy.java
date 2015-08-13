package openblocks;

import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import openmods.api.IProxy;

public interface IOpenBlocksProxy extends IProxy {
	public void spawnLiquidSpray(World worldObj, FluidStack water, double x, double y, double z, float scale, float gravity, Vec3 velocityVector);
}
