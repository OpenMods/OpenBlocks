package openblocks;

import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import openmods.api.IProxy;

public interface IOpenBlocksProxy extends IProxy {
	public void spawnLiquidSpray(World worldObj, Fluid fluid, double x, double y, double z, float scale, float gravity, Vec3 velocity);

	public void spawnParticleSpray(World worldObj, IIcon icon, double x, double y, double z, float scale, float gravity, Vec3 velocity);
}
