package jadedladder.shapes;

import java.util.HashMap;
import java.util.Map;

import jadedladder.common.IShapeGenerator;
import jadedladder.common.IShapeable;

public class ShapeFactory {

	public enum Mode {
		Sphere("Sphere"),
		Cylinder("Cylinder");
		
		private String displayName;
		
		Mode(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	private static Map<Mode, IShapeGenerator> _shapeMap;// = new Map<Mode, IShapeGenerator>
	
	static {
		_shapeMap = new HashMap<ShapeFactory.Mode, IShapeGenerator>();
		_shapeMap.put(Mode.Sphere, new ShapeSphereGenerator());
		_shapeMap.put(Mode.Cylinder, new ShapeCylinderGenerator());
	}
	
	public static void generateShape(int xSize, int ySize, int zSize, IShapeable shapeable, Mode mode){
		if(!_shapeMap.containsKey(mode)) return; // Unavailable shape
		System.out.println(String.format("%s,%s,%s : %s", xSize, ySize, zSize, mode.toString()));
		_shapeMap.get(mode).generateShape(xSize, ySize, zSize, shapeable);
	}
}
