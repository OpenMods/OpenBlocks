package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPointable {
	void onPointingStart(ItemStack itemStack, EntityPlayer player);

	void onPointingEnd(ItemStack itemStack, EntityPlayer player, int x, int y, int z);
}
