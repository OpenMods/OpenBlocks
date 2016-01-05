package openblocks.client.renderer.tileentity.tank;

import openmods.utils.Diagonal;

public class RenderConnection {
	private final DoubledCoords coords;

	public RenderConnection(DoubledCoords coords) {
		this.coords = coords;
	}

	public boolean check(int baseX, int baseY, int baseZ, ForgeDirection dir) {
		return coords.check(baseX, baseY, baseZ, dir);
	}

	public boolean check(int baseX, int baseY, int baseZ, Diagonal dir) {
		return coords.check(baseX, baseY, baseZ, dir);
	}
}