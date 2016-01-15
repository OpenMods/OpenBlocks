package openblocks.integration;

import java.util.Map;

import net.minecraft.util.Vec3i;
import net.minecraft.village.Village;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openperipheral.api.adapter.IPeripheralAdapter;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;

import com.google.common.collect.Maps;

public class AdapterVillageHighlighter implements IPeripheralAdapter {

	@Override
	public Class<?> getTargetClass() {
		return TileEntityVillageHighlighter.class;
	}

	@Override
	public String getSourceId() {
		return "openblocks_village";
	}

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get information about the villages this block is inside")
	public Map<?, ?> getVillages(TileEntityVillageHighlighter vh) {
		Map<Integer, Object> map = Maps.newHashMap();
		int i = 1;
		for (Village village : vh.getWorld().villageCollectionObj.getVillageList()) {
			if (village.func_179866_a(vh.getPos())) {
				Map<String, Object> villageMap = Maps.newHashMap();
				Vec3i d = village.getCenter().subtract(vh.getPos());
				villageMap.put("x", d.getX());
				villageMap.put("y", d.getY());
				villageMap.put("z", d.getZ());
				villageMap.put("doors", village.getNumVillageDoors());
				villageMap.put("villagers", village.getNumVillagers());
				villageMap.put("radius", village.getVillageRadius());
				map.put(i++, villageMap);
			}
		}
		return map;
	}

}
