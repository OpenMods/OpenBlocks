package openblocks.client.renderer.tileentity.tank;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import openmods.utils.Diagonal;

public class RenderConnection {
	private final DoubledCoords coords;

	public RenderConnection(DoubledCoords coords) {
		this.coords = coords;
	}

	public boolean check(BlockPos pos, EnumFacing dir) {
		return coords.check(pos, dir);
	}

	public boolean check(BlockPos pos, Diagonal dir) {
		return coords.check(pos, dir);
	}
}