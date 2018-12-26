package openblocks.client.renderer.tileentity.tank;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import openmods.utils.Diagonal;

public interface ITankRenderFluidData {
	public FluidStack getFluid();

	public boolean hasFluid();

	public boolean shouldRenderFluidWall(EnumFacing side);

	public float getCornerFluidLevel(Diagonal diagonal, float time);

	public float getCenterFluidLevel(float time);
}