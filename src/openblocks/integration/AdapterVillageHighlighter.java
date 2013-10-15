package openblocks.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.village.Village;

import dan200.computer.api.IComputerAccess;

import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;

public class AdapterVillageHighlighter implements IPeripheralAdapter {

	@Override
	public Class getTargetClass() {
		return TileEntityVillageHighlighter.class;
	}
	
	@SuppressWarnings("unchecked")
	@LuaMethod
	public Map getVillages(IComputerAccess computer, TileEntityVillageHighlighter vh) {
		Map map = new HashMap();
		int i = 1;
		for (Village village : (List<Village>)vh.worldObj.villageCollectionObj.getVillageList()) {
			if (village.isInRange(vh.xCoord, vh.yCoord, vh.zCoord)) {
				Map villageMap = new HashMap();
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
