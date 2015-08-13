package openblocks.client.renderer.tileentity.tank;

import net.minecraftforge.common.util.ForgeDirection;
import openmods.utils.Diagonal;

public interface ITankConnections {

	public VerticalConnection getTopConnection();

	public VerticalConnection getBottomConnection();

	public HorizontalConnection getHorizontalConnection(ForgeDirection dir);

	public DiagonalConnection getDiagonalConnection(Diagonal dir);
}