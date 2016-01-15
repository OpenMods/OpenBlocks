package openblocks.client.renderer.tileentity.tank;

import net.minecraft.util.EnumFacing;
import openmods.utils.Diagonal;

public interface ITankConnections {

	public VerticalConnection getTopConnection();

	public VerticalConnection getBottomConnection();

	public HorizontalConnection getHorizontalConnection(EnumFacing dir);

	public DiagonalConnection getDiagonalConnection(Diagonal dir);
}