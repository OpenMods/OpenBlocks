package openblocks.api;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IPointable {
	void onPointingStart(@Nonnull ItemStack itemStack, EntityPlayer player);

	void onPointingEnd(@Nonnull ItemStack itemStack, EntityPlayer player, BlockPos pos);
}
