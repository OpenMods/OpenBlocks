package openblocks.utils;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;
import openblocks.shapes.IShapeable;

public class GeometryUtils {

	public enum Octant {
		TopSouthWest(-1, 1, 1, "Top South West"),
		TopNorthEast(1, 1, -1, "Top North East"),
		TopNorthWest(1, 1, 1, "Top North West"),
		TopSouthEast(-1, 1, -1, "Top South East"),
		BottomSouthWest(-1, -1, 1, "Bottom South West"),
		BottomNorthEast(1, -1, -1, "Bottom North East"),
		BottomNorthWest(1, -1, 1, "Bottom North West"),
		BottomSouthEast(-1, -1, -1, "Bottom South East");

		public static final EnumSet<Octant> ALL = EnumSet.allOf(Octant.class);
		public static final EnumSet<Octant> TOP = EnumSet.of(Octant.TopSouthEast, Octant.TopSouthWest, Octant.TopNorthEast, Octant.TopNorthWest);
		public static final EnumSet<Octant> BOTTOM = EnumSet.of(Octant.BottomSouthEast, Octant.BottomSouthWest, Octant.BottomNorthEast, Octant.BottomNorthWest);

		private final int x, y, z;
		private final String name;

		public int getXOffset() {
			return x;
		}

		public int getYOffset() {
			return y;
		}

		public int getZOffset() {
			return z;
		}

		public String getFriendlyName() {
			return name;
		}

		Octant(int x, int y, int z, String friendlyName) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.name = friendlyName;
		}
	}

	/**
	 * Makes a link of blocks in a shape
	 * 
	 * @param startX
	 *            X position of origin
	 * @param startY
	 *            Y position of origin
	 * @param startZ
	 *            Z position of origin
	 * @param direction
	 *            Direction to apply the line
	 * @param length
	 *            Length of the line
	 * @param shapeable
	 *            Shapeable object to set the blocks
	 */
	public static void makeLine(int startX, int startY, int startZ, ForgeDirection direction, int length, IShapeable shapeable) {
		if (length == 0) return;
		for (int offset = 0; offset <= length; offset++)
			/* Create a line in the direction of direction, length in size */
			shapeable.setBlock(startX + (offset * direction.offsetX), startY
					+ (offset * direction.offsetY), startZ
					+ (offset * direction.offsetZ));
	}

	/**
	 * Makes a flat plane along two directions
	 * 
	 * @param startX
	 *            X position of origin
	 * @param startY
	 *            Y position of origin
	 * @param startZ
	 *            Z position of origin
	 * @param width
	 *            Width of the plane
	 * @param height
	 *            Height of the plane
	 * @param right
	 *            The right(width) direction of the plane
	 * @param up
	 *            The up(height) direction of the plane
	 * @param shapeable
	 *            The shape to apply the blocks to
	 */
	public static void makePlane(int startX, int startY, int startZ, int width, int height, ForgeDirection right, ForgeDirection up, IShapeable shapeable) {
		if (width == 0 || height == 0) return;
		int lineOffsetX, lineOffsetY, lineOffsetZ;
		// We offset each line by up, and then apply it right
		for (int h = 0; h <= height; h++) {
			lineOffsetX = startX + (h * up.offsetX);
			lineOffsetY = startY + (h * up.offsetY);
			lineOffsetZ = startZ + (h * up.offsetZ);
			makeLine(lineOffsetX, lineOffsetY, lineOffsetZ, right, width, shapeable);
		}
	}

	public static void makeSphere(int radiusX, int radiusY, int radiusZ, IShapeable shapeable, EnumSet<Octant> octants) {

		final double invRadiusX = 1.0 / radiusX;
		final double invRadiusY = 1.0 / radiusY;
		final double invRadiusZ = 1.0 / radiusZ;

		final Set<Octant> octantSet = octants;

		double nextXn = 0;
		forX: for (int x = 0; x <= radiusX; ++x) {
			final double xn = nextXn;
			nextXn = (x + 1) * invRadiusX;
			double nextYn = 0;
			forY: for (int y = 0; y <= radiusY; ++y) {
				final double yn = nextYn;
				nextYn = (y + 1) * invRadiusY;
				double nextZn = 0;
				forZ: for (int z = 0; z <= radiusZ; ++z) {
					final double zn = nextZn;
					nextZn = (z + 1) * invRadiusZ;

					double distanceSq = MathUtils.lengthSq(xn, yn, zn);
					if (distanceSq > 1) {
						if (z == 0) {
							if (y == 0) {
								break forX;
							}
							break forY;
						}
						break forZ;
					}

					if (MathUtils.lengthSq(nextXn, yn, zn) <= 1
							&& MathUtils.lengthSq(xn, nextYn, zn) <= 1
							&& MathUtils.lengthSq(xn, yn, nextZn) <= 1) {
						continue;
					}

					for (Octant octant : octantSet) {
						shapeable.setBlock(x * octant.getXOffset(), y
								* octant.getYOffset(), z * octant.getZOffset());
					}
				}
			}
		}
	}

	public static void line2D(int y, int x0, int z0, int x1, int z1, IShapeable shapeable) {
		int dx = Math.abs(x1 - x0), sx = x0 < x1? 1 : -1;
		int dy = -Math.abs(z1 - z0), sy = z0 < z1? 1 : -1;
		int err = dx + dy, e2;

		for (;;) {
			shapeable.setBlock(x0, y, z0);
			if (x0 == x1 && z0 == z1) break;
			e2 = 2 * err;
			if (e2 >= dy) {
				err += dy;
				x0 += sx;
			} /* e_xy+e_x > 0 */
			if (e2 <= dx) {
				err += dx;
				z0 += sy;
			} /* e_xy+e_y < 0 */
		}
	}

	public static void line3D(Vec3 start, Vec3 end, IShapeable shapeable) {

		int dx = (int)(end.xCoord - start.xCoord);
		int dy = (int)(end.yCoord - start.yCoord);
		int dz = (int)(end.zCoord - start.zCoord);

		int ax = Math.abs(dx) << 1;
		int ay = Math.abs(dy) << 1;
		int az = Math.abs(dz) << 1;

		int signx = (int)Math.signum(dx);
		int signy = (int)Math.signum(dy);
		int signz = (int)Math.signum(dz);

		int x = (int)start.xCoord;
		int y = (int)start.yCoord;
		int z = (int)start.zCoord;

		int deltax, deltay, deltaz;
		if (ax >= Math.max(ay, az)) {
			deltay = ay - (ax >> 1);
			deltaz = az - (ax >> 1);
			while (true) {
				shapeable.setBlock(x, y, z);
				if (x == (int)end.xCoord) { return; }

				if (deltay >= 0) {
					y += signy;
					deltay -= ax;
				}

				if (deltaz >= 0) {
					z += signz;
					deltaz -= ax;
				}

				x += signx;
				deltay += ay;
				deltaz += az;
			}
		} else if (ay >= Math.max(ax, az)) {
			deltax = ax - (ay >> 1);
			deltaz = az - (ay >> 1);
			while (true) {
				shapeable.setBlock(x, y, z);
				if (y == (int)end.yCoord) { return; }

				if (deltax >= 0) {
					x += signx;
					deltax -= ay;
				}

				if (deltaz >= 0) {
					z += signz;
					deltaz -= ay;
				}

				y += signy;
				deltax += ax;
				deltaz += az;
			}
		} else if (az >= Math.max(ax, ay)) {
			deltax = ax - (az >> 1);
			deltay = ay - (az >> 1);
			while (true) {
				shapeable.setBlock(x, y, z);
				if (z == (int)end.zCoord) { return; }

				if (deltax >= 0) {
					x += signx;
					deltax -= az;
				}

				if (deltay >= 0) {
					y += signy;
					deltay -= az;
				}

				z += signz;
				deltax += ax;
				deltay += ay;
			}
		}
	}
}
