package jadedladder.shapes;

import java.util.HashMap;
import java.util.Map;

import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;

public class ShapeFactory {

	private static Map<Mode, IShapeGenerator> _shapeMap;// = new Map<Mode, IShapeGenerator>
	
	static {
		_shapeMap = new HashMap<ShapeFactory.Mode, IShapeGenerator>();
		_shapeMap.put(Mode.Sphere, new ShapeSphereGenerator());
		_shapeMap.put(Mode.Cylinder, new ShapeCylinderGenerator());
	}
	
	public static int generateShape(double xSize, double ySize, double zSize, IShapeable shapeable, Mode mode){
		if(!_shapeMap.containsKey(mode)) return 0; // Unavailable shape
		System.out.println(String.format("%s,%s,%s : %s", xSize, ySize, zSize, mode.toString()));
		return _shapeMap.get(mode).generateShape(xSize, ySize, zSize, shapeable);
	}
	
	public enum Mode {
		Sphere, Cube, Pyramid, Cylinder, Car, HousesOfParliment, ACat
	}
}
