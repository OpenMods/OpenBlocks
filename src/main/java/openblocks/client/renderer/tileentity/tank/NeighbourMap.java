package openblocks.client.renderer.tileentity.tank;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.tileentity.TileEntityTank;

public class NeighbourMap implements INeighbourMap {
	private boolean[] neighbors = new boolean[64];

	public NeighbourMap(World world, int x, int y, int z, FluidStack fluid) {
		if (world == null) return;

		testNeighbour(world, fluid, x + 0, y + 1, z + 0, DIR_UP);
		testNeighbour(world, fluid, x + 0, y - 1, z + 0, DIR_DOWN);

		testNeighbour(world, fluid, x + 1, y + 0, z + 0, DIR_EAST);
		testNeighbour(world, fluid, x - 1, y + 0, z + 0, DIR_WEST);
		testNeighbour(world, fluid, x + 0, y + 0, z + 1, DIR_SOUTH);
		testNeighbour(world, fluid, x + 0, y + 0, z - 1, DIR_NORTH);

		testNeighbour(world, fluid, x + 1, y + 1, z + 0, DIR_UP | DIR_EAST);
		testNeighbour(world, fluid, x - 1, y + 1, z + 0, DIR_UP | DIR_WEST);
		testNeighbour(world, fluid, x + 0, y + 1, z + 1, DIR_UP | DIR_SOUTH);
		testNeighbour(world, fluid, x + 0, y + 1, z - 1, DIR_UP | DIR_NORTH);

		testNeighbour(world, fluid, x + 1, y - 1, z + 0, DIR_DOWN | DIR_EAST);
		testNeighbour(world, fluid, x - 1, y - 1, z + 0, DIR_DOWN | DIR_WEST);
		testNeighbour(world, fluid, x + 0, y - 1, z + 1, DIR_DOWN | DIR_SOUTH);
		testNeighbour(world, fluid, x + 0, y - 1, z - 1, DIR_DOWN | DIR_NORTH);

		testNeighbour(world, fluid, x - 1, y + 0, z - 1, DIR_WEST | DIR_NORTH);
		testNeighbour(world, fluid, x - 1, y + 0, z + 1, DIR_WEST | DIR_SOUTH);
		testNeighbour(world, fluid, x + 1, y + 0, z + 1, DIR_EAST | DIR_SOUTH);
		testNeighbour(world, fluid, x + 1, y + 0, z - 1, DIR_EAST | DIR_NORTH);
	}

	private void testNeighbour(World world, FluidStack ownFluid, int x, int y, int z, int flag) {
		final TileEntity te = TankRenderUtils.getTileEntitySafe(world, x, y, z);
		if (te instanceof TileEntityTank) neighbors[flag] = ((TileEntityTank)te).accepts(ownFluid);
	}

	@Override
	public boolean hasDirectNeighbour(int direction) {
		return neighbors[direction];
	}

	@Override
	public boolean hasDiagonalNeighbour(int direction1, int direction2) {
		return neighbors[direction1 | direction2];
	}
}