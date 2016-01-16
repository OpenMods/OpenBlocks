package openblocks.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.infobook.BookDocumentation;

@BookDocumentation(hasVideo = true)
public class ItemSqueegee extends Item {

	public ItemSqueegee() {
		setHasSubtypes(true);
		setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			if (player.isSneaking()) canvas.removePaint(EnumFacing.VALUES);
			else canvas.removePaint(side);
			world.playSoundAtEntity(player, "openblocks:squeegee.use", 1, 1);
			return true;
		}
		return false;
	}
}
