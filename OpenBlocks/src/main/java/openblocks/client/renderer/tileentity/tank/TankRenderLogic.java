package openblocks.client.renderer.tileentity.tank;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.tileentity.TileEntityTank;
import openmods.liquids.GenericTank;
import openmods.utils.Diagonal;

public class TankRenderLogic {

	private static class TankConnections implements ITankConnections {

		private final GenericTank tank;

		private final Map<Diagonal, DiagonalConnection> diagonalConnections;

		private final Map<Direction, HorizontalConnection> horizontalConnections;

		private final VerticalConnection topConnection;

		private final VerticalConnection bottomConnection;

		public TankConnections(GenericTank tank, Map<Diagonal, DiagonalConnection> diagonalConnections, Map<Direction, HorizontalConnection> horizontalConnections, VerticalConnection topConnection, VerticalConnection bottomConnection) {
			this.tank = tank;
			this.diagonalConnections = diagonalConnections;
			this.horizontalConnections = horizontalConnections;
			this.topConnection = topConnection;
			this.bottomConnection = bottomConnection;
		}

		@Override
		public VerticalConnection getTopConnection() {
			return topConnection;
		}

		@Override
		public VerticalConnection getBottomConnection() {
			return bottomConnection;
		}

		@Override
		public HorizontalConnection getHorizontalConnection(Direction dir) {
			return horizontalConnections.get(dir);
		}

		@Override
		@Nullable
		public DiagonalConnection getDiagonalConnection(Diagonal dir) {
			return diagonalConnections.get(dir);
		}

		public void updateFluid(FluidStack fluidStack) {
			for (Map.Entry<Diagonal, DiagonalConnection> e : diagonalConnections.entrySet()) {
				e.getValue().updateFluid(e.getKey().getOpposite(), fluidStack);
			}

			for (Map.Entry<Direction, HorizontalConnection> e : horizontalConnections.entrySet()) {
				e.getValue().updateFluid(e.getKey().getOpposite(), fluidStack);
			}

			topConnection.updateBottomFluid(fluidStack, tank.getSpace() == 0);
			bottomConnection.updateTopFluid(fluidStack);
		}

		private static boolean checkConsistency(RenderConnection connection, BlockPos pos, Direction dir) {
			return connection != null && connection.isPositionEqualTo(pos, dir);
		}

		private static boolean checkConsistency(RenderConnection connection, BlockPos pos, Diagonal dir) {
			return connection != null && connection.isPositionEqualTo(pos, dir);
		}

		private boolean checkHorizontalConsistency(BlockPos pos, Direction dir) {
			return checkConsistency(horizontalConnections.get(dir), pos, dir);
		}

		private boolean checkDiagonalConsistency(BlockPos pos, Diagonal dir) {
			return checkConsistency(diagonalConnections.get(dir), pos, dir);
		}

		public boolean checkConsistency(BlockPos pos) {
			return checkConsistency(topConnection, pos, Direction.UP) &&
					checkConsistency(bottomConnection, pos, Direction.DOWN) &&
					checkHorizontalConsistency(pos, Direction.NORTH) &&
					checkHorizontalConsistency(pos, Direction.SOUTH) &&
					checkHorizontalConsistency(pos, Direction.EAST) &&
					checkHorizontalConsistency(pos, Direction.WEST) &&
					checkDiagonalConsistency(pos, Diagonal.NE) &&
					checkDiagonalConsistency(pos, Diagonal.NW) &&
					checkDiagonalConsistency(pos, Diagonal.SE) &&
					checkDiagonalConsistency(pos, Diagonal.SW);
		}

		public void detach() {
			for (Map.Entry<Diagonal, DiagonalConnection> e : diagonalConnections.entrySet()) {
				e.getValue().clearFluid(e.getKey().getOpposite());
			}

			for (Map.Entry<Direction, HorizontalConnection> e : horizontalConnections.entrySet()) {
				e.getValue().clearFluid(e.getKey().getOpposite());
			}

			if (topConnection != null) {
				topConnection.clearBottomFluid();
			}

			if (bottomConnection != null) {
				bottomConnection.clearTopFluid();
			}
		}

	}

	private static class TankRenderFluidData implements ITankRenderFluidData {

		private final TankConnections connections;

		private final GenericTank tank;

		private final float phase;

		public TankRenderFluidData(TankConnections connections, GenericTank tank, float phase) {
			this.connections = connections;
			this.tank = tank;
			this.phase = phase;
		}

		private static boolean isConnected(@Nullable GridConnection connection) {
			return connection != null && connection.isConnected();
		}

		@Override
		public boolean shouldRenderFluidWall(Direction side) {
			switch (side) {
				case DOWN:
					return !isConnected(connections.getBottomConnection());
				case UP:
					return !isConnected(connections.getTopConnection());
				case EAST:
				case WEST:
				case NORTH:
				case SOUTH: {
					return !isConnected(connections.getHorizontalConnection(side));
				}
				default:
					return true;
			}
		}

		@Override
		public boolean hasFluid() {
			return tank.getFluidAmount() > 0;
		}

		@Override
		public FluidStack getFluid() {
			return tank.getFluid();
		}

		@Override
		public float getCenterFluidLevel(float time) {
			final float raw = (float)tank.getFluidAmount() / tank.getCapacity();
			return TankRenderUtils.calculateRenderHeight(time, phase, raw);
		}

		@Override
		public float getCornerFluidLevel(Diagonal corner, float time) {
			final DiagonalConnection diagonal = connections.getDiagonalConnection(corner);
			return diagonal != null? diagonal.getRenderHeight(corner.getOpposite(), time) : getCenterFluidLevel(time);
		}
	}

	private final GenericTank tank;

	private BlockPos pos;

	private World world;

	private TankConnections connections;

	private TankRenderFluidData renderData;

	public TankRenderLogic(GenericTank tank) {
		this.tank = tank;
	}

	private DoubledCoords createCoords(Direction dir) {
		return new DoubledCoords(pos, dir);
	}

	private DoubledCoords createCoords(Diagonal dir) {
		return new DoubledCoords(pos, dir);
	}

	@Nullable
	private ITankConnections getNeighbourTank(BlockPos pos) {
		TileEntity te = TankRenderUtils.getTileEntitySafe(world, pos);
		return (te instanceof TileEntityTank)? ((TileEntityTank)te).getTankConnections() : null;
	}

	@Nullable
	private ITankConnections getNeighbourTank(Direction dir) {
		return getNeighbourTank(pos.offset(dir));
	}

	@Nullable
	private ITankConnections getNeighbourTank(Diagonal dir) {
		return getNeighbourTank(pos.add(dir.offsetX, dir.offsetY, dir.offsetZ));
	}

	private DiagonalConnection selectDiagonalConnection(@Nullable ITankConnections tankCW, @Nullable ITankConnections tankD, @Nullable ITankConnections tankCCW, Diagonal dir) {
		final Diagonal start = dir;

		dir = dir.rotateCW();
		if (tankCW != null) {
			return tankCW.getDiagonalConnection(dir);
		}

		dir = dir.rotateCW();
		if (tankD != null) {
			return tankD.getDiagonalConnection(dir);
		}

		dir = dir.rotateCW();
		if (tankCCW != null) {
			return tankCCW.getDiagonalConnection(dir);
		}

		return new DiagonalConnection(TankRenderUtils.calculatePhase(pos.getX(), pos.getY(), pos.getZ(), start), createCoords(start));
	}

	private void tryCornerConnection(Map<Diagonal, DiagonalConnection> diagonalConnections, @Nullable ITankConnections tankCW, @Nullable ITankConnections tankD, @Nullable ITankConnections tankCCW, Diagonal dir) {
		final DiagonalConnection connection = selectDiagonalConnection(tankCW, tankD, tankCCW, dir);
		diagonalConnections.put(dir, connection);
	}

	private void tryHorizontalConnection(Map<Direction, HorizontalConnection> horizontalConnections, @Nullable ITankConnections neighbour, Direction dir) {
		final HorizontalConnection connection = (neighbour != null)? neighbour.getHorizontalConnection(dir.getOpposite()) : new HorizontalConnection(createCoords(dir));
		horizontalConnections.put(dir, connection);
	}

	private VerticalConnection tryBottomConnection(@Nullable ITankConnections neighbour) {
		return neighbour != null? neighbour.getTopConnection() : new VerticalConnection(createCoords(Direction.DOWN));
	}

	private VerticalConnection tryTopConnection(@Nullable ITankConnections neighbour) {
		return neighbour != null? neighbour.getBottomConnection() : new VerticalConnection(createCoords(Direction.UP));
	}

	private TankConnections updateConnections() {
		final ITankConnections tankN = getNeighbourTank(Direction.NORTH);
		final ITankConnections tankS = getNeighbourTank(Direction.SOUTH);
		final ITankConnections tankW = getNeighbourTank(Direction.WEST);
		final ITankConnections tankE = getNeighbourTank(Direction.EAST);

		final ITankConnections tankNE = getNeighbourTank(Diagonal.NE);
		final ITankConnections tankNW = getNeighbourTank(Diagonal.NW);
		final ITankConnections tankSE = getNeighbourTank(Diagonal.SE);
		final ITankConnections tankSW = getNeighbourTank(Diagonal.SW);

		final ITankConnections tankT = getNeighbourTank(Direction.UP);
		final ITankConnections tankB = getNeighbourTank(Direction.DOWN);

		final VerticalConnection topConnection = tryTopConnection(tankT);
		final VerticalConnection bottomConnection = tryBottomConnection(tankB);

		final Map<Diagonal, DiagonalConnection> diagonalConnections = Maps.newEnumMap(Diagonal.class);

		final Map<Direction, HorizontalConnection> horizontalConnections = Maps.newEnumMap(Direction.class);

		tryHorizontalConnection(horizontalConnections, tankN, Direction.NORTH);
		tryHorizontalConnection(horizontalConnections, tankS, Direction.SOUTH);
		tryHorizontalConnection(horizontalConnections, tankW, Direction.WEST);
		tryHorizontalConnection(horizontalConnections, tankE, Direction.EAST);

		tryCornerConnection(diagonalConnections, tankN, tankNW, tankW, Diagonal.NW);
		tryCornerConnection(diagonalConnections, tankW, tankSW, tankS, Diagonal.SW);
		tryCornerConnection(diagonalConnections, tankE, tankNE, tankN, Diagonal.NE);
		tryCornerConnection(diagonalConnections, tankS, tankSE, tankE, Diagonal.SE);

		return new TankConnections(tank, diagonalConnections, horizontalConnections, topConnection, bottomConnection);
	}

	public void initialize(@Nullable World world, BlockPos pos) {
		this.world = world;
		this.pos = pos;

		if (this.connections != null) {
			connections.detach();
		}

		if (world == null) {
			this.connections = null;
			this.renderData = null;
		} else {
			float phase = TankRenderUtils.calculatePhase(pos.getX(), pos.getY(), pos.getZ());
			this.connections = updateConnections();
			this.renderData = new TankRenderFluidData(connections, tank, phase);
		}
	}

	public void validateConnections(World world, BlockPos pos) {
		if (world != this.world || connections == null || !connections.checkConsistency(pos)) {
			initialize(world, pos);
		}
	}

	public void invalidateConnections() {
		if (this.connections != null) {
			connections.detach();
		}
		this.connections = null;
		this.renderData = null;
	}

	public void updateFluid(FluidStack stack) {
		if (connections != null) {
			connections.updateFluid(stack);
		}
	}

	@Nullable
	public ITankRenderFluidData getTankRenderData() {
		return renderData;
	}

	public ITankConnections getTankConnections() {
		return connections;
	}

}
