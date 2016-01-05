package openblocks.client.renderer.tileentity.tank;

import openmods.utils.Diagonal;

public interface ITankConnections {

	public VerticalConnection getTopConnection();

	public VerticalConnection getBottomConnection();

	public HorizontalConnection getHorizontalConnection(ForgeDirection dir);

	public DiagonalConnection getDiagonalConnection(Diagonal dir);
}