package openblocks.api;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IPointable {
	void onPointingStart(@Nonnull ItemStack itemStack, PlayerEntity player);

	void onPointingEnd(@Nonnull ItemStack itemStack, PlayerEntity player, BlockPos pos);
}
