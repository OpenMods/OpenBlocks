package openblocks.integration;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedBytes;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import openblocks.common.HeightMapData;
import openblocks.common.HeightMapData.LayerData;
import openblocks.common.tileentity.TileEntityProjector;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.IPeripheralAdapter;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.ArgType;
import openperipheral.api.adapter.method.IMultiReturn;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.architecture.FeatureGroup;
import openperipheral.api.helpers.MultiReturn;
import org.apache.commons.lang3.ArrayUtils;

@FeatureGroup("openblocks-projector")
public class AdapterProjector implements IPeripheralAdapter {

	@Override
	public String getSourceId() {
		return "openblocks_projector";
	}

	private static int toInt(Object object) {
		return ((Number)object).intValue();
	}

	@Override
	public Class<?> getTargetClass() {
		return TileEntityProjector.class;
	}

	@ScriptCallable(returnTypes = ReturnType.NUMBER, description = "Get current map id")
	public Integer getMapId(TileEntityProjector projector) {
		int mapId = projector.mapId();
		return (mapId >= 0)? mapId : null;
	}

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get current map info")
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

	@ScriptCallable(description = "Set current map info")
	public void setMapInfo(TileEntityProjector projector, @Arg(name = "properties", description = "Map of properties", type = ArgType.TABLE) Map<String, Object> args) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data != null && data.isValid(), "Map not loaded");

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
				Map<?, ?> layers = (Map<?, ?>)value;
				for (Map.Entry<?, ?> e : layers.entrySet()) {
					int index = toInt(e.getKey());
					Preconditions.checkElementIndex(index, data.layers.length, "layer index");

					LayerData layerData = data.layers[index];
					layerData.alpha = (byte)toInt(e.getValue());
				}
			}
		}

		projector.markMapDirty();
	}

	@ScriptCallable(returnTypes = ReturnType.NUMBER, description = "Get displayed map rotation")
	public int getRotation(TileEntityProjector projector) {
		return projector.rotation();
	}

	@ScriptCallable(description = "Rotate displayed map rotation")
	public void rotate(TileEntityProjector projector,
			@Arg(name = "delta", description = "Rotation delta (positive - CW, negative - CCW)") int delta) {
		projector.rotate(delta);
	}

	@Asynchronous
	@ScriptCallable(returnTypes = { ReturnType.NUMBER, ReturnType.NUMBER }, description = "Get height and color of point on map")
	public IMultiReturn getPoint(TileEntityProjector projector,
			@Arg(name = "row", description = "Map row (0..63)") int row,
			@Arg(name = "column", description = "Map column (0..63)") int column,
			@Arg(name = "layer", description = "Map layer") int layer) {
		Preconditions.checkElementIndex(row, 64, "row");
		Preconditions.checkElementIndex(column, 64, "column");

		HeightMapData data = projector.getMap();
		Preconditions.checkState(data != null && data.isValid(), "Map not loaded");

		Preconditions.checkElementIndex(layer, data.layers.length, "layer");
		int index = 64 * row + column;
		LayerData layerData = data.layers[layer];
		return MultiReturn.wrap(UnsignedBytes.toInt(layerData.heightMap[index]), layerData.colorMap[index]);
	}

	@Asynchronous
	@ScriptCallable(description = "Get height and color of point on map")
	public void setPoint(TileEntityProjector projector,
			@Arg(name = "row", description = "Map row (0..63)") int row,
			@Arg(name = "column", description = "Map column (0..63)") int column,
			@Arg(name = "layer", description = "Map layer") int layer,
			@Arg(name = "height", description = "Point height") int height,
			@Arg(name = "color", description = "Point color ") int color) {
		Preconditions.checkElementIndex(row, 64, "row");
		Preconditions.checkElementIndex(column, 64, "column");
		Preconditions.checkElementIndex(height, 256, "height");
		Preconditions.checkElementIndex(color, MapColor.COLORS.length, "color");

		HeightMapData data = projector.getMap();
		Preconditions.checkState(data != null && data.isValid(), "Map not loaded");

		Preconditions.checkElementIndex(layer, data.layers.length, "layer");
		int index = 64 * row + column;
		LayerData layerData = data.layers[layer];
		layerData.heightMap[index] = (byte)height;
		layerData.colorMap[index] = (byte)color;
		projector.markMapDirty();
	}

	@ScriptCallable(description = "Clear map")
	public void clearMap(TileEntityProjector projector) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");
		data.layers = new LayerData[0];
		projector.markMapDirty();
	}

	@ScriptCallable(description = "Clear single layer")
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

	@ScriptCallable(description = "Append layer")
	public void appendLayer(TileEntityProjector projector) {
		HeightMapData data = projector.getMap();
		Preconditions.checkState(data.isValid(), "Map not loaded");
		LayerData newLayer = new LayerData();
		newLayer.alpha = (byte)255;
		data.layers = ArrayUtils.add(data.layers, newLayer);
		projector.markMapDirty();
	}
}
