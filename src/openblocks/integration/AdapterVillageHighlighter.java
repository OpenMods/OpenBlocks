package openblocks.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.village.Village;
import openblocks.common.tileentity.TileEntityVillageHighlighter;
import openperipheral.api.IPeripheralAdapter;
import openperipheral.api.LuaMethod;
import openperipheral.api.LuaType;
import dan200.computer.api.IComputerAccess;

public class AdapterVillageHighlighter implements IPeripheralAdapter {

	@SuppressWarnings("rawtypes")
	@Override
	public Class getTargetClass() {
		return TileEntityVillageHighlighter.class;
	}

	/***
	 * 
	 * @param computer
	 *            the computer
	 * @param vh
	 *            the tile
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@LuaMethod(onTick = true, returnType = LuaType.TABLE, description = "Get information about the villages this block is inside")
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
