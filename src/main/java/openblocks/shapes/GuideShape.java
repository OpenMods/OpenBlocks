package openblocks.shapes;

import net.minecraft.util.StatCollector;
import openmods.shapes.*;
import openmods.utils.render.GeometryUtils.Octant;

public enum GuideShape {
	Sphere(false, new ShapeSphereGenerator(Octant.ALL), "sphere"),
	Cylinder(false, new ShapeCylinderGenerator(), "cylinder"),
	Cuboid(false, new ShapeCuboidGenerator(ShapeCuboidGenerator.Elements.EDGES), "cuboid"),
	FullCuboid(false, new ShapeCuboidGenerator(ShapeCuboidGenerator.Elements.WALLS), "full_cuboid"),
	Dome(false, new ShapeSphereGenerator(Octant.TOP), "dome"),
	Triangle(true, new ShapeEquilateral2dGenerator(3), "triangle"),
	Pentagon(true, new ShapeEquilateral2dGenerator(5), "pentagon"),
	Hexagon(true, new ShapeEquilateral2dGenerator(6), "hexagon"),
	Octagon(true, new ShapeEquilateral2dGenerator(8), "octagon");

	public final String unlocalizedName;
	public final boolean fixedRatio;
	public final IShapeGenerator generator;

	private GuideShape(boolean fixedRatio, IShapeGenerator generator, String name) {
		this.unlocalizedName = "openblocks.misc.shape." + name;
		this.fixedRatio = fixedRatio;
		this.generator = generator;
	}

	public String getLocalizedName() {
		return StatCollector.translateToLocal(unlocalizedName);
	}

	public static final GuideShape[] VALUES = values();
}