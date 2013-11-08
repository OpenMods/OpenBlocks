package openblocks.common.block;

import java.util.ArrayList;

import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntitySpecialStainedClay;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSpecialStainedClay extends OpenBlock {

	public BlockSpecialStainedClay() {
		super(Config.blockSpecialStainedClayId, Material.clay);
		setupBlock(this, "specialStainedClay", TileEntitySpecialStainedClay.class);
	}

	@Override
	public boolean shouldRenderBlock() {
		return true;
	}
	
	@Override
    public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ItemStack stack = new ItemStack(OpenBlocks.Blocks.specialStainedClay);
        TileEntitySpecialStainedClay tile = getTileEntity(world, x, y, z, TileEntitySpecialStainedClay.class);
	    if (tile != null) {
        	writeColorToNBT(stack, tile.getColor());
        }
        ret.add(stack);
        return ret;
    }

    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
    	TileEntitySpecialStainedClay tile = this.getTileEntity(world, x, y, z, TileEntitySpecialStainedClay.class);
    	if (tile != null) {
    		return tile.getColor();
    	}
        return 16777215;
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
