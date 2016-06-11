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

public class TankRenderLogic implements ITankConnections, ITankRenderFluidData {

	private int x;

	private int y;

	private int z;

	private World world;

	private final GenericTank tank;

	private final Map<Diagonal, DiagonalConnection> diagonalConnections = Maps.newEnumMap(Diagonal.class);

	private final Map<ForgeDirection, HorizontalConnection> horizontalConnections = Maps.newEnumMap(ForgeDirection.class);

	private VerticalConnection topConnection;

	private VerticalConnection bottomConnection;

	private float phase;

	public TankRenderLogic(GenericTank tank) {
		this.tank = tank;
	}

	private static boolean isConnected(GridConnection connection) {
		return connection != null? connection.isConnected() : false;
	}

	private DoubledCoords createCoords(ForgeDirection dir) {
		return new DoubledCoords(x, y, z, dir);
	}

	private DoubledCoords createCoords(Diagonal dir) {
		return new DoubledCoords(x, y, z, dir);
	}

	private ITankConnections getNeighbourTank(int x, int y, int z) {
		TileEntity te = TankRenderUtils.getTileEntitySafe(world, x, y, z);
		return (te instanceof TileEntityTank)? ((TileEntityTank)te).getRenderConnectionsData() : null;
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

	private void tryCornerConnection(ITankConnections tankCW, ITankConnections tankD, ITankConnections tankCCW, Diagonal dir) {
		final DiagonalConnection connection = selectDiagonalConnection(tankCW, tankD, tankCCW, dir);
		diagonalConnections.put(dir, connection);
	}

	private void tryHorizontalConnection(ITankConnections neighbour, ForgeDirection dir) {
		final HorizontalConnection connection = (neighbour != null)? neighbour.getHorizontalConnection(dir.getOpposite()) : new HorizontalConnection(createCoords(dir));
		horizontalConnections.put(dir, connection);
	}

	private void tryBottomConnection(ITankConnections neighbour) {
		bottomConnection = neighbour != null? neighbour.getTopConnection() : new VerticalConnection(createCoords(ForgeDirection.DOWN));
	}

	private void tryTopConnection(ITankConnections neighbour) {
		topConnection = neighbour != null? neighbour.getBottomConnection() : new VerticalConnection(createCoords(ForgeDirection.UP));
	}

	public void updateConnections() {
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

		tryTopConnection(tankT);
		tryBottomConnection(tankB);

		tryHorizontalConnection(tankN, ForgeDirection.NORTH);
		tryHorizontalConnection(tankS, ForgeDirection.SOUTH);
		tryHorizontalConnection(tankW, ForgeDirection.WEST);
		tryHorizontalConnection(tankE, ForgeDirection.EAST);

		tryCornerConnection(tankN, tankNW, tankW, Diagonal.NW);
		tryCornerConnection(tankW, tankSW, tankS, Diagonal.SW);
		tryCornerConnection(tankE, tankNE, tankN, Diagonal.NE);
		tryCornerConnection(tankS, tankSE, tankE, Diagonal.SE);
	}

	public void initialize(World world, int x, int y, int z) {
		this.phase = TankRenderUtils.calculatePhase(x, y, z);

		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;

		updateConnections();
	}

	public void clearConnections() {
		for (Map.Entry<Diagonal, DiagonalConnection> e : diagonalConnections.entrySet())
			e.getValue().clearFluid(e.getKey().getOpposite());

		diagonalConnections.clear();

		for (Map.Entry<ForgeDirection, HorizontalConnection> e : horizontalConnections.entrySet())
			e.getValue().clearFluid(e.getKey().getOpposite());

		horizontalConnections.clear();

		if (topConnection != null) {
			topConnection.clearBottomFluid();
			topConnection = null;
		}

		if (bottomConnection != null) {
			bottomConnection.clearTopFluid();
			bottomConnection = null;
		}
	}

	private boolean checkConnection(RenderConnection connection, ForgeDirection dir) {
		return connection == null || !connection.check(x, y, z, dir);
	}

	private boolean checkConnection(RenderConnection connection, Diagonal dir) {
		return connection == null || !connection.check(x, y, z, dir);
	}

	private boolean checkHorizontalConnection(ForgeDirection dir) {
		return checkConnection(horizontalConnections.get(dir), dir);
	}

	private boolean checkDiagonalConnection(Diagonal dir) {
		return checkConnection(diagonalConnections.get(dir), dir);
	}

	private boolean checkConnections() {
		return checkConnection(topConnection, ForgeDirection.UP) ||
				checkConnection(bottomConnection, ForgeDirection.DOWN) ||
				checkConnection(topConnection, ForgeDirection.UP) ||
				checkHorizontalConnection(ForgeDirection.NORTH) ||
				checkHorizontalConnection(ForgeDirection.SOUTH) ||
				checkHorizontalConnection(ForgeDirection.EAST) ||
				checkHorizontalConnection(ForgeDirection.WEST) ||
				checkDiagonalConnection(Diagonal.NE) ||
				checkDiagonalConnection(Diagonal.NW) ||
				checkDiagonalConnection(Diagonal.SE) ||
				checkDiagonalConnection(Diagonal.SW);

	}

	public void validateConnections() {
		if (checkConnections()) {
			clearConnections();
			updateConnections();
		}
	}

	public void updateFluid(FluidStack fluidStack) {
		for (Map.Entry<Diagonal, DiagonalConnection> e : diagonalConnections.entrySet())
			e.getValue().updateFluid(e.getKey().getOpposite(), fluidStack);

		for (Map.Entry<ForgeDirection, HorizontalConnection> e : horizontalConnections.entrySet())
			e.getValue().updateFluid(e.getKey().getOpposite(), fluidStack);

		topConnection.updateBottomFluid(fluidStack, tank.getSpace() == 0);
		bottomConnection.updateTopFluid(fluidStack);
	}

	@Override
	public boolean shouldRenderFluidWall(ForgeDirection side) {
		switch (side) {
			case DOWN:
				return !isConnected(bottomConnection);
			case UP:
				return !isConnected(topConnection);
			case EAST:
			case WEST:
			case NORTH:
			case SOUTH: {
				return !isConnected(horizontalConnections.get(side));
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
		final DiagonalConnection diagonal = diagonalConnections.get(corner);
		return diagonal != null? diagonal.getRenderHeight(corner.getOpposite(), time) : getCenterFluidLevel(time);
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

}
