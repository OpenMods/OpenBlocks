package openblocks.client.renderer.tileentity.tank;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import openblocks.common.tileentity.TileEntityTank;
import openmods.liquids.GenericTank;
import openmods.utils.Diagonal;

public class TankRenderLogic {

	private static class TankConnections implements ITankConnections {

		private final GenericTank tank;

		private final Map<Diagonal, DiagonalConnection> diagonalConnections;

		private final Map<ForgeDirection, HorizontalConnection> horizontalConnections;

		private final VerticalConnection topConnection;

		private final VerticalConnection bottomConnection;

		public TankConnections(GenericTank tank, Map<Diagonal, DiagonalConnection> diagonalConnections, Map<ForgeDirection, HorizontalConnection> horizontalConnections, VerticalConnection topConnection, VerticalConnection bottomConnection) {
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
		public HorizontalConnection getHorizontalConnection(ForgeDirection dir) {
			return horizontalConnections.get(dir);
		}

		@Override
		public DiagonalConnection getDiagonalConnection(Diagonal dir) {
			return diagonalConnections.get(dir);
		}

		public void updateFluid(FluidStack fluidStack) {
			for (Map.Entry<Diagonal, DiagonalConnection> e : diagonalConnections.entrySet())
				e.getValue().updateFluid(e.getKey().getOpposite(), fluidStack);

			for (Map.Entry<ForgeDirection, HorizontalConnection> e : horizontalConnections.entrySet())
				e.getValue().updateFluid(e.getKey().getOpposite(), fluidStack);

			topConnection.updateBottomFluid(fluidStack, tank.getSpace() == 0);
			bottomConnection.updateTopFluid(fluidStack);
		}

		private static boolean checkConsistency(RenderConnection connection, int x, int y, int z, ForgeDirection dir) {
			return connection != null && connection.isPositionEqualTo(x, y, z, dir);
		}

		private static boolean checkConsistency(RenderConnection connection, int x, int y, int z, Diagonal dir) {
			return connection != null && connection.isPositionEqualTo(x, y, z, dir);
		}

		private boolean checkHorizontalConsistency(int x, int y, int z, ForgeDirection dir) {
			return checkConsistency(horizontalConnections.get(dir), x, y, z, dir);
		}

		private boolean checkDiagonalConsistency(int x, int y, int z, Diagonal dir) {
			return checkConsistency(diagonalConnections.get(dir), x, y, z, dir);
		}

		public boolean checkConsistency(int x, int y, int z) {
			return checkConsistency(topConnection, x, y, z, ForgeDirection.UP) &&
					checkConsistency(bottomConnection, x, y, z, ForgeDirection.DOWN) &&
					checkHorizontalConsistency(x, y, z, ForgeDirection.NORTH) &&
					checkHorizontalConsistency(x, y, z, ForgeDirection.SOUTH) &&
					checkHorizontalConsistency(x, y, z, ForgeDirection.EAST) &&
					checkHorizontalConsistency(x, y, z, ForgeDirection.WEST) &&
					checkDiagonalConsistency(x, y, z, Diagonal.NE) &&
					checkDiagonalConsistency(x, y, z, Diagonal.NW) &&
					checkDiagonalConsistency(x, y, z, Diagonal.SE) &&
					checkDiagonalConsistency(x, y, z, Diagonal.SW);
		}

		public void detach() {
			for (Map.Entry<Diagonal, DiagonalConnection> e : diagonalConnections.entrySet())
				e.getValue().clearFluid(e.getKey().getOpposite());

			for (Map.Entry<ForgeDirection, HorizontalConnection> e : horizontalConnections.entrySet())
				e.getValue().clearFluid(e.getKey().getOpposite());

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

		private static boolean isConnected(GridConnection connection) {
			return connection != null? connection.isConnected() : false;
		}

		@Override
		public boolean shouldRenderFluidWall(ForgeDirection side) {
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
			final float waving = TankRenderUtils.calculateWaveAmplitude(time, phase) + raw;
			return TankRenderUtils.clampLevel(waving);
		}

		@Override
		public float getCornerFluidLevel(Diagonal corner, float time) {
			final DiagonalConnection diagonal = connections.getDiagonalConnection(corner);
			return diagonal != null? diagonal.getRenderHeight(corner.getOpposite(), time) : getCenterFluidLevel(time);
		}
	}

	private final GenericTank tank;

	private int x;

	private int y;

	private int z;

	private World world;

	private TankConnections connections;

	private TankRenderFluidData renderData;

	public TankRenderLogic(GenericTank tank) {
		this.tank = tank;
	}

	private DoubledCoords createCoords(ForgeDirection dir) {
		return new DoubledCoords(x, y, z, dir);
	}

	private DoubledCoords createCoords(Diagonal dir) {
		return new DoubledCoords(x, y, z, dir);
	}

	private ITankConnections getNeighbourTank(int x, int y, int z) {
		TileEntity te = TankRenderUtils.getTileEntitySafe(world, x, y, z);
		return (te instanceof TileEntityTank)? ((TileEntityTank)te).getTankConnections() : null;
	}

	private ITankConnections getNeighbourTank(ForgeDirection dir) {
		return getNeighbourTank(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
	}

	private ITankConnections getNeighbourTank(Diagonal dir) {
		return getNeighbourTank(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
	}

	private DiagonalConnection selectDiagonalConnection(ITankConnections tankCW, ITankConnections tankD, ITankConnections tankCCW, Diagonal dir) {
		final Diagonal start = dir;

		dir = dir.rotateCW();
		if (tankCW != null) return tankCW.getDiagonalConnection(dir);

		dir = dir.rotateCW();
		if (tankD != null) return tankD.getDiagonalConnection(dir);

		dir = dir.rotateCW();
		if (tankCCW != null) return tankCCW.getDiagonalConnection(dir);

		return new DiagonalConnection(TankRenderUtils.calculatePhase(x, y, z, start), createCoords(start));
	}

	private void tryCornerConnection(Map<Diagonal, DiagonalConnection> diagonalConnections, ITankConnections tankCW, ITankConnections tankD, ITankConnections tankCCW, Diagonal dir) {
		final DiagonalConnection connection = selectDiagonalConnection(tankCW, tankD, tankCCW, dir);
		diagonalConnections.put(dir, connection);
	}

	private void tryHorizontalConnection(Map<ForgeDirection, HorizontalConnection> horizontalConnections, ITankConnections neighbour, ForgeDirection dir) {
		final HorizontalConnection connection = (neighbour != null)? neighbour.getHorizontalConnection(dir.getOpposite()) : new HorizontalConnection(createCoords(dir));
		horizontalConnections.put(dir, connection);
	}

	private VerticalConnection tryBottomConnection(ITankConnections neighbour) {
		return neighbour != null? neighbour.getTopConnection() : new VerticalConnection(createCoords(ForgeDirection.DOWN));
	}

	private VerticalConnection tryTopConnection(ITankConnections neighbour) {
		return neighbour != null? neighbour.getBottomConnection() : new VerticalConnection(createCoords(ForgeDirection.UP));
	}

	private TankConnections updateConnections() {
		final ITankConnections tankN = getNeighbourTank(ForgeDirection.NORTH);
		final ITankConnections tankS = getNeighbourTank(ForgeDirection.SOUTH);
		final ITankConnections tankW = getNeighbourTank(ForgeDirection.WEST);
		final ITankConnections tankE = getNeighbourTank(ForgeDirection.EAST);

		final ITankConnections tankNE = getNeighbourTank(Diagonal.NE);
		final ITankConnections tankNW = getNeighbourTank(Diagonal.NW);
		final ITankConnections tankSE = getNeighbourTank(Diagonal.SE);
		final ITankConnections tankSW = getNeighbourTank(Diagonal.SW);

		final ITankConnections tankT = getNeighbourTank(ForgeDirection.UP);
		final ITankConnections tankB = getNeighbourTank(ForgeDirection.DOWN);

		final VerticalConnection topConnection = tryTopConnection(tankT);
		final VerticalConnection bottomConnection = tryBottomConnection(tankB);

		final Map<Diagonal, DiagonalConnection> diagonalConnections = Maps.newEnumMap(Diagonal.class);

		final Map<ForgeDirection, HorizontalConnection> horizontalConnections = Maps.newEnumMap(ForgeDirection.class);

		tryHorizontalConnection(horizontalConnections, tankN, ForgeDirection.NORTH);
		tryHorizontalConnection(horizontalConnections, tankS, ForgeDirection.SOUTH);
		tryHorizontalConnection(horizontalConnections, tankW, ForgeDirection.WEST);
		tryHorizontalConnection(horizontalConnections, tankE, ForgeDirection.EAST);

		tryCornerConnection(diagonalConnections, tankN, tankNW, tankW, Diagonal.NW);
		tryCornerConnection(diagonalConnections, tankW, tankSW, tankS, Diagonal.SW);
		tryCornerConnection(diagonalConnections, tankE, tankNE, tankN, Diagonal.NE);
		tryCornerConnection(diagonalConnections, tankS, tankSE, tankE, Diagonal.SE);

		return new TankConnections(tank, diagonalConnections, horizontalConnections, topConnection, bottomConnection);
	}

	public void initialize(World world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;

		if (this.connections != null) connections.detach();

		if (world == null) {
			this.connections = null;
			this.renderData = null;
		} else {
			float phase = TankRenderUtils.calculatePhase(x, y, z);
			this.connections = updateConnections();
			this.renderData = new TankRenderFluidData(connections, tank, phase);
		}
	}

	public void validateConnections(World world, int x, int y, int z) {
		if (world != this.world || connections == null || !connections.checkConsistency(x, y, z))
			initialize(world, x, y, z);
	}

	public void invalidateConnections() {
		if (this.connections != null) connections.detach();
		this.connections = null;
		this.renderData = null;
	}

	public void updateFluid(FluidStack stack) {
		if (connections != null) connections.updateFluid(stack);
	}

	public ITankRenderFluidData getTankRenderData() {
		return renderData;
	}

	public ITankConnections getTankConnections() {
		return connections;
	}

}
