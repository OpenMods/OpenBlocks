package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IPointable {
	void onPoint(ItemStack itemStack, EntityPlayer player, int x, int y, int z);
}
