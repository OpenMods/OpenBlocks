package openblocks.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import openblocks.OpenBlocks;
import openblocks.common.Stencil;
import openblocks.common.block.BlockCanvas;
import openblocks.common.tileentity.TileEntityCanvas;
import openmods.utils.render.PaintUtils;

public class ItemStencil extends Item {

	public ItemStencil() {
		setCreativeTab(OpenBlocks.tabOpenBlocks);
		setHasSubtypes(true);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) {
		for (Stencil stencil : Stencil.values()) {
			list.add(new ItemStack(item, 1, stencil.ordinal()));
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {

		if (PaintUtils.instance.isAllowedToReplace(world, pos)) {
			BlockCanvas.replaceBlock(world, pos);
		}

		TileEntity te = world.getTileEntity(pos);

		if (te instanceof TileEntityCanvas) {
			TileEntityCanvas canvas = (TileEntityCanvas)te;
			int stencilId = stack.getItemDamage();
			Stencil stencil;
			try {
				stencil = Stencil.VALUES[stencilId];
			} catch (ArrayIndexOutOfBoundsException e) {
				return false;
			}

			if (canvas.useStencil(side, stencil)) stack.stackSize--;
			return true;
		}

		return false;
	}

}
