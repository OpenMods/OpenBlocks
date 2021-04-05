package openblocks.client.renderer.tileentity.tank;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.tileentity.TileEntityTank;
import openmods.utils.Diagonal;

public class DiagonalConnection extends RenderConnection {

	private static class Group {
		private final FluidStack fluid;
		private final Set<Diagonal> diagonals = EnumSet.noneOf(Diagonal.class);

		private float sum;

		public Group(FluidStack fluid) {
			this.fluid = fluid;
		}

		public boolean match(FluidStack stack) {
			return fluid.isFluidEqual(stack);
		}

		public void addDiagonal(Diagonal diagonal, FluidStack stack) {
			diagonals.add(diagonal);
			sum += stack.getAmount();
		}

		public void update(float[] height) {
			final float average = TankRenderUtils.clampLevel((sum / diagonals.size()) / TileEntityTank.getTankCapacity());

			for (Diagonal d : diagonals) {
				height[d.ordinal()] = average;
			}
		}
	}

	private final float phase;

	private final Map<Diagonal, FluidStack> fluids = Maps.newEnumMap(Diagonal.class);

	private final float[] height = new float[4];

	public DiagonalConnection(float phase, DoubledCoords coords) {
		super(coords);
		this.phase = phase;
	}

	public float getRenderHeight(Diagonal corner, float time) {
		float h = height[corner.ordinal()];
		if (h <= 0) {
			return 0;
		}
		return TankRenderUtils.calculateRenderHeight(time, phase, h);
	}

	public void updateFluid(Diagonal corner, FluidStack stack) {
		fluids.put(corner, stack.copy());
		recalculate();
	}

	public void clearFluid(Diagonal corner) {
		fluids.remove(corner);
		recalculate();
	}

	private static DiagonalConnection.Group findGroup(List<DiagonalConnection.Group> entries, FluidStack stack) {
		for (DiagonalConnection.Group group : entries) {
			if (group.match(stack)) {
				return group;
			}
		}

		DiagonalConnection.Group newGroup = new Group(stack);
		entries.add(newGroup);
		return newGroup;
	}

	private void recalculate() {
		forceZero();

		List<DiagonalConnection.Group> groups = Lists.newArrayList();
		for (Diagonal diagonal : Diagonal.VALUES) {
			if (!fluids.containsKey(diagonal)) {
				continue;
			}

			FluidStack stack = fluids.get(diagonal);

			if (stack == null || stack.getAmount() <= 0) {
				return;
			}

			DiagonalConnection.Group e = findGroup(groups, stack);
			e.addDiagonal(diagonal, stack);
		}

		for (DiagonalConnection.Group group : groups) {
			group.update(height);
		}
	}

	private void forceZero() {
		height[0] = height[1] = height[2] = height[3] = 0;
	}
}