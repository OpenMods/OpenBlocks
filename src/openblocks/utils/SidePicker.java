package openblocks.utils;

import java.util.Map;

import net.minecraft.util.Vec3;
import net.minecraftforge.common.ForgeDirection;

import org.lwjgl.input.Mouse;

import com.google.common.collect.Maps;

public class SidePicker {

	public enum Side {
		XNeg,
		XPos,
		YNeg,
		YPos,
		ZNeg,
		ZPos;

		public static Side fromForgeDirection(ForgeDirection dir) {
			switch (dir) {
				case WEST:
					return XNeg;
				case EAST:
					return XPos;
				case DOWN:
					return YNeg;
				case UP:
					return YPos;
				case NORTH:
					return ZNeg;
				case SOUTH:
					return ZPos;
				default:
					break;
			}
			return null;
		}

		public ForgeDirection toForgeDirection() {
			switch (this) {
				case XNeg:
					return ForgeDirection.WEST;
				case XPos:
					return ForgeDirection.EAST;
				case YNeg:
					return ForgeDirection.DOWN;
				case YPos:
					return ForgeDirection.UP;
				case ZNeg:
					return ForgeDirection.NORTH;
				case ZPos:
					return ForgeDirection.SOUTH;
				default:
					return ForgeDirection.UNKNOWN;
			}
		}
	}

	public static class HitCoord {
		public final Side side;
		public final Vec3 coord;

		public HitCoord(Side side, Vec3 coord) {
			this.side = side;
			this.coord = coord;
		}
	}

	private final double negX, negY, negZ;
	private final double posX, posY, posZ;

	public SidePicker(double negX, double negY, double negZ, double posX, double posY, double posZ) {
		this.negX = negX;
		this.negY = negY;
		this.negZ = negZ;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	public SidePicker(double halfSize) {
		negX = negY = negZ = -halfSize;
		posX = posY = posZ = +halfSize;
	}

	private static Vec3 getMouseVector(float z) {
		return OpenGLUtils.unproject(Mouse.getX(), Mouse.getY(), z);
	}

	private Vec3 calculateXPoint(Vec3 near, Vec3 diff, double x) {
		double p = (x - near.xCoord) / diff.xCoord;

		double y = near.yCoord + diff.yCoord * p;
		double z = near.zCoord + diff.zCoord * p;

		if (negY <= y && y <= posY && negZ <= z && z <= posZ) return Vec3.createVectorHelper(x, y, z);

		return null;
	}

	private Vec3 calculateYPoint(Vec3 near, Vec3 diff, double y) {
		double p = (y - near.yCoord) / diff.yCoord;

		double x = near.xCoord + diff.xCoord * p;
		double z = near.zCoord + diff.zCoord * p;

		if (negX <= x && x <= posX && negZ <= z && z <= posZ) return Vec3.createVectorHelper(x, y, z);

		return null;
	}

	private Vec3 calculateZPoint(Vec3 near, Vec3 diff, double z) {
		double p = (z - near.zCoord) / diff.zCoord;

		double x = near.xCoord + diff.xCoord * p;
		double y = near.yCoord + diff.yCoord * p;

		if (negX <= x && x <= posX && negY <= y && y <= posY) return Vec3.createVectorHelper(x, y, z);

		return null;
	}

	private static void addPoint(Map<Side, Vec3> map, Side side, Vec3 value) {
		if (value != null) map.put(side, value);
	}

	private Map<Side, Vec3> calculateHitPoints(Vec3 near, Vec3 far) {
		Vec3 diff = far.subtract(near);

		Map<Side, Vec3> result = Maps.newEnumMap(Side.class);
		addPoint(result, Side.XNeg, calculateXPoint(near, diff, negX));
		addPoint(result, Side.XPos, calculateXPoint(near, diff, posX));

		addPoint(result, Side.YNeg, calculateYPoint(near, diff, negY));
		addPoint(result, Side.YPos, calculateYPoint(near, diff, posY));

		addPoint(result, Side.ZNeg, calculateZPoint(near, diff, negZ));
		addPoint(result, Side.ZPos, calculateZPoint(near, diff, posZ));
		return result;
	}

	public Map<Side, Vec3> calculateMouseHits() {
		OpenGLUtils.updateMatrices();
		Vec3 near = getMouseVector(0);
		Vec3 far = getMouseVector(1);

		return calculateHitPoints(near, far);
	}

	public HitCoord getNearestHit() {
		OpenGLUtils.updateMatrices();
		Vec3 near = getMouseVector(0);
		Vec3 far = getMouseVector(1);

		Map<Side, Vec3> hits = calculateHitPoints(near, far);

		if (hits.isEmpty()) return null;

		Side minSide = null;
		double minDist = Double.MAX_VALUE;

		// yeah, I know there are two entries max, but... meh
		for (Map.Entry<Side, Vec3> e : hits.entrySet()) {
			double dist = e.getValue().subtract(near).lengthVector();
			if (dist < minDist) {
				minDist = dist;
				minSide = e.getKey();
			}
		}

		if (minSide == null) return null; // !?

		return new HitCoord(minSide, hits.get(minSide));
	}

}