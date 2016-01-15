package openblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

public interface IPointable {
	void onPointingStart(ItemStack itemStack, EntityPlayer player);

	void onPointingEnd(ItemStack itemStack, EntityPlayer player, BlockPos pos);
}
