package openblocks.common.tileentity;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginaryPencil;

public class TileEntityImaginaryPencil extends TileEntityImaginary {

	@Override
	public boolean isAlwaysSolid() {
		return true;
	}

	@Override
	@Nonnull
	public ItemStack getPickBlock(PlayerEntity player) {
		return ItemImaginaryPencil.setupValues(new ItemStack(getBlockType(), 1), shape, isInverted, ItemImaginary.DEFAULT_USE_COUNT);
	}

	@Override public int getColor() {
		return 0xFFFFFF;
	}
}
