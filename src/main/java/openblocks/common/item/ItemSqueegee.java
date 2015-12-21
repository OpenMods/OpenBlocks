package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.infobook.BookDocumentation;

@BookDocumentation
public class ItemSqueegee extends Item {

	public ItemSqueegee() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(x, y, z);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			if (player.isSneaking()) canvas.removePaint(TileEntityCanvas.ALL_SIDES);
			else canvas.removePaint(side);
			world.playSoundAtEntity(player, "openblocks:squeegee.use", 1, 1);
			return true;
		}
		return false;
	}
}
