package openblocks.client.renderer.tileentity.tank;

import net.minecraftforge.common.util.ForgeDirection;
import openmods.utils.Diagonal;

public class DoubledCoords {
	private final int x;
	private final int y;
	private final int z;

	private DoubledCoords(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public DoubledCoords(int baseX, int baseY, int baseZ, ForgeDirection dir) {
		this(2 * baseX + dir.offsetX, 2 * baseY + dir.offsetY, 2 * baseZ + dir.offsetZ);
	}

	public DoubledCoords(int baseX, int baseY, int baseZ, Diagonal dir) {
		this(2 * baseX + dir.offsetX, 2 * baseY + dir.offsetY, 2 * baseZ + dir.offsetZ);
	}

	public boolean check(int baseX, int baseY, int baseZ, ForgeDirection dir) {
		return (x == 2 * baseX + dir.offsetX) &&
				(y == 2 * baseY + dir.offsetY) &&
				(z == 2 * baseZ + dir.offsetZ);
	}

	public boolean check(int baseX, int baseY, int baseZ, Diagonal dir) {
		return (x == 2 * baseX + dir.offsetX) &&
				(y == 2 * baseY + dir.offsetY) &&
				(z == 2 * baseZ + dir.offsetZ);
	}
}