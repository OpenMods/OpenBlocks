package openblocks.client.renderer.tileentity.tank;

import net.minecraft.util.Direction;
import openmods.utils.Diagonal;

public interface ITankConnections {

	VerticalConnection getTopConnection();

	VerticalConnection getBottomConnection();

	HorizontalConnection getHorizontalConnection(Direction dir);

	DiagonalConnection getDiagonalConnection(Diagonal dir);
}