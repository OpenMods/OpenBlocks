package openblocks.shapes;

import openmods.shapes.*;

public enum GuideShape {
	Sphere(false, new ShapeSphereGenerator()),
	Cylinder(false,
			new ShapeCylinderGenerator()),
	Cuboid(false,
			new ShapeCuboidGenerator()),
	Dome(false, new ShapeDomeGenerator()),
	Triangle(
			true, new ShapeEquilateral2dGenerator(3)),
	Pentagon(true,
			new ShapeEquilateral2dGenerator(5)),
	Hexagon(true,
			new ShapeEquilateral2dGenerator(6)),
	Octagon(true,
			new ShapeEquilateral2dGenerator(8));

	private final String displayName;
	public final boolean fixedRatio;
	public final IShapeGenerator generator;

	private GuideShape(boolean fixedRatio, IShapeGenerator generator) {
		this(null, fixedRatio, generator);
	}

	private GuideShape(String displayName, boolean fixedRatio, IShapeGenerator generator) {
		this.displayName = displayName;
		this.fixedRatio = fixedRatio;
		this.generator = generator;
	}

	public String getDisplayName() {
		return displayName == null? name() : displayName;
	}
}