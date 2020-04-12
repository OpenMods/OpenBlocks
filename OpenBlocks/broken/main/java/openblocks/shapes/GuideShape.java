package openblocks.shapes;

import openmods.shapes.IShapeGenerator;
import openmods.shapes.ShapeAxesGenerator;
import openmods.shapes.ShapeCuboidGenerator;
import openmods.shapes.ShapeCylinderGenerator;
import openmods.shapes.ShapeEquilateral2dGenerator;
import openmods.shapes.ShapePlanesGenerator;
import openmods.shapes.ShapeSphereGenerator;
import openmods.utils.TranslationUtils;
import openmods.utils.render.GeometryUtils.Octant;

public enum GuideShape {
	Sphere(new ShapeSphereGenerator(Octant.ALL), "sphere"),
	Cylinder(new ShapeCylinderGenerator(), "cylinder"),
	Cuboid(new ShapeCuboidGenerator(ShapeCuboidGenerator.Elements.EDGES), "cuboid"),
	FullCuboid(new ShapeCuboidGenerator(ShapeCuboidGenerator.Elements.WALLS), "full_cuboid"),
	Dome(new ShapeSphereGenerator(Octant.SOUTH), "dome"),
	Triangle(new ShapeEquilateral2dGenerator(3), "triangle"),
	Pentagon(new ShapeEquilateral2dGenerator(5), "pentagon"),
	Hexagon(new ShapeEquilateral2dGenerator(6), "hexagon"),
	Octagon(new ShapeEquilateral2dGenerator(8), "octagon"),
	Axes(new ShapeAxesGenerator(), "axes"),
	Planes(new ShapePlanesGenerator(), "planes");

	public final String unlocalizedName;
	public final IShapeGenerator generator;

	GuideShape(IShapeGenerator generator, String name) {
		this.unlocalizedName = "openblocks.misc.shape." + name;
		this.generator = generator;
	}

	public String getLocalizedName() {
		return TranslationUtils.translateToLocal(unlocalizedName);
	}

	public static final GuideShape[] VALUES = values();
}