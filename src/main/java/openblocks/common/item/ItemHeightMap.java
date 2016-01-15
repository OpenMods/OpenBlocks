package openblocks.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;

public class ItemHeightMap extends Item {

	public ItemHeightMap() {
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, EntityPlayer player, List<String> result, boolean extended) {
		int mapId = item.getItemDamage();
		HeightMapData data = MapDataManager.getMapData(player.worldObj, mapId);

		if (data.isValid()) {
			result.add(String.format("Center X: %d", data.centerX));
			result.add(String.format("Center Z: %d", data.centerZ));
			result.add(String.format("Scale: 1:%d", 1 << data.scale));
		} else if (data.isEmpty()) {
			MapDataManager.requestMapData(player.worldObj, mapId);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {}

}
