package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.Config;
import openblocks.OpenBlocks;
import openblocks.common.block.BlockCanvas;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.utils.render.PaintUtils;

public class ItemWallpaper extends Item {

	public ItemWallpaper() {
		super(Config.itemWallpaperId);
		setMaxStackSize(1);
		setCreativeTab(OpenBlocks.tabOpenBlocks);
	}

	public static void setDataName(ItemStack stack, String mapName) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", mapName);
		stack.setTagCompound(tag);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		if (!stack.hasTagCompound()) {
			return false;
		}
		
		NBTTagCompound tag = stack.getTagCompound();
		if (!tag.hasKey("name")) {
			return false;
		}
		
		String textureName = tag.getString("name");
		
		if (PaintUtils.instance.isAllowedToReplace(world, x, y, z)) {
			BlockCanvas.replaceBlock(world, x, y, z);
		}

		TileEntity te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			try {
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}
			
			if (canvas.useWallpaper(side, textureName)) stack.stackSize--;
			return true;
		}

		return false;
	}
}
