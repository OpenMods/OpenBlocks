package openblocks.integration;

import java.util.Map;

import net.minecraft.block.material.MapColor;
import openblocks.common.HeightMapData;
import openblocks.common.HeightMapData.LayerData;
import openblocks.common.tileentity.TileEntityProjector;
import openperipheral.api.*;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedBytes;

import dan200.computer.api.IComputerAccess;

public class AdapterProjector implements IPeripheralAdapter {

	private static IMultiReturn wrap(final Object... values) {
		return new IMultiReturn() {
			@Override
			public Object[] getObjects() {
				return values;
			}
		};
	}

	private static int toInt(Object object) {
		return ((Number)object).intValue();
	}

	@Override
	public Class<?> getTargetClass() {
		return TileEntityProjector.class;
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get current map id")
	public Integer getMapId(IComputerAccess computer, final TileEntityProjector projector) {
		int mapId = projector.mapId();
		return (mapId >= 0)? mapId : null;
	}

	@LuaMethod(returnType = LuaType.TABLE, description = "Get current map info")
	public Map<String, Object> getMapInfo(IComputerAccess computer, final TileEntityProjector projector) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");

		Map<String, Object> result = Maps.newHashMap();
		result.put("center_x", data.centerX);
		result.put("center_z", data.centerZ);
		result.put("scale", data.scale);
		result.put("dimension", data.dimension);

		Map<Integer, Integer> layers = Maps.newHashMap();

		for (int i = 0; i < data.layers.length; i++) {
			LayerData layerData = data.layers[i];
			layers.put(i, UnsignedBytes.toInt(layerData.alpha));
		}

		result.put("layers", layers);
		return result;
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Set current map info", args = {
			@Arg(name = "properties", description = "Map of properties", type = LuaType.OBJECT),
	})
	public void setMapInfo(IComputerAccess computer, final TileEntityProjector projector, Map<String, Object> args) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");

		{
			Object value = args.get("center_x");
			if (value != null) data.centerX = toInt(value);
		}

		{
			Object value = args.get("center_z");
			if (value != null) data.centerZ = toInt(value);
		}

		{
			Object value = args.get("dimension");
			if (value != null) data.dimension = toInt(value);
		}

		{
			Object value = args.get("layers");
			if (value != null) {
				@SuppressWarnings("unchecked")
				Map<Integer, Integer> layers = (Map<Integer, Integer>)value;
				for (Map.Entry<Integer, Integer> e : layers.entrySet()) {
					int index = toInt(e.getKey());
					Preconditions.checkElementIndex(index, data.layers.length, "layer index");

					LayerData layerData = data.layers[index];
					layerData.alpha = (byte)toInt(e.getValue());
				}
			}
		}

		projector.markMapDirty();
	}

	@LuaMethod(returnType = LuaType.NUMBER, description = "Get displayed map rotation")
	public int getRotation(IComputerAccess computer, final TileEntityProjector projector) {
		return projector.rotation();
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Rotate displayed map rotation", args = {
			@Arg(name = "delta", description = "Rotation delta (positive - CW, negative - CCW)", type = LuaType.NUMBER)
	})
	public void rotate(IComputerAccess computer, final TileEntityProjector projector, int delta) {
		projector.rotate(delta);
	}

	@LuaMethod(onTick = false, returnType = LuaType.NUMBER, description = "Get height and color of point on map", args = {
			@Arg(name = "row", description = "Map row (0..63)", type = LuaType.NUMBER),
			@Arg(name = "column", description = "Map column (0..63)", type = LuaType.NUMBER),
			@Arg(name = "layer", description = "Map layer", type = LuaType.NUMBER)
	})
	public IMultiReturn getPoint(IComputerAccess computer, final TileEntityProjector projector, int row, int column, int layer) {
		Preconditions.checkElementIndex(row, 64, "row");
		Preconditions.checkElementIndex(column, 64, "column");

		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");

		Preconditions.checkElementIndex(layer, data.layers.length, "layer");
		int index = 64 * row + column;
		LayerData layerData = data.layers[layer];
		return wrap(UnsignedBytes.toInt(layerData.heightMap[index]), layerData.colorMap[index]);
	}

	@LuaMethod(onTick = false, returnType = LuaType.NUMBER, description = "Get height and color of point on map", args = {
			@Arg(name = "row", description = "Map row (0..63)", type = LuaType.NUMBER),
			@Arg(name = "column", description = "Map column (0..63)", type = LuaType.NUMBER),
			@Arg(name = "layer", description = "Map layer", type = LuaType.NUMBER),
			@Arg(name = "height", description = "Point color", type = LuaType.NUMBER),
			@Arg(name = "color", description = "Point height", type = LuaType.NUMBER)
	})
	public void setPoint(IComputerAccess computer, final TileEntityProjector projector, int row, int column, int layer, int height, int color) {
		Preconditions.checkElementIndex(row, 64, "row");
		Preconditions.checkElementIndex(column, 64, "column");
		Preconditions.checkElementIndex(height, 256, "height");
		Preconditions.checkElementIndex(color, MapColor.mapColorArray.length, "color");

		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");

		Preconditions.checkElementIndex(layer, data.layers.length, "layer");
		int index = 64 * row + column;
		LayerData layerData = data.layers[layer];
		layerData.heightMap[index] = (byte)height;
		layerData.colorMap[index] = (byte)color;
		projector.markMapDirty();
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Clear map")
	public void clearMap(IComputerAccess computer, final TileEntityProjector projector) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");
		data.layers = new LayerData[0];
		projector.markMapDirty();
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Clear single layer", args = {
			@Arg(name = "layer", description = "Map layer", type = LuaType.NUMBER)
	})
	public void clearLayer(IComputerAccess computer, final TileEntityProjector projector, int layer) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");
		Preconditions.checkElementIndex(layer, data.layers.length, "layer");

		LayerData newLayer = new LayerData();
		newLayer.alpha = (byte)255;
		data.layers[layer] = newLayer;
		projector.markMapDirty();
	}

	@LuaMethod(returnType = LuaType.VOID, description = "Append layer")
	public void appendLayer(IComputerAccess computer, final TileEntityProjector projector) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");
		LayerData newLayer = new LayerData();
		newLayer.alpha = (byte)255;
		data.layers = ArrayUtils.add(data.layers, newLayer);
		projector.markMapDirty();
	}
}
