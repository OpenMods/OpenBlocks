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
			testNeighbour(neighbours, world, fluid, x + 0, y + 1, z + 0, "n_t");
			testNeighbour(neighbours, world, fluid, x + 0, y - 1, z + 0, "n_b");

			testNeighbour(neighbours, world, fluid, x + 1, y + 0, z + 0, "n_e");
			testNeighbour(neighbours, world, fluid, x - 1, y + 0, z + 0, "n_w");
			testNeighbour(neighbours, world, fluid, x + 0, y + 0, z + 1, "n_s");
			testNeighbour(neighbours, world, fluid, x + 0, y + 0, z - 1, "n_n");

			testNeighbour(neighbours, world, fluid, x + 1, y + 1, z + 0, "n_te");
			testNeighbour(neighbours, world, fluid, x - 1, y + 1, z + 0, "n_tw");
			testNeighbour(neighbours, world, fluid, x + 0, y + 1, z + 1, "n_ts");
			testNeighbour(neighbours, world, fluid, x + 0, y + 1, z - 1, "n_tn");

			testNeighbour(neighbours, world, fluid, x + 1, y - 1, z + 0, "n_be");
			testNeighbour(neighbours, world, fluid, x - 1, y - 1, z + 0, "n_bw");
			testNeighbour(neighbours, world, fluid, x + 0, y - 1, z + 1, "n_bs");
			testNeighbour(neighbours, world, fluid, x + 0, y - 1, z - 1, "n_bn");

			testNeighbour(neighbours, world, fluid, x - 1, y + 0, z - 1, "n_nw");
			testNeighbour(neighbours, world, fluid, x - 1, y + 0, z + 1, "n_sw");
			testNeighbour(neighbours, world, fluid, x + 1, y + 0, z + 1, "n_se");
			testNeighbour(neighbours, world, fluid, x + 1, y + 0, z - 1, "n_ne");

			this.state = VariantModelState.create().withKeys(neighbours);
		}
	}

	public VariantModelState getState() {
		return this.state;
	}
}