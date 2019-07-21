package openblocks.common.tileentity;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import openblocks.common.block.BlockImaginary;
import openblocks.common.item.ItemImaginary;
import openblocks.common.item.ItemImaginaryCrayon;

public class TileEntityImaginaryCrayon extends TileEntityImaginary {

	private int color;

	@Override public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		color = tag.getInteger("Color");
	}

	@Override public CompoundNBT writeToNBT(CompoundNBT tag) {
		super.writeToNBT(tag);
		tag.setInteger("Color", color);
		return tag;
	}

	public void setup(boolean isInverted, BlockImaginary.Shape shape, int color) {
		super.setup(isInverted, shape);
		this.color = color;
	}

	@Override
	public boolean isAlwaysSolid() {
		return false;
	}

	@Override
	@Nonnull
	public ItemStack getPickBlock(PlayerEntity player) {
		return ItemImaginaryCrayon.setupValues(new ItemStack(getBlockType(), 1), color, shape, isInverted, ItemImaginary.DEFAULT_USE_COUNT);
	}

	public int getColor() {
		return color;
	}
}
