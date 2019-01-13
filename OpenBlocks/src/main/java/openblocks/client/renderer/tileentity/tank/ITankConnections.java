package openblocks.client.renderer.tileentity.tank;

import net.minecraft.util.EnumFacing;
import openmods.utils.Diagonal;

public interface ITankConnections {

	VerticalConnection getTopConnection();

	VerticalConnection getBottomConnection();

	HorizontalConnection getHorizontalConnection(EnumFacing dir);

	DiagonalConnection getDiagonalConnection(Diagonal dir);
}