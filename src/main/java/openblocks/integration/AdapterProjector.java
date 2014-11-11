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

	@LuaCallable(returnTypes = LuaReturnType.NUMBER, description = "Get current map id")
	public Integer getMapId(TileEntityProjector projector) {
		int mapId = projector.mapId();
		return (mapId >= 0)? mapId : null;
	}

	@LuaCallable(returnTypes = LuaReturnType.TABLE, description = "Get current map info")
	public Map<String, Object> getMapInfo(final TileEntityProjector projector) {
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

	@LuaCallable(description = "Set current map info")
	public void setMapInfo(TileEntityProjector projector, @Arg(name = "properties", description = "Map of properties") Map<String, Object> args) {
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

	@LuaCallable(returnTypes = LuaReturnType.NUMBER, description = "Get displayed map rotation")
	public int getRotation(TileEntityProjector projector) {
		return projector.rotation();
	}

	@LuaCallable(description = "Rotate displayed map rotation")
	public void rotate(TileEntityProjector projector,
			@Arg(name = "delta", description = "Rotation delta (positive - CW, negative - CCW)") int delta) {
		projector.rotate(delta);
	}

	@Asynchronous
	@LuaCallable(returnTypes = LuaReturnType.NUMBER, description = "Get height and color of point on map")
	public IMultiReturn getPoint(TileEntityProjector projector,
			@Arg(name = "row", description = "Map row (0..63)") int row,
			@Arg(name = "column", description = "Map column (0..63)") int column,
			@Arg(name = "layer", description = "Map layer") int layer) {
		Preconditions.checkElementIndex(row, 64, "row");
		Preconditions.checkElementIndex(column, 64, "column");

		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");

		Preconditions.checkElementIndex(layer, data.layers.length, "layer");
		int index = 64 * row + column;
		LayerData layerData = data.layers[layer];
		return wrap(UnsignedBytes.toInt(layerData.heightMap[index]), layerData.colorMap[index]);
	}

	@Asynchronous
	@LuaCallable(description = "Get height and color of point on map")
	public void setPoint(TileEntityProjector projector,
			@Arg(name = "row", description = "Map row (0..63)") int row,
			@Arg(name = "column", description = "Map column (0..63)") int column,
			@Arg(name = "layer", description = "Map layer") int layer,
			@Arg(name = "height", description = "Point height") int height,
			@Arg(name = "color", description = "Point color ") int color) {
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

	@LuaCallable(description = "Clear map")
	public void clearMap(TileEntityProjector projector) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");
		data.layers = new LayerData[0];
		projector.markMapDirty();
	}

	@LuaCallable(description = "Clear single layer")
	public void clearLayer(TileEntityProjector projector,
			@Arg(name = "layer", description = "Map layer") int layer) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");
		Preconditions.checkElementIndex(layer, data.layers.length, "layer");

		LayerData newLayer = new LayerData();
		newLayer.alpha = (byte)255;
		data.layers[layer] = newLayer;
		projector.markMapDirty();
	}

	@LuaCallable(description = "Append layer")
	public void appendLayer(TileEntityProjector projector) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");
		LayerData newLayer = new LayerData();
		newLayer.alpha = (byte)255;
		data.layers = ArrayUtils.add(data.layers, newLayer);
		projector.markMapDirty();
	}
}
