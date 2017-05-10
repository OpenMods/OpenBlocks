package openblocks.client.renderer.tileentity.tank;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.tileentity.TileEntityTank;
import openmods.model.variant.VariantModelState;

public class NeighbourMap {
	private final VariantModelState state;

	public static final String DIR_NORTH = "n";
	public static final String DIR_SOUTH = "s";

	public static final String DIR_WEST = "w";
	public static final String DIR_EAST = "e";

	public static final String DIR_UP = "t";
	public static final String DIR_DOWN = "b";

	private static String direct(String a) {
		return "n_" + a;
	}

	private static String diagonal(String a, String b) {
		return "n_" + a + b;
	}

	private static void testNeighbour(Set<String> result, World world, FluidStack ownFluid, int x, int y, int z, String id) {
		final TileEntity te = TankRenderUtils.getTileEntitySafe(world, new BlockPos(x, y, z));
		if (te instanceof TileEntityTank) if (((TileEntityTank)te).accepts(ownFluid)) result.add(id);
	}

	public NeighbourMap(World world, BlockPos pos, FluidStack fluid) {
		if (world == null) {
			this.state = VariantModelState.EMPTY;
		} else {
			final int x = pos.getX();
			final int y = pos.getY();
			final int z = pos.getZ();

			final Set<String> neighbours = Sets.newHashSet();
			testNeighbour(neighbours, world, fluid, x + 0, y + 1, z + 0, direct(DIR_UP));
			testNeighbour(neighbours, world, fluid, x + 0, y - 1, z + 0, direct(DIR_DOWN));

			testNeighbour(neighbours, world, fluid, x + 1, y + 0, z + 0, direct(DIR_EAST));
			testNeighbour(neighbours, world, fluid, x - 1, y + 0, z + 0, direct(DIR_WEST));
			testNeighbour(neighbours, world, fluid, x + 0, y + 0, z + 1, direct(DIR_SOUTH));
			testNeighbour(neighbours, world, fluid, x + 0, y + 0, z - 1, direct(DIR_NORTH));

			testNeighbour(neighbours, world, fluid, x + 1, y + 1, z + 0, diagonal(DIR_UP, DIR_EAST));
			testNeighbour(neighbours, world, fluid, x - 1, y + 1, z + 0, diagonal(DIR_UP, DIR_WEST));
			testNeighbour(neighbours, world, fluid, x + 0, y + 1, z + 1, diagonal(DIR_UP, DIR_SOUTH));
			testNeighbour(neighbours, world, fluid, x + 0, y + 1, z - 1, diagonal(DIR_UP, DIR_NORTH));

			testNeighbour(neighbours, world, fluid, x + 1, y - 1, z + 0, diagonal(DIR_DOWN, DIR_EAST));
			testNeighbour(neighbours, world, fluid, x - 1, y - 1, z + 0, diagonal(DIR_DOWN, DIR_WEST));
			testNeighbour(neighbours, world, fluid, x + 0, y - 1, z + 1, diagonal(DIR_DOWN, DIR_SOUTH));
			testNeighbour(neighbours, world, fluid, x + 0, y - 1, z - 1, diagonal(DIR_DOWN, DIR_NORTH));

			testNeighbour(neighbours, world, fluid, x - 1, y + 0, z - 1, diagonal(DIR_NORTH, DIR_WEST));
			testNeighbour(neighbours, world, fluid, x - 1, y + 0, z + 1, diagonal(DIR_SOUTH, DIR_WEST));
			testNeighbour(neighbours, world, fluid, x + 1, y + 0, z + 1, diagonal(DIR_SOUTH, DIR_EAST));
			testNeighbour(neighbours, world, fluid, x + 1, y + 0, z - 1, diagonal(DIR_NORTH, DIR_EAST));

			this.state = VariantModelState.create().withKeys(neighbours);
		}
	}

	public VariantModelState getState() {
		return this.state;
	}
}