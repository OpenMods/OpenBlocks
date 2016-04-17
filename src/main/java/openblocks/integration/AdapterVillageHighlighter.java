package openblocks.integration;

import java.util.List;
import java.util.Map;

import net.minecraft.village.Village;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openperipheral.api.adapter.IPeripheralAdapter;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.architecture.FeatureGroup;

import com.google.common.collect.Maps;

@FeatureGroup("openblocks-village-highlighter")
public class AdapterVillageHighlighter implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityVillageHighlighter.class;
	}

	@Override
	public String getSourceId() {
		return "openblocks_village";
	}

	@SuppressWarnings({ "unchecked" })
	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get information about the villages this block is inside")
	public Map<?, ?> getVillages(TileEntityVillageHighlighter vh) {
		Map<Integer, Object> map = Maps.newHashMap();
		int i = 1;
		for (Village village : (List<Village>)vh.getWorldObj().villageCollectionObj.getVillageList()) {
			if (village.isInRange(vh.xCoord, vh.yCoord, vh.zCoord)) {
				Map<String, Object> villageMap = Maps.newHashMap();
				villageMap.put("x", village.getCenter().posX - vh.xCoord);
				villageMap.put("y", village.getCenter().posY - vh.yCoord);
				villageMap.put("z", village.getCenter().posZ - vh.zCoord);
				villageMap.put("doors", village.getNumVillageDoors());
				villageMap.put("villagers", village.getNumVillagers());
				villageMap.put("radius", village.getVillageRadius());
				map.put(i++, villageMap);
			}
		}
		return map;
	}

}
