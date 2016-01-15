package openblocks.client.renderer.tileentity.tank;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;

public class HorizontalConnection extends GridConnection {

	private FluidStack fluidA;

	private FluidStack fluidB;

	private boolean isConnected;

	public HorizontalConnection(DoubledCoords coords) {
		super(coords);
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}

	public void updateFluid(EnumFacing direction, FluidStack stack) {
		if (direction == EnumFacing.NORTH || direction == EnumFacing.WEST) this.fluidA = TankRenderUtils.safeCopy(stack);
		else this.fluidB = TankRenderUtils.safeCopy(stack);

		this.isConnected = fluidA != null && fluidB != null && fluidA.isFluidEqual(fluidB);
	}

	public void clearFluid(EnumFacing direction) {
		if (direction == EnumFacing.NORTH || direction == EnumFacing.WEST) this.fluidA = null;
		else this.fluidB = null;

		this.isConnected = false;
	}
}