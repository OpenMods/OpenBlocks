package openblocks.client.renderer.tileentity.tank;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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

	public DoubledCoords(int baseX, int baseY, int baseZ, EnumFacing dir) {
		this(2 * baseX + dir.getFrontOffsetX(), 2 * baseY + dir.getFrontOffsetY(), 2 * baseZ + dir.getFrontOffsetZ());
	}

	public DoubledCoords(BlockPos pos, EnumFacing dir) {
		this(pos.getX(), pos.getY(), pos.getZ(), dir);
	}

	public DoubledCoords(int baseX, int baseY, int baseZ, Diagonal dir) {
		this(2 * baseX + dir.offsetX, 2 * baseY + dir.offsetY, 2 * baseZ + dir.offsetZ);
	}

	public DoubledCoords(BlockPos pos, Diagonal dir) {
		this(pos.getX(), pos.getY(), pos.getZ(), dir);
	}

	public boolean check(int baseX, int baseY, int baseZ, EnumFacing dir) {
		return (x == 2 * baseX + dir.getFrontOffsetX()) &&
				(y == 2 * baseY + dir.getFrontOffsetY()) &&
				(z == 2 * baseZ + dir.getFrontOffsetZ());
	}

	public boolean check(BlockPos pos, EnumFacing dir) {
		return check(pos.getX(), pos.getY(), pos.getZ(), dir);
	}

	public boolean check(int baseX, int baseY, int baseZ, Diagonal dir) {
		return (x == 2 * baseX + dir.offsetX) &&
				(y == 2 * baseY + dir.offsetY) &&
				(z == 2 * baseZ + dir.offsetZ);
	}

	public boolean check(BlockPos pos, Diagonal dir) {
		return check(pos.getX(), pos.getY(), pos.getZ(), dir);
	}
}