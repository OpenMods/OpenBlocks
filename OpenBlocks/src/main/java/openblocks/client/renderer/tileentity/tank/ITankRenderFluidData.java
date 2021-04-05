package openblocks.client.renderer.tileentity.tank;

import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import openmods.utils.Diagonal;

public interface ITankRenderFluidData {
	FluidStack getFluid();

	boolean hasFluid();

	boolean shouldRenderFluidWall(Direction side);

	float getCornerFluidLevel(Diagonal diagonal, float time);

	float getCenterFluidLevel(float time);
}