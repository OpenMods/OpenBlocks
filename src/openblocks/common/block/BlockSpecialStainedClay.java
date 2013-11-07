package openblocks.common.block;

import openblocks.Config;
import openblocks.common.tileentity.TileEntitySpecialStainedClay;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BlockSpecialStainedClay extends OpenBlock {

	public BlockSpecialStainedClay() {
		super(Config.blockSpecialStainedClayId, Material.clay);
		setupBlock(this, "specialStainedClay", TileEntitySpecialStainedClay.class);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}

	public static void writeColorToNBT(ItemStack itemStack, int color) {
		if (itemStack == null) {
			return;
		}
		NBTTagCompound tag = itemStack.getTagCompound();;
		if (tag == null) {
			tag = new NBTTagCompound();
			itemStack.setTagCompound(tag);
		}
		tag.setInteger("color", color);
	}

	public static int getColorFromNBT(ItemStack itemStack) {
		if (itemStack != null && itemStack.hasTagCompound()) {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag.hasKey("color")) {
				return tag.getInteger("color");
			}
		}
		return -1;
	}

}
