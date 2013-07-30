package openblocks.shapes;

import java.util.HashMap;
import java.util.Map;

public class ShapeFactory {

	public enum Mode {
		Sphere(false), Cylinder(false), Cuboid(false), Dome(false), Triangle(
				true), Pentagon(true), Hexagon(true), Octagon(true);

		private String displayName;
		private boolean fixedRatio = false;

		Mode(boolean fixedRatio) {
			this(null, fixedRatio);
		}

		Mode(String displayName, boolean fixedRatio) {
			this.displayName = displayName;
			this.fixedRatio = fixedRatio;
		}

		public String getDisplayName() {
			return displayName == null ? name() : displayName;
		}

		public boolean isFixedRatio() {
			return fixedRatio;
		}
	}

	private static Map<Mode, IShapeGenerator> _shapeMap; // = new Map<Mode,
															// IShapeGenerator>

	static {
		_shapeMap = new HashMap<ShapeFactory.Mode, IShapeGenerator>();
		_shapeMap.put(Mode.Sphere, new ShapeSphereGenerator());
		_shapeMap.put(Mode.Cylinder, new ShapeCylinderGenerator());
		_shapeMap.put(Mode.Cuboid, new ShapeCuboidGenerator());
		_shapeMap.put(Mode.Dome, new ShapeDomeGenerator());
		_shapeMap.put(Mode.Triangle, new ShapeEquilateral2dGenerator(3));
		_shapeMap.put(Mode.Pentagon, new ShapeEquilateral2dGenerator(5));
		_shapeMap.put(Mode.Hexagon, new ShapeEquilateral2dGenerator(6));
		_shapeMap.put(Mode.Octagon, new ShapeEquilateral2dGenerator(8));
	}

	public static void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable, Mode mode) {
		if (!_shapeMap.containsKey(mode)) return; // Unavailable shape
		System.out.println(String.format("%s,%s,%s : %s", xSize, ySize, zSize, mode.toString()));
		_shapeMap.get(mode).generateShape(xSize, ySize, zSize, shapeable);
	}
}
