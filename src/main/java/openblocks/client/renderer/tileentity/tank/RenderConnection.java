package openblocks.client.renderer.tileentity.tank;

import net.minecraftforge.common.util.ForgeDirection;
import openmods.utils.Diagonal;

public class RenderConnection {
	private final DoubledCoords coords;

	public RenderConnection(DoubledCoords coords) {
		this.coords = coords;
	}

	public boolean isPositionEqualTo(int baseX, int baseY, int baseZ, ForgeDirection dir) {
		return coords.isSameAs(baseX, baseY, baseZ, dir);
	}

	public boolean isPositionEqualTo(int baseX, int baseY, int baseZ, Diagonal dir) {
		return coords.isSameAs(baseX, baseY, baseZ, dir);
	}
}