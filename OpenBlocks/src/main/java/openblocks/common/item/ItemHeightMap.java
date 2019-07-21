package openblocks.common.item;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openblocks.common.HeightMapData;
import openblocks.common.MapDataManager;

public class ItemHeightMap extends Item {

	public static final String TAG_MAP_ID = "MapId";

	public ItemHeightMap() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World world, List<String> result, ITooltipFlag flag) {
		if (world == null) return;
		int mapId = getMapId(stack);
		HeightMapData data = MapDataManager.getMapData(world, mapId);

		if (data.isValid()) {
			result.add(String.format("Center X: %d", data.centerX));
			result.add(String.format("Center Z: %d", data.centerZ));
			result.add(String.format("Scale: 1:%d", 1 << data.scale));
		} else if (data.isEmpty()) {
			MapDataManager.requestMapData(world, mapId);
		}
	}

	public static int getMapId(final ItemStack map) {
		final CompoundNBT tag = map.getTagCompound();
		return tag != null ? tag.getInteger(TAG_MAP_ID) : 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {}

}
